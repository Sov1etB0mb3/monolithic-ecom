export interface IPermission {
  id: number;
  name?: string | null;
  description?: string | null;
}

export type NewPermission = Omit<IPermission, 'id'> & { id: null };
