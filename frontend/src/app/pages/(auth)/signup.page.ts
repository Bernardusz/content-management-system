import AuthService from '@/shared/auth.service';
import { FormAction, RouteMeta } from '@analogjs/router';
import { Component, inject, signal } from '@angular/core';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmCardImports } from '@spartan-ng/helm/card';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { Router, RouterLink } from '@angular/router';
import { anonymousGuard } from '@/guards/anonymous.guard';

export const routeMeta: RouteMeta = {
	canActivate: [anonymousGuard],
}

@Component({
	selector: 'app-signup-page',
	imports: [FormAction, RouterLink, HlmCardImports, HlmLabelImports, HlmInputImports, HlmButtonImports],
	host: { class: 'w-full max-w-md' },
	template: `
		<section class="section flex flex-col items-center justify-center">
			<hlm-card class="w-full max-w-sm">
				<hlm-card-header>
					<h3 hlmCardTitle>Sign up now!</h3>
					<p hlmCardDescription>Enter your credential to create and account</p>
				</hlm-card-header>

				<form
						id="signup-form"
						method="post"
						class="flex flex-col gap-4"
						>

				<div
					hlmCardContent
					class="flex flex-col gap-4"

				>
					
						<div class="flex flex-col gap-6">
							<div class="grid gap-2">
								<label hlmLabel for="username">Username</label>
								<input type="text" id="username" name="username" placeholder="Input your username" required hlmInput />
							</div>
						
							<div class="grid gap-2">
								<label hlmLabel for="email">Email</label>
								<input type="email" id="email" name="email" placeholder="m@example.com" required hlmInput />
							</div>

							<div class="grid gap-2">

								<label hlmLabel for="password">Password</label>
								<input type="password" id="password" name="password" required hlmInput />
							</div>
						</div>
				</div>

				<hlm-card-footer class="flex-col gap-2">
					<button hlmBtn type="submit" class="w-full" form="signup-form">Sign up</button>
					<a routerLink="/login" hlmBtn variant="outline" class="w-full">Login to your account</a>
				</hlm-card-footer>
				</form>
			</hlm-card>
		</section>
	`,
})
export default class AppSignupPage {
    private authService = inject(AuthService);
    private router = inject(Router);

    isSubmitting = signal(false);
}