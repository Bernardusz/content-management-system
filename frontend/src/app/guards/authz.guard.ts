import AuthService from "@/shared/auth.service";
import { isPlatformBrowser } from "@angular/common";
import { inject, PLATFORM_ID } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { map, of } from "rxjs";

export const authzGuard: CanActivateFn = (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    const pathUserId = route.paramMap.get("userId");

    if (!pathUserId) return router.createUrlTree(["/"]);

    return authService.currentUser()?.id === parseInt(pathUserId) ? 
        of(true) :
        of(router.createUrlTree(["/"]));
};
