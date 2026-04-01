import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { PermissionService } from '../service/permission.service';
import { IPermission } from '../permission.model';
import { PermissionFormService } from './permission-form.service';

import { PermissionUpdateComponent } from './permission-update.component';

describe('Permission Management Update Component', () => {
  let comp: PermissionUpdateComponent;
  let fixture: ComponentFixture<PermissionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let permissionFormService: PermissionFormService;
  let permissionService: PermissionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PermissionUpdateComponent],
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
      .overrideTemplate(PermissionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PermissionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    permissionFormService = TestBed.inject(PermissionFormService);
    permissionService = TestBed.inject(PermissionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const permission: IPermission = { id: 17103 };

      activatedRoute.data = of({ permission });
      comp.ngOnInit();

      expect(comp.permission).toEqual(permission);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPermission>>();
      const permission = { id: 19932 };
      jest.spyOn(permissionFormService, 'getPermission').mockReturnValue(permission);
      jest.spyOn(permissionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ permission });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: permission }));
      saveSubject.complete();

      // THEN
      expect(permissionFormService.getPermission).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(permissionService.update).toHaveBeenCalledWith(expect.objectContaining(permission));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPermission>>();
      const permission = { id: 19932 };
      jest.spyOn(permissionFormService, 'getPermission').mockReturnValue({ id: null });
      jest.spyOn(permissionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ permission: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: permission }));
      saveSubject.complete();

      // THEN
      expect(permissionFormService.getPermission).toHaveBeenCalled();
      expect(permissionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPermission>>();
      const permission = { id: 19932 };
      jest.spyOn(permissionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ permission });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(permissionService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
