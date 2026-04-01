import dayjs from 'dayjs/esm';

import { ICategory, NewCategory } from './category.model';

export const sampleWithRequiredData: ICategory = {
  id: 8109,
  name: 'midst croon cautiously',
};

export const sampleWithPartialData: ICategory = {
  id: 23998,
  name: 'appropriate functional',
  description: 'and',
  createdAt: dayjs('2026-03-31T13:35'),
  updatedAt: dayjs('2026-03-31T18:28'),
};

export const sampleWithFullData: ICategory = {
  id: 28780,
  name: 'ouch',
  description: 'self-confidence',
  createdAt: dayjs('2026-03-31T16:03'),
  updatedAt: dayjs('2026-03-31T20:14'),
};

export const sampleWithNewData: NewCategory = {
  name: 'where pfft regularly',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
