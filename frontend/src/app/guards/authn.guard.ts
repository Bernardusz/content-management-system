import AuthService from "@/shared/auth.service";
import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { map } from "rxjs";

export const authnGuard: CanActivateFn = (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    return authService.checkIfUserLoggedIn().pipe(
        map((isLoggedIn) => {
            if (isLoggedIn) return true;
            return router.createUrlTree(["/login"]);
        }),
    );
};
