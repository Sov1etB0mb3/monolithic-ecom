import { IRolePermission, NewRolePermission } from './role-permission.model';

export const sampleWithRequiredData: IRolePermission = {
  id: 25385,
};

export const sampleWithPartialData: IRolePermission = {
  id: 12558,
};

export const sampleWithFullData: IRolePermission = {
  id: 11955,
};

export const sampleWithNewData: NewRolePermission = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
