import { ErrorHandler, Injectable, inject } from '@angular/core';
import { AnalyticsService } from '../analytics/analytics.service';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  private analytics = inject(AnalyticsService);

  handleError(error: unknown): void {
    this.analytics.trackError(error);
    console.error(error);
  }
}
