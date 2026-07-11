import AuthService from "@/shared/auth.service";
import { isPlatformBrowser } from "@angular/common";
import { inject, PLATFORM_ID } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { map, of } from "rxjs";

export const anonymousGuard: CanActivateFn = (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    return authService.checkIfUserLoggedIn().pipe(
        map((isLoggedIn) => {
            if (isLoggedIn) return router.createUrlTree(["/"]);
            return true;
        }),
    );
};
