import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IRole } from 'app/entities/role/role.model';
import { RoleService } from 'app/entities/role/service/role.service';
import { UserRoleService } from '../service/user-role.service';
import { IUserRole } from '../user-role.model';
import { UserRoleFormGroup, UserRoleFormService } from './user-role-form.service';

@Component({
  selector: 'jhi-user-role-update',
  templateUrl: './user-role-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class UserRoleUpdateComponent implements OnInit {
  isSaving = false;
  userRole: IUserRole | null = null;

  usersSharedCollection: IUser[] = [];
  rolesSharedCollection: IRole[] = [];

  protected userRoleService = inject(UserRoleService);
  protected userRoleFormService = inject(UserRoleFormService);
  protected userService = inject(UserService);
  protected roleService = inject(RoleService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: UserRoleFormGroup = this.userRoleFormService.createUserRoleFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareRole = (o1: IRole | null, o2: IRole | null): boolean => this.roleService.compareRole(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userRole }) => {
      this.userRole = userRole;
      if (userRole) {
        this.updateForm(userRole);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userRole = this.userRoleFormService.getUserRole(this.editForm);
    if (userRole.id !== null) {
      this.subscribeToSaveResponse(this.userRoleService.update(userRole));
    } else {
      this.subscribeToSaveResponse(this.userRoleService.create(userRole));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserRole>>): void {
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

  protected updateForm(userRole: IUserRole): void {
    this.userRole = userRole;
    this.userRoleFormService.resetForm(this.editForm, userRole);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, userRole.user);
    this.rolesSharedCollection = this.roleService.addRoleToCollectionIfMissing<IRole>(this.rolesSharedCollection, userRole.role);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.userRole?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.roleService
      .query()
      .pipe(map((res: HttpResponse<IRole[]>) => res.body ?? []))
      .pipe(map((roles: IRole[]) => this.roleService.addRoleToCollectionIfMissing<IRole>(roles, this.userRole?.role)))
      .subscribe((roles: IRole[]) => (this.rolesSharedCollection = roles));
  }
}
