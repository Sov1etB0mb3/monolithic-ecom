import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPermission } from '../permission.model';
import { PermissionService } from '../service/permission.service';

const permissionResolve = (route: ActivatedRouteSnapshot): Observable<null | IPermission> => {
  const id = route.params.id;
  if (id) {
    return inject(PermissionService)
      .find(id)
      .pipe(
        mergeMap((permission: HttpResponse<IPermission>) => {
          if (permission.body) {
            return of(permission.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default permissionResolve;
