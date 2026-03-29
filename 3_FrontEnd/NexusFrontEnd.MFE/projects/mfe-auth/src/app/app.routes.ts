import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';

export const AUTH_ROUTES: Routes = [
  { path: '', component: LoginComponent }
];

export const routes: Routes = AUTH_ROUTES;
