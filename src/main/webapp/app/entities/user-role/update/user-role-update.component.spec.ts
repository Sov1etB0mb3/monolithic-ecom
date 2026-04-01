import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IRole } from 'app/entities/role/role.model';
import { RoleService } from 'app/entities/role/service/role.service';
import { IUserRole } from '../user-role.model';
import { UserRoleService } from '../service/user-role.service';
import { UserRoleFormService } from './user-role-form.service';

import { UserRoleUpdateComponent } from './user-role-update.component';

describe('UserRole Management Update Component', () => {
  let comp: UserRoleUpdateComponent;
  let fixture: ComponentFixture<UserRoleUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let userRoleFormService: UserRoleFormService;
  let userRoleService: UserRoleService;
  let userService: UserService;
  let roleService: RoleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [UserRoleUpdateComponent],
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
      .overrideTemplate(UserRoleUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserRoleUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    userRoleFormService = TestBed.inject(UserRoleFormService);
    userRoleService = TestBed.inject(UserRoleService);
    userService = TestBed.inject(UserService);
    roleService = TestBed.inject(RoleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const userRole: IUserRole = { id: 7023 };
      const user: IUser = { id: 3944 };
      userRole.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userRole });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('should call Role query and add missing value', () => {
      const userRole: IUserRole = { id: 7023 };
      const role: IRole = { id: 12873 };
      userRole.role = role;

      const roleCollection: IRole[] = [{ id: 12873 }];
      jest.spyOn(roleService, 'query').mockReturnValue(of(new HttpResponse({ body: roleCollection })));
      const additionalRoles = [role];
      const expectedCollection: IRole[] = [...additionalRoles, ...roleCollection];
      jest.spyOn(roleService, 'addRoleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userRole });
      comp.ngOnInit();

      expect(roleService.query).toHaveBeenCalled();
      expect(roleService.addRoleToCollectionIfMissing).toHaveBeenCalledWith(
        roleCollection,
        ...additionalRoles.map(expect.objectContaining),
      );
      expect(comp.rolesSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const userRole: IUserRole = { id: 7023 };
      const user: IUser = { id: 3944 };
      userRole.user = user;
      const role: IRole = { id: 12873 };
      userRole.role = role;

      activatedRoute.data = of({ userRole });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(user);
      expect(comp.rolesSharedCollection).toContainEqual(role);
      expect(comp.userRole).toEqual(userRole);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserRole>>();
      const userRole = { id: 16337 };
      jest.spyOn(userRoleFormService, 'getUserRole').mockReturnValue(userRole);
      jest.spyOn(userRoleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userRole });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userRole }));
      saveSubject.complete();

      // THEN
      expect(userRoleFormService.getUserRole).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(userRoleService.update).toHaveBeenCalledWith(expect.objectContaining(userRole));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserRole>>();
      const userRole = { id: 16337 };
      jest.spyOn(userRoleFormService, 'getUserRole').mockReturnValue({ id: null });
      jest.spyOn(userRoleService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userRole: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userRole }));
      saveSubject.complete();

      // THEN
      expect(userRoleFormService.getUserRole).toHaveBeenCalled();
      expect(userRoleService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserRole>>();
      const userRole = { id: 16337 };
      jest.spyOn(userRoleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userRole });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(userRoleService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareRole', () => {
      it('should forward to roleService', () => {
        const entity = { id: 12873 };
        const entity2 = { id: 333 };
        jest.spyOn(roleService, 'compareRole');
        comp.compareRole(entity, entity2);
        expect(roleService.compareRole).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
