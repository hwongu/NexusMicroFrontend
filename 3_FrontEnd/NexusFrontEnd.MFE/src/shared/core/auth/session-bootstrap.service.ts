import { Injectable, inject } from '@angular/core';
import { AuthService } from './auth.service';
import { AnalyticsService } from '../analytics/analytics.service';

@Injectable({
  providedIn: 'root'
})
export class SessionBootstrapService {
  private authService = inject(AuthService);
  private analytics = inject(AnalyticsService);

  initialize(): void {
    const authenticated = this.authService.isAuthenticated();
    this.analytics.trackEvent('session_bootstrap', { authenticated });
  }
}
