import { APP_INITIALIZER, ApplicationConfig, ErrorHandler, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { SessionBootstrapService } from '@shared/core/auth/session-bootstrap.service';
import { GlobalErrorHandler } from '@shared/core/errors/global-error-handler';
import { routes } from './app.routes';

export function initializeSession(sessionBootstrapService: SessionBootstrapService): () => void {
  return () => sessionBootstrapService.initialize();
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideAnimations(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withFetch()),
    {
      provide: APP_INITIALIZER,
      multi: true,
      deps: [SessionBootstrapService],
      useFactory: initializeSession
    },
    {
      provide: ErrorHandler,
      useClass: GlobalErrorHandler
    }
  ]
};
