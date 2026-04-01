import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'category',
    data: { pageTitle: 'monolithicEcomApp.category.home.title' },
    loadChildren: () => import('./category/category.routes'),
  },
  {
    path: 'product',
    data: { pageTitle: 'monolithicEcomApp.product.home.title' },
    loadChildren: () => import('./product/product.routes'),
  },
  {
    path: 'user',
    data: { pageTitle: 'monolithicEcomApp.user.home.title' },
    loadChildren: () => import('./user/user.routes'),
  },
  {
    path: 'user-role',
    data: { pageTitle: 'monolithicEcomApp.userRole.home.title' },
    loadChildren: () => import('./user-role/user-role.routes'),
  },
  {
    path: 'role-permission',
    data: { pageTitle: 'monolithicEcomApp.rolePermission.home.title' },
    loadChildren: () => import('./role-permission/role-permission.routes'),
  },
  {
    path: 'role',
    data: { pageTitle: 'monolithicEcomApp.role.home.title' },
    loadChildren: () => import('./role/role.routes'),
  },
  {
    path: 'permission',
    data: { pageTitle: 'monolithicEcomApp.permission.home.title' },
    loadChildren: () => import('./permission/permission.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
