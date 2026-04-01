import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IRole } from 'app/entities/role/role.model';
import { RoleService } from 'app/entities/role/service/role.service';
import { IPermission } from 'app/entities/permission/permission.model';
import { PermissionService } from 'app/entities/permission/service/permission.service';
import { IRolePermission } from '../role-permission.model';
import { RolePermissionService } from '../service/role-permission.service';
import { RolePermissionFormService } from './role-permission-form.service';

import { RolePermissionUpdateComponent } from './role-permission-update.component';

describe('RolePermission Management Update Component', () => {
  let comp: RolePermissionUpdateComponent;
  let fixture: ComponentFixture<RolePermissionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let rolePermissionFormService: RolePermissionFormService;
  let rolePermissionService: RolePermissionService;
  let roleService: RoleService;
  let permissionService: PermissionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RolePermissionUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(RolePermissionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RolePermissionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    rolePermissionFormService = TestBed.inject(RolePermissionFormService);
    rolePermissionService = TestBed.inject(RolePermissionService);
    roleService = TestBed.inject(RoleService);
    permissionService = TestBed.inject(PermissionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Role query and add missing value', () => {
      const rolePermission: IRolePermission = { id: 30908 };
      const role: IRole = { id: 12873 };
      rolePermission.role = role;

      const roleCollection: IRole[] = [{ id: 12873 }];
      jest.spyOn(roleService, 'query').mockReturnValue(of(new HttpResponse({ body: roleCollection })));
      const additionalRoles = [role];
      const expectedCollection: IRole[] = [...additionalRoles, ...roleCollection];
      jest.spyOn(roleService, 'addRoleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ rolePermission });
      comp.ngOnInit();

      expect(roleService.query).toHaveBeenCalled();
      expect(roleService.addRoleToCollectionIfMissing).toHaveBeenCalledWith(
        roleCollection,
        ...additionalRoles.map(expect.objectContaining),
      );
      expect(comp.rolesSharedCollection).toEqual(expectedCollection);
    });

    it('should call Permission query and add missing value', () => {
      const rolePermission: IRolePermission = { id: 30908 };
      const permission: IPermission = { id: 19932 };
      rolePermission.permission = permission;

      const permissionCollection: IPermission[] = [{ id: 19932 }];
      jest.spyOn(permissionService, 'query').mockReturnValue(of(new HttpResponse({ body: permissionCollection })));
      const additionalPermissions = [permission];
      const expectedCollection: IPermission[] = [...additionalPermissions, ...permissionCollection];
      jest.spyOn(permissionService, 'addPermissionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ rolePermission });
      comp.ngOnInit();

      expect(permissionService.query).toHaveBeenCalled();
      expect(permissionService.addPermissionToCollectionIfMissing).toHaveBeenCalledWith(
        permissionCollection,
        ...additionalPermissions.map(expect.objectContaining),
      );
      expect(comp.permissionsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const rolePermission: IRolePermission = { id: 30908 };
      const role: IRole = { id: 12873 };
      rolePermission.role = role;
      const permission: IPermission = { id: 19932 };
      rolePermission.permission = permission;

      activatedRoute.data = of({ rolePermission });
      comp.ngOnInit();

      expect(comp.rolesSharedCollection).toContainEqual(role);
      expect(comp.permissionsSharedCollection).toContainEqual(permission);
      expect(comp.rolePermission).toEqual(rolePermission);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRolePermission>>();
      const rolePermission = { id: 7719 };
      jest.spyOn(rolePermissionFormService, 'getRolePermission').mockReturnValue(rolePermission);
      jest.spyOn(rolePermissionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rolePermission });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: rolePermission }));
      saveSubject.complete();

      // THEN
      expect(rolePermissionFormService.getRolePermission).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(rolePermissionService.update).toHaveBeenCalledWith(expect.objectContaining(rolePermission));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRolePermission>>();
      const rolePermission = { id: 7719 };
      jest.spyOn(rolePermissionFormService, 'getRolePermission').mockReturnValue({ id: null });
      jest.spyOn(rolePermissionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rolePermission: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: rolePermission }));
      saveSubject.complete();

      // THEN
      expect(rolePermissionFormService.getRolePermission).toHaveBeenCalled();
      expect(rolePermissionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRolePermission>>();
      const rolePermission = { id: 7719 };
      jest.spyOn(rolePermissionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rolePermission });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(rolePermissionService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareRole', () => {
      it('should forward to roleService', () => {
        const entity = { id: 12873 };
        const entity2 = { id: 333 };
        jest.spyOn(roleService, 'compareRole');
        comp.compareRole(entity, entity2);
        expect(roleService.compareRole).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('comparePermission', () => {
      it('should forward to permissionService', () => {
        const entity = { id: 19932 };
        const entity2 = { id: 17103 };
        jest.spyOn(permissionService, 'comparePermission');
        comp.comparePermission(entity, entity2);
        expect(permissionService.comparePermission).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
