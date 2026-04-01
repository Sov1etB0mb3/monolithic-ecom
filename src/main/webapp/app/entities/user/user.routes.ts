import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import UserResolve from './route/user-routing-resolve.service';

const userRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/user.component').then(m => m.UserComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/user-detail.component').then(m => m.UserDetailComponent),
    resolve: {
      user: UserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/user-update.component').then(m => m.UserUpdateComponent),
    resolve: {
      user: UserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/user-update.component').then(m => m.UserUpdateComponent),
    resolve: {
      user: UserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default userRoute;
