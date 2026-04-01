import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRolePermission } from '../role-permission.model';
import { RolePermissionService } from '../service/role-permission.service';

const rolePermissionResolve = (route: ActivatedRouteSnapshot): Observable<null | IRolePermission> => {
  const id = route.params.id;
  if (id) {
    return inject(RolePermissionService)
      .find(id)
      .pipe(
        mergeMap((rolePermission: HttpResponse<IRolePermission>) => {
          if (rolePermission.body) {
            return of(rolePermission.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default rolePermissionResolve;
