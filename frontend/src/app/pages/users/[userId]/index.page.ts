import { authzGuard } from "@/guards/authz.guard";
import {
    UserUpdateInformation,
    UserDetail,
} from "@/types/user";
import { FormAction, injectLoad, LoadResult, RouteMeta } from "@analogjs/router";
import { Component, inject, Input, signal, Signal } from "@angular/core";
import { authnGuard } from "@/guards/authn.guard";
import { load } from "@/pages/users/[userId]/index.server";
import { toSignal } from "@angular/core/rxjs-interop";
import { Router } from "@angular/router";
import AuthService from "@/shared/auth.service";
import UserService from "./index.service";
import { HlmCardImports } from "@/libs/ui/card/src";
import { HlmLabelImports } from "@/libs/ui/label/src";
import { HlmInputImports } from "@/libs/ui/input/src";
import { HlmButtonImports } from "@/libs/ui/button/src";
import { finalize, tap } from "rxjs";
import { DatePipe } from "@angular/common";

export const routeMeta: RouteMeta = {
    canActivate: [authnGuard, authzGuard],
};

@Component({
    standalone: true,
    selector: "app-user-detail",
    host: { class: "h-full w-full" },
    imports: [FormAction, DatePipe, HlmCardImports, HlmLabelImports, HlmInputImports, HlmButtonImports],
    template: `
        <button
            (click)="isEditing.set(false)"
            class="absolute top-20 left-4 bg-primary text-background btn-primary p-2 text-center rounded-2xl"
        >
            Go back
        </button>
        <div class="flex z-50 flex-col gap-2 absolute top-20 right-2">
            <button
                (click)="isEditing.set('information')"
                class="bg-primary text-background btn-primary p-2 text-center rounded-2xl"
            >
                Edit Information
            </button>
            <button
                (click)="isEditing.set('password')"
                class="bg-primary text-background btn-primary p-2 text-center rounded-2xl"
            >
                Edit Password
            </button>
            <button
                (click)="this.logOut()"
                class="bg-primary text-background btn-primary p-2 text-center rounded-2xl"
            >
                Logout
            </button>
            <button
                (click)="deleteUser()"
                class="bg-red-500 text-background btn-primary p-2 text-center rounded-2xl"
            >
                Delete account
            </button>
            </div>

            <section class="section relative w-full">
                @if (isEditing() === false) {
                    @if (data(); as user) {
                        <div class="flex flex-col gap-4 w-full justify-center items-center">
                            <h2>
                                {{ data()?.username }}
                            </h2>
                            <hr />
                            <span class="flex flex-row w-full justify-center gap-4 items-center">
                                <p>Email: </p>
                                <p>{{ data()?.email }}</p>
                            </span>
                            <span class="flex flex-row justify-center w-full items-center gap-4">
                                <p>Created at: </p>
                                <p>{{ data()?.createdAt | date }}</p>
                            </span>
                        </div>
                    }
                } @else if (isEditing() === "password") {
                    <hlm-card size="default" class="min-w-96 w-full">
                        <hlm-card-header>
                            <h2 hlmCardTitle>Information</h2>
                            <p hlmCardDescription>
                                Update the public information of your profile!
                            </p>
                        </hlm-card-header>
                        <form
                                id="user-password-form"
                                (onSuccess)="onPasswordSuccess($event)"
                                method="post"
                                class="flex flex-col gap-4"
                            >
                            <div hlmCardContent>
                                
                                    <div class="flex flex-col gap-2">
                                        <label for="password">Password</label>
                                        <input
                                            type="password"
                                            id="password"
                                            name="password"
                                            required
                                        />
                                    </div>
                                    <div class="flex flex-col gap-2">
                                        <label for="password-confirm">Confirm your password</label>
                                        <input
                                            type="password"
                                            id="password-confirm"
                                            name="password-confirm"
                                            required
                                        />
                                    </div>
                                    <input type="hidden" name="userId" [value]="this.userId" />
                                    <input type="hidden" name="action" value="password" />
                                    <input type="hidden" name="createdAt" [value]="this.data()?.createdAt" />
                            </div>
                            <hlm-card-footer>
                                <button
                                    class="bg-primary rounded-2xl btn-primary p-2 text-center text-background"
                                    [disabled]="isSubmitting()"
                                    type="submit"
                                    form="user-password-form"
                                >
                                    {{ isSubmitting() ? "Updating Account..." : "Submit" }}
                                </button>
                            </hlm-card-footer>
                        </form>

                    </hlm-card>
                } @else if (isEditing() === "information") {
                    @if (data(); as user) {
                        <hlm-card size="default" class="min-w-96 w-full">
                            <hlm-card-header>
                                <h2 hlmCardTitle>Information</h2>
                                <p hlmCardDescription>
                                    Update the public information of your profile!
                                </p>
                            </hlm-card-header>
                            <form
                                (onSuccess)="onInformationSuccess($event)"
                                    id="user-update-form"
                                    method="post"
                                    class="flex flex-col gap-4"
                                >
                                <div hlmCardContent>
                                    
                                        <div class="flex flex-col gap-2">
                                            <label for="username">Username</label>
                                            <input
                                                [value]="data()?.username"
                                                type="text"
                                                id="username"
                                                name="username"
                                                required
                                            />
                                        </div>
                                        <div class="flex flex-col gap-2">
                                            <label for="email">Email</label>
                                            <input
                                                [value]="data()?.email"
                                                type="email"
                                                id="email"
                                                name="email"
                                                required
                                            />
                                        </div>
                                        <input type="hidden" name="userId" [value]="this.userId" />
                                        <input type="hidden" name="action" value="information" />
                                        <input type="hidden" name="createdAt" [value]="this.data()?.createdAt" />
                                </div>
                                <hlm-card-footer>
                                    <button
                                        class="bg-primary rounded-2xl btn-primary p-2 text-center text-background"
                                        [disabled]="isSubmitting()"
                                        type="submit"
                                        form="user-update-form"
                                    >
                                        {{ isSubmitting() ? "Updating Account..." : "Submit" }}
                                    </button>
                                </hlm-card-footer>
                            </form>

                        </hlm-card>
                    }
                }
            </section>
    `,
})
export default class ProfilePage {
    @Input() userId!: number;
    inputData = toSignal(injectLoad<typeof load>(), { requireSync: true });

    data = signal<UserDetail | null>(this.inputData()?.data ?? null);

    private userService = inject(UserService);
    private router = inject(Router);
    private authService = inject(AuthService);

    isEditing = signal<"information" | "password" | false>(false);
    isSubmitting = signal<boolean>(false);

    onInformationSuccess(response: any){
        if (response?.data){
            this.data.set(response.data);
        }
        this.isSubmitting.set(false);
        this.isEditing.set(false);
    }

    onPasswordSuccess(response: any){
        this.isSubmitting.set(false);       
        this.isEditing.set(false);    
        this.authService.logOut().subscribe({
            next: () => {
                this.router.navigate(["/login"]);
            },
            error: (error) => {
                console.error("Caught error: ", error);
            },
        });
    }

    logOut(){
        this.authService.logOut().pipe(
            tap(() => {
                this.router.navigate(["/login"]);
        })).subscribe();
    }


    deleteUser() {
        if (confirm("Are you sure you want to delete your account?")) {
            this.isSubmitting.set(true);
            this.userService.deleteUser(this.userId).subscribe({
                next: () => {
                    this.authService.logOut().subscribe({
                        next: () => {
                            this.router.navigate(["/login"]);
                        }
                    });
                },
                error: (error) => {
                    console.error("Caught error: ", error);
                },
            });
        }
    }
}