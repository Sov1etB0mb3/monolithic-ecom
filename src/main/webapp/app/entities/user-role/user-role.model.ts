import { IUser } from 'app/entities/user/user.model';
import { IRole } from 'app/entities/role/role.model';

export interface IUserRole {
  id: number;
  user?: IUser | null;
  role?: IRole | null;
}

export type NewUserRole = Omit<IUserRole, 'id'> & { id: null };
