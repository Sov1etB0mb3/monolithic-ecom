import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IPermission } from '../permission.model';

@Component({
  selector: 'jhi-permission-detail',
  templateUrl: './permission-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class PermissionDetailComponent {
  permission = input<IPermission | null>(null);

  previousState(): void {
    window.history.back();
  }
}
