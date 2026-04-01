import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IUser } from '../user.model';
import { UserService } from '../service/user.service';

@Component({
  templateUrl: './user-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class UserDeleteDialogComponent {
  user?: IUser;

  protected userService = inject(UserService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.userService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
