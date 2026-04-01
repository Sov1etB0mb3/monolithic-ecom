import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IRolePermission } from '../role-permission.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../role-permission.test-samples';

import { RolePermissionService } from './role-permission.service';

const requireRestSample: IRolePermission = {
  ...sampleWithRequiredData,
};

describe('RolePermission Service', () => {
  let service: RolePermissionService;
  let httpMock: HttpTestingController;
  let expectedResult: IRolePermission | IRolePermission[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(RolePermissionService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a RolePermission', () => {
      const rolePermission = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(rolePermission).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a RolePermission', () => {
      const rolePermission = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(rolePermission).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a RolePermission', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of RolePermission', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a RolePermission', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a RolePermission', () => {
      const queryObject: any = {
        page: 0,
        size: 20,
        query: '',
        sort: [],
      };
      service.search(queryObject).subscribe(() => expectedResult);

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(null, { status: 500, statusText: 'Internal Server Error' });
      expect(expectedResult).toBe(null);
    });

    describe('addRolePermissionToCollectionIfMissing', () => {
      it('should add a RolePermission to an empty array', () => {
        const rolePermission: IRolePermission = sampleWithRequiredData;
        expectedResult = service.addRolePermissionToCollectionIfMissing([], rolePermission);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(rolePermission);
      });

      it('should not add a RolePermission to an array that contains it', () => {
        const rolePermission: IRolePermission = sampleWithRequiredData;
        const rolePermissionCollection: IRolePermission[] = [
          {
            ...rolePermission,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRolePermissionToCollectionIfMissing(rolePermissionCollection, rolePermission);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a RolePermission to an array that doesn't contain it", () => {
        const rolePermission: IRolePermission = sampleWithRequiredData;
        const rolePermissionCollection: IRolePermission[] = [sampleWithPartialData];
        expectedResult = service.addRolePermissionToCollectionIfMissing(rolePermissionCollection, rolePermission);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(rolePermission);
      });

      it('should add only unique RolePermission to an array', () => {
        const rolePermissionArray: IRolePermission[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const rolePermissionCollection: IRolePermission[] = [sampleWithRequiredData];
        expectedResult = service.addRolePermissionToCollectionIfMissing(rolePermissionCollection, ...rolePermissionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const rolePermission: IRolePermission = sampleWithRequiredData;
        const rolePermission2: IRolePermission = sampleWithPartialData;
        expectedResult = service.addRolePermissionToCollectionIfMissing([], rolePermission, rolePermission2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(rolePermission);
        expect(expectedResult).toContain(rolePermission2);
      });

      it('should accept null and undefined values', () => {
        const rolePermission: IRolePermission = sampleWithRequiredData;
        expectedResult = service.addRolePermissionToCollectionIfMissing([], null, rolePermission, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(rolePermission);
      });

      it('should return initial array if no RolePermission is added', () => {
        const rolePermissionCollection: IRolePermission[] = [sampleWithRequiredData];
        expectedResult = service.addRolePermissionToCollectionIfMissing(rolePermissionCollection, undefined, null);
        expect(expectedResult).toEqual(rolePermissionCollection);
      });
    });

    describe('compareRolePermission', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRolePermission(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 7719 };
        const entity2 = null;

        const compareResult1 = service.compareRolePermission(entity1, entity2);
        const compareResult2 = service.compareRolePermission(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 7719 };
        const entity2 = { id: 30908 };

        const compareResult1 = service.compareRolePermission(entity1, entity2);
        const compareResult2 = service.compareRolePermission(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 7719 };
        const entity2 = { id: 7719 };

        const compareResult1 = service.compareRolePermission(entity1, entity2);
        const compareResult2 = service.compareRolePermission(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
