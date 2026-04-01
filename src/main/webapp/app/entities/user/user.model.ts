import dayjs from 'dayjs/esm';

export interface IUser {
  id: number;
  username?: string | null;
  password?: string | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
}

export type NewUser = Omit<IUser, 'id'> & { id: null };
