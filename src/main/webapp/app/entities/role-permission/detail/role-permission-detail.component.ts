import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IRolePermission } from '../role-permission.model';

@Component({
  selector: 'jhi-role-permission-detail',
  templateUrl: './role-permission-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class RolePermissionDetailComponent {
  rolePermission = input<IRolePermission | null>(null);

  previousState(): void {
    window.history.back();
  }
}
