import { Routes } from '@angular/router';
import { ProductFormComponent } from './features/products/product-form/product-form.component';
import { ProductsComponent } from './features/products/products.component';

export const PRODUCT_ROUTES: Routes = [
  { path: '', component: ProductsComponent },
  { path: 'new', component: ProductFormComponent },
  { path: 'edit/:id', component: ProductFormComponent }
];

export const routes: Routes = PRODUCT_ROUTES;
