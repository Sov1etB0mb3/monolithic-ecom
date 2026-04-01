import { IPermission, NewPermission } from './permission.model';

export const sampleWithRequiredData: IPermission = {
  id: 8197,
  name: 'forenenst pessimistic impressionable',
};

export const sampleWithPartialData: IPermission = {
  id: 3679,
  name: 'hovercraft',
};

export const sampleWithFullData: IPermission = {
  id: 11619,
  name: 'beneath yum loftily',
  description: 'aside wiggly into',
};

export const sampleWithNewData: NewPermission = {
  name: 'limply meanwhile drat',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
