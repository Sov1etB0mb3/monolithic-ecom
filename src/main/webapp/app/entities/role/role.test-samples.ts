import { IRole, NewRole } from './role.model';

export const sampleWithRequiredData: IRole = {
  id: 2153,
  name: 'outgoing unblinking',
};

export const sampleWithPartialData: IRole = {
  id: 22661,
  name: 'fatal since',
  description: 'why frantically even',
};

export const sampleWithFullData: IRole = {
  id: 16604,
  name: 'before solidly swill',
  description: 'considering',
};

export const sampleWithNewData: NewRole = {
  name: 'commonly writhing than',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
