import { Routes } from '@angular/router';
import { UserFormComponent } from './features/users/user-form/user-form.component';
import { UsersComponent } from './features/users/users.component';

export const USER_ROUTES: Routes = [
  { path: '', component: UsersComponent },
  { path: 'new', component: UserFormComponent },
  { path: 'edit/:id', component: UserFormComponent }
];

export const routes: Routes = USER_ROUTES;
