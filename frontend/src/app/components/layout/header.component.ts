import { inject, Component, computed } from "@angular/core";
import AuthService from "@/shared/auth.service";

@Component({
    selector: 'app-header',
    standalone: true,
    template: `
    <header class="flex flex-row justify-between items-center py-6 px-4">
        <h1>CMS</h1>
        
        @if (this.username()){
            <h3>Hello, {{ this.username() }}</h3>
        }
    </header>
    `
})
export default class HeaderComponent {
    private authService = inject(AuthService);

    readonly username = computed(() => {
        return this.authService.currentUser()?.username
    })
}