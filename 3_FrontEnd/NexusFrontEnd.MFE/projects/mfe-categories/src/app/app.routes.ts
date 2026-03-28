import { Routes } from '@angular/router';
import { CategoryFormComponent } from './features/categories/category-form/category-form.component';
import { CategoriesComponent } from './features/categories/categories.component';

export const CATEGORY_ROUTES: Routes = [
  { path: '', component: CategoriesComponent },
  { path: 'new', component: CategoryFormComponent },
  { path: 'edit/:id', component: CategoryFormComponent }
];

export const routes: Routes = CATEGORY_ROUTES;
