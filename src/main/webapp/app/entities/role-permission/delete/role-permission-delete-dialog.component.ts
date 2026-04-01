import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IRolePermission } from '../role-permission.model';
import { RolePermissionService } from '../service/role-permission.service';

@Component({
  templateUrl: './role-permission-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class RolePermissionDeleteDialogComponent {
  rolePermission?: IRolePermission;

  protected rolePermissionService = inject(RolePermissionService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.rolePermissionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
