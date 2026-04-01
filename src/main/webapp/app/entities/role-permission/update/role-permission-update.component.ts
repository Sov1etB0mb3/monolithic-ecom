import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IRole } from 'app/entities/role/role.model';
import { RoleService } from 'app/entities/role/service/role.service';
import { IPermission } from 'app/entities/permission/permission.model';
import { PermissionService } from 'app/entities/permission/service/permission.service';
import { RolePermissionService } from '../service/role-permission.service';
import { IRolePermission } from '../role-permission.model';
import { RolePermissionFormGroup, RolePermissionFormService } from './role-permission-form.service';

@Component({
  selector: 'jhi-role-permission-update',
  templateUrl: './role-permission-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class RolePermissionUpdateComponent implements OnInit {
  isSaving = false;
  rolePermission: IRolePermission | null = null;

  rolesSharedCollection: IRole[] = [];
  permissionsSharedCollection: IPermission[] = [];

  protected rolePermissionService = inject(RolePermissionService);
  protected rolePermissionFormService = inject(RolePermissionFormService);
  protected roleService = inject(RoleService);
  protected permissionService = inject(PermissionService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: RolePermissionFormGroup = this.rolePermissionFormService.createRolePermissionFormGroup();

  compareRole = (o1: IRole | null, o2: IRole | null): boolean => this.roleService.compareRole(o1, o2);

  comparePermission = (o1: IPermission | null, o2: IPermission | null): boolean => this.permissionService.comparePermission(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ rolePermission }) => {
      this.rolePermission = rolePermission;
      if (rolePermission) {
        this.updateForm(rolePermission);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const rolePermission = this.rolePermissionFormService.getRolePermission(this.editForm);
    if (rolePermission.id !== null) {
      this.subscribeToSaveResponse(this.rolePermissionService.update(rolePermission));
    } else {
      this.subscribeToSaveResponse(this.rolePermissionService.create(rolePermission));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRolePermission>>): void {
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

  protected updateForm(rolePermission: IRolePermission): void {
    this.rolePermission = rolePermission;
    this.rolePermissionFormService.resetForm(this.editForm, rolePermission);

    this.rolesSharedCollection = this.roleService.addRoleToCollectionIfMissing<IRole>(this.rolesSharedCollection, rolePermission.role);
    this.permissionsSharedCollection = this.permissionService.addPermissionToCollectionIfMissing<IPermission>(
      this.permissionsSharedCollection,
      rolePermission.permission,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.roleService
      .query()
      .pipe(map((res: HttpResponse<IRole[]>) => res.body ?? []))
      .pipe(map((roles: IRole[]) => this.roleService.addRoleToCollectionIfMissing<IRole>(roles, this.rolePermission?.role)))
      .subscribe((roles: IRole[]) => (this.rolesSharedCollection = roles));

    this.permissionService
      .query()
      .pipe(map((res: HttpResponse<IPermission[]>) => res.body ?? []))
      .pipe(
        map((permissions: IPermission[]) =>
          this.permissionService.addPermissionToCollectionIfMissing<IPermission>(permissions, this.rolePermission?.permission),
        ),
      )
      .subscribe((permissions: IPermission[]) => (this.permissionsSharedCollection = permissions));
  }
}
