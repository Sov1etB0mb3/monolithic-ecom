import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { PermissionDetailComponent } from './permission-detail.component';

describe('Permission Management Detail Component', () => {
  let comp: PermissionDetailComponent;
  let fixture: ComponentFixture<PermissionDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermissionDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./permission-detail.component').then(m => m.PermissionDetailComponent),
              resolve: { permission: () => of({ id: 19932 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(PermissionDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PermissionDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load permission on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PermissionDetailComponent);

      // THEN
      expect(instance.permission()).toEqual(expect.objectContaining({ id: 19932 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
