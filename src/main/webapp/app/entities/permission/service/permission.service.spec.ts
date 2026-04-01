import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IPermission } from '../permission.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../permission.test-samples';

import { PermissionService } from './permission.service';

const requireRestSample: IPermission = {
  ...sampleWithRequiredData,
};

describe('Permission Service', () => {
  let service: PermissionService;
  let httpMock: HttpTestingController;
  let expectedResult: IPermission | IPermission[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PermissionService);
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

    it('should create a Permission', () => {
      const permission = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(permission).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Permission', () => {
      const permission = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(permission).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Permission', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Permission', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Permission', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Permission', () => {
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

    describe('addPermissionToCollectionIfMissing', () => {
      it('should add a Permission to an empty array', () => {
        const permission: IPermission = sampleWithRequiredData;
        expectedResult = service.addPermissionToCollectionIfMissing([], permission);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(permission);
      });

      it('should not add a Permission to an array that contains it', () => {
        const permission: IPermission = sampleWithRequiredData;
        const permissionCollection: IPermission[] = [
          {
            ...permission,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPermissionToCollectionIfMissing(permissionCollection, permission);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Permission to an array that doesn't contain it", () => {
        const permission: IPermission = sampleWithRequiredData;
        const permissionCollection: IPermission[] = [sampleWithPartialData];
        expectedResult = service.addPermissionToCollectionIfMissing(permissionCollection, permission);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(permission);
      });

      it('should add only unique Permission to an array', () => {
        const permissionArray: IPermission[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const permissionCollection: IPermission[] = [sampleWithRequiredData];
        expectedResult = service.addPermissionToCollectionIfMissing(permissionCollection, ...permissionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const permission: IPermission = sampleWithRequiredData;
        const permission2: IPermission = sampleWithPartialData;
        expectedResult = service.addPermissionToCollectionIfMissing([], permission, permission2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(permission);
        expect(expectedResult).toContain(permission2);
      });

      it('should accept null and undefined values', () => {
        const permission: IPermission = sampleWithRequiredData;
        expectedResult = service.addPermissionToCollectionIfMissing([], null, permission, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(permission);
      });

      it('should return initial array if no Permission is added', () => {
        const permissionCollection: IPermission[] = [sampleWithRequiredData];
        expectedResult = service.addPermissionToCollectionIfMissing(permissionCollection, undefined, null);
        expect(expectedResult).toEqual(permissionCollection);
      });
    });

    describe('comparePermission', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePermission(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 19932 };
        const entity2 = null;

        const compareResult1 = service.comparePermission(entity1, entity2);
        const compareResult2 = service.comparePermission(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 19932 };
        const entity2 = { id: 17103 };

        const compareResult1 = service.comparePermission(entity1, entity2);
        const compareResult2 = service.comparePermission(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 19932 };
        const entity2 = { id: 19932 };

        const compareResult1 = service.comparePermission(entity1, entity2);
        const compareResult2 = service.comparePermission(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
