import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../permission.test-samples';

import { PermissionFormService } from './permission-form.service';

describe('Permission Form Service', () => {
  let service: PermissionFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PermissionFormService);
  });

  describe('Service methods', () => {
    describe('createPermissionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPermissionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });

      it('passing IPermission should create a new form with FormGroup', () => {
        const formGroup = service.createPermissionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });
    });

    describe('getPermission', () => {
      it('should return NewPermission for default Permission initial value', () => {
        const formGroup = service.createPermissionFormGroup(sampleWithNewData);

        const permission = service.getPermission(formGroup) as any;

        expect(permission).toMatchObject(sampleWithNewData);
      });

      it('should return NewPermission for empty Permission initial value', () => {
        const formGroup = service.createPermissionFormGroup();

        const permission = service.getPermission(formGroup) as any;

        expect(permission).toMatchObject({});
      });

      it('should return IPermission', () => {
        const formGroup = service.createPermissionFormGroup(sampleWithRequiredData);

        const permission = service.getPermission(formGroup) as any;

        expect(permission).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPermission should not enable id FormControl', () => {
        const formGroup = service.createPermissionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPermission should disable id FormControl', () => {
        const formGroup = service.createPermissionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
