import dayjs from 'dayjs/esm';
import { ICategory } from 'app/entities/category/category.model';

export interface IProduct {
  id: number;
  name?: string | null;
  quantity?: number | null;
  price?: number | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  category?: Pick<ICategory, 'id'> | null;
}

export type NewProduct = Omit<IProduct, 'id'> & { id: null };
