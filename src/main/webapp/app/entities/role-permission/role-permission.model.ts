import { IRole } from 'app/entities/role/role.model';
import { IPermission } from 'app/entities/permission/permission.model';

export interface IRolePermission {
  id: number;
  role?: IRole | null;
  permission?: IPermission | null;
}

export type NewRolePermission = Omit<IRolePermission, 'id'> & { id: null };
