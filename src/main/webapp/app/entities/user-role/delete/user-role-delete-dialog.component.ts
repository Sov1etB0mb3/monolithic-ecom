import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IUserRole } from '../user-role.model';
import { UserRoleService } from '../service/user-role.service';

@Component({
  templateUrl: './user-role-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class UserRoleDeleteDialogComponent {
  userRole?: IUserRole;

  protected userRoleService = inject(UserRoleService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.userRoleService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
