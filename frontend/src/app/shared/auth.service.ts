import { LoginRequest } from "@/types/auth";
import { UserCreation, UserDetail } from "@/types/user";
import { HttpClient } from "@angular/common/http";
import { inject, Injectable, signal } from "@angular/core";
import { catchError, map, Observable, of, tap } from "rxjs";

@Injectable({
    providedIn: "root",
})
export default class AuthService {
    private http = inject(HttpClient);
    private baseUrl = "https://localhost:8443/api";

    readonly isAuthenticated = signal<boolean | null>(null);
    public readonly currentUser = signal<UserDetail | null>(null);

    setLogOut() {
        this.isAuthenticated.set(false);
        this.currentUser.set(null);
    }

    registerUser(userData: UserCreation): Observable<UserCreation> {
        return this.http.post<UserCreation>(
            `${this.baseUrl}/auth/register`,
            userData,
        );
    }

    loginUser(userData: LoginRequest): Observable<void> {
        return this.http.post<void>(`${this.baseUrl}/auth/login`, userData);
    }

    checkIfUserLoggedIn(): Observable<boolean> {
        if (this.isAuthenticated() !== null) {
            return of(this.isAuthenticated() as boolean);
        }
        return this.http.get<UserDetail>(`${this.baseUrl}/auth/me`).pipe(
            tap((response) => {
                this.isAuthenticated.set(response === null ? false : true);
                this.currentUser.set(response);
                console.log(response)
            }),
            map((response) => {
                return response === null ? false : true;
            }),
            catchError(() => {
                this.isAuthenticated.set(false);
                this.currentUser.set(null);
                return of(false);
            }),
        );
    }

    refreshToken(): Observable<void> {
        return this.http.post<void>(`${this.baseUrl}/auth/refresh`, { withCredentials: true });
    }

    logOut(): Observable<void> {
        return this.http.post<void>(`${this.baseUrl}/auth/logout`, {}).pipe(
            tap(() => {
                this.setLogOut();
            }),
        );
    }
}
