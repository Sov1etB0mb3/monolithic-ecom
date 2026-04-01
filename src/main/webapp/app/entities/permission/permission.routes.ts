import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import PermissionResolve from './route/permission-routing-resolve.service';

const permissionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/permission.component').then(m => m.PermissionComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/permission-detail.component').then(m => m.PermissionDetailComponent),
    resolve: {
      permission: PermissionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/permission-update.component').then(m => m.PermissionUpdateComponent),
    resolve: {
      permission: PermissionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/permission-update.component').then(m => m.PermissionUpdateComponent),
    resolve: {
      permission: PermissionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default permissionRoute;
