import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IUserRole, NewUserRole } from '../user-role.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IUserRole for edit and NewUserRoleFormGroupInput for create.
 */
type UserRoleFormGroupInput = IUserRole | PartialWithRequiredKeyOf<NewUserRole>;

type UserRoleFormDefaults = Pick<NewUserRole, 'id'>;

type UserRoleFormGroupContent = {
  id: FormControl<IUserRole['id'] | NewUserRole['id']>;
  user: FormControl<IUserRole['user']>;
  role: FormControl<IUserRole['role']>;
};

export type UserRoleFormGroup = FormGroup<UserRoleFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class UserRoleFormService {
  createUserRoleFormGroup(userRole: UserRoleFormGroupInput = { id: null }): UserRoleFormGroup {
    const userRoleRawValue = {
      ...this.getFormDefaults(),
      ...userRole,
    };
    return new FormGroup<UserRoleFormGroupContent>({
      id: new FormControl(
        { value: userRoleRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      user: new FormControl(userRoleRawValue.user, {
        validators: [Validators.required],
      }),
      role: new FormControl(userRoleRawValue.role, {
        validators: [Validators.required],
      }),
    });
  }

  getUserRole(form: UserRoleFormGroup): IUserRole | NewUserRole {
    return form.getRawValue() as IUserRole | NewUserRole;
  }

  resetForm(form: UserRoleFormGroup, userRole: UserRoleFormGroupInput): void {
    const userRoleRawValue = { ...this.getFormDefaults(), ...userRole };
    form.reset(
      {
        ...userRoleRawValue,
        id: { value: userRoleRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): UserRoleFormDefaults {
    return {
      id: null,
    };
  }
}
