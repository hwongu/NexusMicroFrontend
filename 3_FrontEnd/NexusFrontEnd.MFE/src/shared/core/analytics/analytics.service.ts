import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  trackEvent(eventName: string, payload?: Record<string, unknown>): void {
    console.debug('[analytics]', eventName, payload ?? {});
  }

  trackError(error: unknown): void {
    console.error('[analytics:error]', error);
  }
}
