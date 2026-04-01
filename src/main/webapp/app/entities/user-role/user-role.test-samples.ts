import { IUserRole, NewUserRole } from './user-role.model';

export const sampleWithRequiredData: IUserRole = {
  id: 9115,
};

export const sampleWithPartialData: IUserRole = {
  id: 30986,
};

export const sampleWithFullData: IUserRole = {
  id: 21383,
};

export const sampleWithNewData: NewUserRole = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
