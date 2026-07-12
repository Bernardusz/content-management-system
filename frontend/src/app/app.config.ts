import {
  provideHttpClient,
  withInterceptors,
} from '@angular/common/http';
import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners,
} from '@angular/core';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { provideFileRouter, requestContextInterceptor } from '@analogjs/router';
import { authInterceptor } from '@/interceptors/auth.interceptor';
import { withComponentInputBinding, withNavigationErrorHandler, withRouterConfig } from '@angular/router';
import { refreshTokenInterceptor } from './interceptors/refresh-token.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideFileRouter(
      withComponentInputBinding(),
      withNavigationErrorHandler(console.error),
      withRouterConfig({
        onSameUrlNavigation: 'reload',
      })
    ),
    provideHttpClient(
      withInterceptors([authInterceptor, refreshTokenInterceptor, requestContextInterceptor])
    ),
    provideClientHydration(withEventReplay()),
  ],
};
