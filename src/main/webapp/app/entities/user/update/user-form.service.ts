import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IUser, NewUser } from '../user.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IUser for edit and NewUserFormGroupInput for create.
 */
type UserFormGroupInput = IUser | PartialWithRequiredKeyOf<NewUser>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IUser | NewUser> = Omit<T, 'createdAt' | 'updatedAt'> & {
  createdAt?: string | null;
  updatedAt?: string | null;
};

type UserFormRawValue = FormValueOf<IUser>;

type NewUserFormRawValue = FormValueOf<NewUser>;

type UserFormDefaults = Pick<NewUser, 'id' | 'createdAt' | 'updatedAt'>;

type UserFormGroupContent = {
  id: FormControl<UserFormRawValue['id'] | NewUser['id']>;
  username: FormControl<UserFormRawValue['username']>;
  password: FormControl<UserFormRawValue['password']>;
  createdAt: FormControl<UserFormRawValue['createdAt']>;
  updatedAt: FormControl<UserFormRawValue['updatedAt']>;
};

export type UserFormGroup = FormGroup<UserFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class UserFormService {
  createUserFormGroup(user: UserFormGroupInput = { id: null }): UserFormGroup {
    const userRawValue = this.convertUserToUserRawValue({
      ...this.getFormDefaults(),
      ...user,
    });
    return new FormGroup<UserFormGroupContent>({
      id: new FormControl(
        { value: userRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      username: new FormControl(userRawValue.username, {
        validators: [Validators.required],
      }),
      password: new FormControl(userRawValue.password, {
        validators: [Validators.required],
      }),
      createdAt: new FormControl(userRawValue.createdAt),
      updatedAt: new FormControl(userRawValue.updatedAt),
    });
  }

  getUser(form: UserFormGroup): IUser | NewUser {
    return this.convertUserRawValueToUser(form.getRawValue() as UserFormRawValue | NewUserFormRawValue);
  }

  resetForm(form: UserFormGroup, user: UserFormGroupInput): void {
    const userRawValue = this.convertUserToUserRawValue({ ...this.getFormDefaults(), ...user });
    form.reset(
      {
        ...userRawValue,
        id: { value: userRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): UserFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
      updatedAt: currentTime,
    };
  }

  private convertUserRawValueToUser(rawUser: UserFormRawValue | NewUserFormRawValue): IUser | NewUser {
    return {
      ...rawUser,
      createdAt: dayjs(rawUser.createdAt, DATE_TIME_FORMAT),
      updatedAt: dayjs(rawUser.updatedAt, DATE_TIME_FORMAT),
    };
  }

  private convertUserToUserRawValue(
    user: IUser | (Partial<NewUser> & UserFormDefaults),
  ): UserFormRawValue | PartialWithRequiredKeyOf<NewUserFormRawValue> {
    return {
      ...user,
      createdAt: user.createdAt ? user.createdAt.format(DATE_TIME_FORMAT) : undefined,
      updatedAt: user.updatedAt ? user.updatedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
