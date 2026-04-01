import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IRolePermission, NewRolePermission } from '../role-permission.model';

export type PartialUpdateRolePermission = Partial<IRolePermission> & Pick<IRolePermission, 'id'>;

export type EntityResponseType = HttpResponse<IRolePermission>;
export type EntityArrayResponseType = HttpResponse<IRolePermission[]>;

@Injectable({ providedIn: 'root' })
export class RolePermissionService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/role-permissions');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/role-permissions/_search');

  create(rolePermission: NewRolePermission): Observable<EntityResponseType> {
    return this.http.post<IRolePermission>(this.resourceUrl, rolePermission, { observe: 'response' });
  }

  update(rolePermission: IRolePermission): Observable<EntityResponseType> {
    return this.http.put<IRolePermission>(`${this.resourceUrl}/${this.getRolePermissionIdentifier(rolePermission)}`, rolePermission, {
      observe: 'response',
    });
  }

  partialUpdate(rolePermission: PartialUpdateRolePermission): Observable<EntityResponseType> {
    return this.http.patch<IRolePermission>(`${this.resourceUrl}/${this.getRolePermissionIdentifier(rolePermission)}`, rolePermission, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IRolePermission>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRolePermission[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IRolePermission[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IRolePermission[]>()], asapScheduler)));
  }

  getRolePermissionIdentifier(rolePermission: Pick<IRolePermission, 'id'>): number {
    return rolePermission.id;
  }

  compareRolePermission(o1: Pick<IRolePermission, 'id'> | null, o2: Pick<IRolePermission, 'id'> | null): boolean {
    return o1 && o2 ? this.getRolePermissionIdentifier(o1) === this.getRolePermissionIdentifier(o2) : o1 === o2;
  }

  addRolePermissionToCollectionIfMissing<Type extends Pick<IRolePermission, 'id'>>(
    rolePermissionCollection: Type[],
    ...rolePermissionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const rolePermissions: Type[] = rolePermissionsToCheck.filter(isPresent);
    if (rolePermissions.length > 0) {
      const rolePermissionCollectionIdentifiers = rolePermissionCollection.map(rolePermissionItem =>
        this.getRolePermissionIdentifier(rolePermissionItem),
      );
      const rolePermissionsToAdd = rolePermissions.filter(rolePermissionItem => {
        const rolePermissionIdentifier = this.getRolePermissionIdentifier(rolePermissionItem);
        if (rolePermissionCollectionIdentifiers.includes(rolePermissionIdentifier)) {
          return false;
        }
        rolePermissionCollectionIdentifiers.push(rolePermissionIdentifier);
        return true;
      });
      return [...rolePermissionsToAdd, ...rolePermissionCollection];
    }
    return rolePermissionCollection;
  }
}
