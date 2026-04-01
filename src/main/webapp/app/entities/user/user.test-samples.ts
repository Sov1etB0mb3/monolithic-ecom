import dayjs from 'dayjs/esm';

import { IUser, NewUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 24814,
  username: 'fuel extremely ha',
  password: 'instead minus wombat',
};

export const sampleWithPartialData: IUser = {
  id: 216,
  username: 'optimistic triumphantly',
  password: 'perfectly gum intervention',
  createdAt: dayjs('2026-03-31T12:13'),
};

export const sampleWithFullData: IUser = {
  id: 5440,
  username: 'as athwart above',
  password: 'carefully overvalue',
  createdAt: dayjs('2026-04-01T00:23'),
  updatedAt: dayjs('2026-04-01T07:29'),
};

export const sampleWithNewData: NewUser = {
  username: 'mostly crystallize gym',
  password: 'eek powerfully',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
