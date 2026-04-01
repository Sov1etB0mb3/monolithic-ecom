import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUserRole } from '../user-role.model';
import { UserRoleService } from '../service/user-role.service';

const userRoleResolve = (route: ActivatedRouteSnapshot): Observable<null | IUserRole> => {
  const id = route.params.id;
  if (id) {
    return inject(UserRoleService)
      .find(id)
      .pipe(
        mergeMap((userRole: HttpResponse<IUserRole>) => {
          if (userRole.body) {
            return of(userRole.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default userRoleResolve;
