import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IRolePermission, NewRolePermission } from '../role-permission.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRolePermission for edit and NewRolePermissionFormGroupInput for create.
 */
type RolePermissionFormGroupInput = IRolePermission | PartialWithRequiredKeyOf<NewRolePermission>;

type RolePermissionFormDefaults = Pick<NewRolePermission, 'id'>;

type RolePermissionFormGroupContent = {
  id: FormControl<IRolePermission['id'] | NewRolePermission['id']>;
  role: FormControl<IRolePermission['role']>;
  permission: FormControl<IRolePermission['permission']>;
};

export type RolePermissionFormGroup = FormGroup<RolePermissionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RolePermissionFormService {
  createRolePermissionFormGroup(rolePermission: RolePermissionFormGroupInput = { id: null }): RolePermissionFormGroup {
    const rolePermissionRawValue = {
      ...this.getFormDefaults(),
      ...rolePermission,
    };
    return new FormGroup<RolePermissionFormGroupContent>({
      id: new FormControl(
        { value: rolePermissionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      role: new FormControl(rolePermissionRawValue.role, {
        validators: [Validators.required],
      }),
      permission: new FormControl(rolePermissionRawValue.permission, {
        validators: [Validators.required],
      }),
    });
  }

  getRolePermission(form: RolePermissionFormGroup): IRolePermission | NewRolePermission {
    return form.getRawValue() as IRolePermission | NewRolePermission;
  }

  resetForm(form: RolePermissionFormGroup, rolePermission: RolePermissionFormGroupInput): void {
    const rolePermissionRawValue = { ...this.getFormDefaults(), ...rolePermission };
    form.reset(
      {
        ...rolePermissionRawValue,
        id: { value: rolePermissionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): RolePermissionFormDefaults {
    return {
      id: null,
    };
  }
}
