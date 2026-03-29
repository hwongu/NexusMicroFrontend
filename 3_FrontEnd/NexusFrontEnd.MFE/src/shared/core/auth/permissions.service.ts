import { Injectable, inject } from '@angular/core';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class PermissionsService {
  private authService = inject(AuthService);

  hasAnyRole(expectedRoles: string[] = []): boolean {
    if (expectedRoles.length === 0) {
      return true;
    }

    const user = this.authService.getUser();
    const roles = Array.isArray(user?.roles) ? user.roles : [];
    return expectedRoles.some((role) => roles.includes(role));
  }
}
