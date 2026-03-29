import { loadRemoteModule } from '@angular-architects/native-federation';
import { Route, Routes } from '@angular/router';
import { authGuard } from '@shared/core/guards/auth.guard';
import { LayoutComponent } from './core/layout/layout.component';
import { RemoteUnavailableComponent } from './shell/remote-unavailable.component';

function remoteFallback(remoteName: string): Route[] {
  return [{ path: '', component: RemoteUnavailableComponent, data: { remoteName } }];
}

function loadRemoteRoutes(remoteName: string, exportName: string): Promise<Route[]> {
  return loadRemoteModule(remoteName, './Routes')
    .then((module) => module[exportName] as Route[])
    .catch(() => remoteFallback(remoteName));
}

export const routes: Routes = [
  {
    path: 'login',
    loadChildren: () => loadRemoteRoutes('mfe-auth', 'AUTH_ROUTES')
  },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        loadChildren: () => loadRemoteRoutes('mfe-dashboard', 'DASHBOARD_ROUTES')
      },
      {
        path: 'categories',
        loadChildren: () => loadRemoteRoutes('mfe-categories', 'CATEGORY_ROUTES')
      },
      {
        path: 'users',
        loadChildren: () => loadRemoteRoutes('mfe-users', 'USER_ROUTES')
      },
      {
        path: 'products',
        loadChildren: () => loadRemoteRoutes('mfe-products', 'PRODUCT_ROUTES')
      },
      {
        path: 'revenues',
        loadChildren: () => loadRemoteRoutes('mfe-revenues', 'REVENUE_ROUTES')
      },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  },
  { path: '**', redirectTo: 'login' }
];
