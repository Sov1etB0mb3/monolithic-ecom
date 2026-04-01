import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IUserRole } from '../user-role.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../user-role.test-samples';

import { UserRoleService } from './user-role.service';

const requireRestSample: IUserRole = {
  ...sampleWithRequiredData,
};

describe('UserRole Service', () => {
  let service: UserRoleService;
  let httpMock: HttpTestingController;
  let expectedResult: IUserRole | IUserRole[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(UserRoleService);
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

    it('should create a UserRole', () => {
      const userRole = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(userRole).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a UserRole', () => {
      const userRole = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(userRole).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a UserRole', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of UserRole', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a UserRole', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a UserRole', () => {
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

    describe('addUserRoleToCollectionIfMissing', () => {
      it('should add a UserRole to an empty array', () => {
        const userRole: IUserRole = sampleWithRequiredData;
        expectedResult = service.addUserRoleToCollectionIfMissing([], userRole);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userRole);
      });

      it('should not add a UserRole to an array that contains it', () => {
        const userRole: IUserRole = sampleWithRequiredData;
        const userRoleCollection: IUserRole[] = [
          {
            ...userRole,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addUserRoleToCollectionIfMissing(userRoleCollection, userRole);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a UserRole to an array that doesn't contain it", () => {
        const userRole: IUserRole = sampleWithRequiredData;
        const userRoleCollection: IUserRole[] = [sampleWithPartialData];
        expectedResult = service.addUserRoleToCollectionIfMissing(userRoleCollection, userRole);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userRole);
      });

      it('should add only unique UserRole to an array', () => {
        const userRoleArray: IUserRole[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const userRoleCollection: IUserRole[] = [sampleWithRequiredData];
        expectedResult = service.addUserRoleToCollectionIfMissing(userRoleCollection, ...userRoleArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const userRole: IUserRole = sampleWithRequiredData;
        const userRole2: IUserRole = sampleWithPartialData;
        expectedResult = service.addUserRoleToCollectionIfMissing([], userRole, userRole2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userRole);
        expect(expectedResult).toContain(userRole2);
      });

      it('should accept null and undefined values', () => {
        const userRole: IUserRole = sampleWithRequiredData;
        expectedResult = service.addUserRoleToCollectionIfMissing([], null, userRole, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userRole);
      });

      it('should return initial array if no UserRole is added', () => {
        const userRoleCollection: IUserRole[] = [sampleWithRequiredData];
        expectedResult = service.addUserRoleToCollectionIfMissing(userRoleCollection, undefined, null);
        expect(expectedResult).toEqual(userRoleCollection);
      });
    });

    describe('compareUserRole', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareUserRole(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 16337 };
        const entity2 = null;

        const compareResult1 = service.compareUserRole(entity1, entity2);
        const compareResult2 = service.compareUserRole(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 16337 };
        const entity2 = { id: 7023 };

        const compareResult1 = service.compareUserRole(entity1, entity2);
        const compareResult2 = service.compareUserRole(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 16337 };
        const entity2 = { id: 16337 };

        const compareResult1 = service.compareUserRole(entity1, entity2);
        const compareResult2 = service.compareUserRole(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
