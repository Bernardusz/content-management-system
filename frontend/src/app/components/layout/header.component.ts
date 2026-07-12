import { inject, Component, computed } from "@angular/core";
import AuthService from "@/shared/auth.service";
import { RouterLink } from "@angular/router";

@Component({
    selector: 'app-header',
    standalone: true,
    imports: [RouterLink],
    template: `
    <header class="flex flex-row justify-between items-center py-6 px-4">
        <h1>
            <a routerLink="/" class="text-primary">CMS</a>
        </h1>
        
        @if (this.username()){
            <h3>Hello, 
                <a [routerLink]="['/users', this.userId()]">
                    {{ this.username() }}
                </a>
            </h3>
        }
    </header>
    `
})
export default class HeaderComponent {
    private authService = inject(AuthService);

    readonly username = computed(() => {
        return this.authService.currentUser()?.username
    })

    readonly userId = computed(() => {
        return this.authService.currentUser()?.id
    })
}