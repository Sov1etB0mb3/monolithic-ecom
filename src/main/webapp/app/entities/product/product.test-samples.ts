import dayjs from 'dayjs/esm';

import { IProduct, NewProduct } from './product.model';

export const sampleWithRequiredData: IProduct = {
  id: 11737,
  name: 'hm sleepily',
  quantity: 17511,
  price: 1843.12,
};

export const sampleWithPartialData: IProduct = {
  id: 22728,
  name: 'sore ack unsteady',
  quantity: 6707,
  price: 8081.23,
  updatedAt: dayjs('2026-03-31T19:12'),
};

export const sampleWithFullData: IProduct = {
  id: 4403,
  name: 'cafe',
  quantity: 21233,
  price: 2026.75,
  createdAt: dayjs('2026-03-31T18:44'),
  updatedAt: dayjs('2026-03-31T13:35'),
};

export const sampleWithNewData: NewProduct = {
  name: 'anti inject why',
  quantity: 26563,
  price: 4718.75,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
