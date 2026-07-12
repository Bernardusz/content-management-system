import { FormAction, RouteMeta } from '@analogjs/router';
import { Component, computed, inject } from '@angular/core';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmCardImports } from '@spartan-ng/helm/card';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { RouterLink } from '@angular/router';
import { authnGuard } from '@/guards/authn.guard';
import AuthService from '@/shared/auth.service';

export const routeMeta: RouteMeta = {
	canActivate: [authnGuard],
}

@Component({
	selector: 'app-create-content-page',
	imports: [FormAction, RouterLink, HlmCardImports, HlmLabelImports, HlmInputImports, HlmButtonImports],
	host: { class: 'w-full max-w-2xl' },
	template: `
		<section class="section flex flex-col items-center w-full justify-center p-4">
			<hlm-card class="w-full">
				<hlm-card-header>
					<h3 hlmCardTitle>Create Content</h3>
					<p hlmCardDescription>Fill in the details to create new content</p>
				</hlm-card-header>

				<form
					id="create-content-form"
					method="post"
					class="flex flex-col gap-4"
				>
					<div hlmCardContent class="flex flex-col gap-4">
						<div class="grid gap-2">
							<label hlmLabel for="title">Title</label>
							<input type="text" id="title" name="title" placeholder="Content title" required hlmInput />
						</div>

						<div class="grid gap-2">
							<label hlmLabel for="description">Description</label>
							<input type="text" id="description" name="description" placeholder="Short description" required hlmInput />
						</div>

						<div class="grid gap-2">
							<label hlmLabel for="content">Content</label>
							<textarea id="content" name="content" placeholder="Main content" required hlmInput rows="6"></textarea>
						</div>

						<div class="grid gap-2">
							<label hlmLabel for="isPrivate">Private</label>
							<select id="isPrivate" name="isPrivate" hlmInput>
								<option value="false">Public</option>
								<option value="true">Private</option>
							</select>
						</div>
						<input type="hidden" name="userId" [value]="userId()" />
					</div>

					<hlm-card-footer class="flex-col gap-2">
						<button hlmBtn type="submit" class="w-full" form="create-content-form">Create Content</button>
						<a routerLink="/" hlmBtn variant="outline" class="w-full">Back to Contents</a>
					</hlm-card-footer>
				</form>
			</hlm-card>
		</section>
	`,
})
export default class AppCreateContentPage {
	private authService = inject(AuthService);

	userId = computed(() => {
		return this.authService.currentUser()?.id;
	});
}