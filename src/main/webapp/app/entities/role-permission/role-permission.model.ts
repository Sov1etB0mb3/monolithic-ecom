import { IRole } from 'app/entities/role/role.model';
import { IPermission } from 'app/entities/permission/permission.model';

export interface IRolePermission {
  id: number;
  role?: Pick<IRole, 'id'> | null;
  permission?: Pick<IPermission, 'id'> | null;
}

export type NewRolePermission = Omit<IRolePermission, 'id'> & { id: null };
