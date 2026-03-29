import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { AuthService } from '@shared/core/auth/auth.service';
import { PermissionsService } from '@shared/core/auth/permissions.service';

interface NavigationItem {
  icon: string;
  label: string;
  route: string;
  roles?: string[];
}

@Component({
  selector: 'app-layout',
  imports: [
    CommonModule,
    RouterModule,
    MatToolbarModule,
    MatSidenavModule,
    MatMenuModule,
    MatIconModule,
    MatButtonModule,
    MatDividerModule
  ],
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.css']
})
export class LayoutComponent implements OnInit {
  private authService = inject(AuthService);
  private permissionsService = inject(PermissionsService);

  user: any;
  readonly navigationItems: NavigationItem[] = [
    { icon: 'star_rate', label: 'Bienvenida', route: '/dashboard' },
    { icon: 'search', label: 'Mant. Categoria', route: '/categories' },
    { icon: 'inventory', label: 'Mant. Productos', route: '/products' },
    { icon: 'group', label: 'Mant. Usuarios', route: '/users' },
    { icon: 'monetization_on', label: 'Reporte Ingresos', route: '/revenues' }
  ];

  get visibleNavigationItems(): NavigationItem[] {
    return this.navigationItems.filter((item) => this.permissionsService.hasAnyRole(item.roles));
  }

  ngOnInit(): void {
    this.user = this.authService.getUser();
  }

  logout(): void {
    this.authService.logout();
  }
}
