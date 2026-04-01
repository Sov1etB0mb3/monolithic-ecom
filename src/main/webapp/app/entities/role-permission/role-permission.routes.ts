import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import RolePermissionResolve from './route/role-permission-routing-resolve.service';

const rolePermissionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/role-permission.component').then(m => m.RolePermissionComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/role-permission-detail.component').then(m => m.RolePermissionDetailComponent),
    resolve: {
      rolePermission: RolePermissionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/role-permission-update.component').then(m => m.RolePermissionUpdateComponent),
    resolve: {
      rolePermission: RolePermissionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/role-permission-update.component').then(m => m.RolePermissionUpdateComponent),
    resolve: {
      rolePermission: RolePermissionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default rolePermissionRoute;
