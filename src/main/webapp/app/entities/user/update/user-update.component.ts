import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from '../user.model';
import { UserService } from '../service/user.service';
import { UserFormGroup, UserFormService } from './user-form.service';

@Component({
  selector: 'jhi-user-update',
  templateUrl: './user-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class UserUpdateComponent implements OnInit {
  isSaving = false;
  user: IUser | null = null;

  protected userService = inject(UserService);
  protected userFormService = inject(UserFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: UserFormGroup = this.userFormService.createUserFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ user }) => {
      this.user = user;
      if (user) {
        this.updateForm(user);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const user = this.userFormService.getUser(this.editForm);
    if (user.id !== null) {
      this.subscribeToSaveResponse(this.userService.update(user));
    } else {
      this.subscribeToSaveResponse(this.userService.create(user));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUser>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(user: IUser): void {
    this.user = user;
    this.userFormService.resetForm(this.editForm, user);
  }
}
