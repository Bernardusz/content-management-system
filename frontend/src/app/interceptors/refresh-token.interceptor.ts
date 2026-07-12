import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpEvent } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, filter, take, throwError } from 'rxjs';
import { Observable, BehaviorSubject } from 'rxjs';
import AuthService from '@/shared/auth.service';
import { Router } from '@angular/router';

let isRefreshing = false;
const refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);

export const refreshTokenInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn) => {
  const authService = inject(AuthService);
  const router = inject(Router);

    if (req.url.includes('/auth/refresh')) {
        return next(req);
    }

  return next(req).pipe(
    catchError((error) => {
      if (error.status === 403) {
        return handle403Error(req, next, authService);
      }
      router.navigate(['/']);
      return throwError(() => error);
    })
  );
};

function handle403Error(req: HttpRequest<unknown>, next: HttpHandlerFn, authService: AuthService): Observable<HttpEvent<unknown>> {
  if (!isRefreshing) {
    isRefreshing = true;
    refreshTokenSubject.next(null);

    return authService.refreshToken().pipe(
      switchMap((token: any) => {
        isRefreshing = false;
        refreshTokenSubject.next(token);
        // Retry the original request with the new token or modified request
        return next(req);
      }),
      catchError((err) => {
        isRefreshing = false;
        // Handle failed refresh (e.g., logout)
        return throwError(() => err);
      })
    );
  } else {
    // If a refresh is already happening, wait for it and retry
    return refreshTokenSubject.pipe(
      filter((token) => token != null),
      take(1),
      switchMap(() => next(req))
    );
  }
}
