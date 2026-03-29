import { Routes } from '@angular/router';
import { RevenueFormComponent } from './features/revenues/revenue-form/revenue-form.component';
import { RevenuesComponent } from './features/revenues/revenues.component';

export const REVENUE_ROUTES: Routes = [
  { path: '', component: RevenuesComponent },
  { path: 'new', component: RevenueFormComponent }
];

export const routes: Routes = REVENUE_ROUTES;
