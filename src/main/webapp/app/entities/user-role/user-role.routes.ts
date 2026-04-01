import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import UserRoleResolve from './route/user-role-routing-resolve.service';

const userRoleRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/user-role.component').then(m => m.UserRoleComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/user-role-detail.component').then(m => m.UserRoleDetailComponent),
    resolve: {
      userRole: UserRoleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/user-role-update.component').then(m => m.UserRoleUpdateComponent),
    resolve: {
      userRole: UserRoleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/user-role-update.component').then(m => m.UserRoleUpdateComponent),
    resolve: {
      userRole: UserRoleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default userRoleRoute;
