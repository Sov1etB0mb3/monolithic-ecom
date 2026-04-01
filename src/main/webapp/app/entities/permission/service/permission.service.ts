import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IPermission, NewPermission } from '../permission.model';

export type PartialUpdatePermission = Partial<IPermission> & Pick<IPermission, 'id'>;

export type EntityResponseType = HttpResponse<IPermission>;
export type EntityArrayResponseType = HttpResponse<IPermission[]>;

@Injectable({ providedIn: 'root' })
export class PermissionService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/permissions');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/permissions/_search');

  create(permission: NewPermission): Observable<EntityResponseType> {
    return this.http.post<IPermission>(this.resourceUrl, permission, { observe: 'response' });
  }

  update(permission: IPermission): Observable<EntityResponseType> {
    return this.http.put<IPermission>(`${this.resourceUrl}/${this.getPermissionIdentifier(permission)}`, permission, {
      observe: 'response',
    });
  }

  partialUpdate(permission: PartialUpdatePermission): Observable<EntityResponseType> {
    return this.http.patch<IPermission>(`${this.resourceUrl}/${this.getPermissionIdentifier(permission)}`, permission, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPermission>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPermission[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IPermission[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IPermission[]>()], asapScheduler)));
  }

  getPermissionIdentifier(permission: Pick<IPermission, 'id'>): number {
    return permission.id;
  }

  comparePermission(o1: Pick<IPermission, 'id'> | null, o2: Pick<IPermission, 'id'> | null): boolean {
    return o1 && o2 ? this.getPermissionIdentifier(o1) === this.getPermissionIdentifier(o2) : o1 === o2;
  }

  addPermissionToCollectionIfMissing<Type extends Pick<IPermission, 'id'>>(
    permissionCollection: Type[],
    ...permissionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const permissions: Type[] = permissionsToCheck.filter(isPresent);
    if (permissions.length > 0) {
      const permissionCollectionIdentifiers = permissionCollection.map(permissionItem => this.getPermissionIdentifier(permissionItem));
      const permissionsToAdd = permissions.filter(permissionItem => {
        const permissionIdentifier = this.getPermissionIdentifier(permissionItem);
        if (permissionCollectionIdentifiers.includes(permissionIdentifier)) {
          return false;
        }
        permissionCollectionIdentifiers.push(permissionIdentifier);
        return true;
      });
      return [...permissionsToAdd, ...permissionCollection];
    }
    return permissionCollection;
  }
}
