import { UserDetail, UserUpdateInformation } from "@/types/user";
import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import AuthService from "@/shared/auth.service";
import { Observable, switchMap } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export default class UserService {
    private readonly http = inject(HttpClient);

    editUserInformation(userId: number, userData: UserUpdateInformation): Observable<UserDetail> {
        return this.http.put<void>(`https://localhost:8443/api/users/${userId}`, userData).pipe(
            switchMap(() => {
                return this.http.get<UserDetail>(`https://localhost:8443/api/users/${userId}`);
            })   
        );
    }

    editUserPassword(userId: number, password: string): Observable<void> {
        return this.http.put<void>(`https://localhost:8443/api/users/${userId}/password`, { password });
    }

    deleteUser(userId: number): Observable<void> {
        return this.http.delete<void>(`https://localhost:8443/api/users/${userId}`);
    }
}