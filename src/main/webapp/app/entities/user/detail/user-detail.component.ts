import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IUser } from '../user.model';

@Component({
  selector: 'jhi-user-detail',
  templateUrl: './user-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class UserDetailComponent {
  user = input<IUser | null>(null);

  previousState(): void {
    window.history.back();
  }
}
