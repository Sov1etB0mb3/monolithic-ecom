import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../role-permission.test-samples';

import { RolePermissionFormService } from './role-permission-form.service';

describe('RolePermission Form Service', () => {
  let service: RolePermissionFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RolePermissionFormService);
  });

  describe('Service methods', () => {
    describe('createRolePermissionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRolePermissionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            role: expect.any(Object),
            permission: expect.any(Object),
          }),
        );
      });

      it('passing IRolePermission should create a new form with FormGroup', () => {
        const formGroup = service.createRolePermissionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            role: expect.any(Object),
            permission: expect.any(Object),
          }),
        );
      });
    });

    describe('getRolePermission', () => {
      it('should return NewRolePermission for default RolePermission initial value', () => {
        const formGroup = service.createRolePermissionFormGroup(sampleWithNewData);

        const rolePermission = service.getRolePermission(formGroup) as any;

        expect(rolePermission).toMatchObject(sampleWithNewData);
      });

      it('should return NewRolePermission for empty RolePermission initial value', () => {
        const formGroup = service.createRolePermissionFormGroup();

        const rolePermission = service.getRolePermission(formGroup) as any;

        expect(rolePermission).toMatchObject({});
      });

      it('should return IRolePermission', () => {
        const formGroup = service.createRolePermissionFormGroup(sampleWithRequiredData);

        const rolePermission = service.getRolePermission(formGroup) as any;

        expect(rolePermission).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRolePermission should not enable id FormControl', () => {
        const formGroup = service.createRolePermissionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRolePermission should disable id FormControl', () => {
        const formGroup = service.createRolePermissionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
