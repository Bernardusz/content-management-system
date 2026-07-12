import { FormAction, injectLoad, RouteMeta } from '@analogjs/router';
import { Component, inject, Input, signal } from '@angular/core';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmCardImports } from '@spartan-ng/helm/card';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { RouterLink } from '@angular/router';
import { authnGuard } from '@/guards/authn.guard';
import type { ContentDetail } from '@/types/content.d';
import { LoadResult } from '@analogjs/router';
import { load } from '@/pages/contents/[contentId]/edit/index.server';
import { toSignal } from '@angular/core/rxjs-interop';

export const routeMeta: RouteMeta = {
	canActivate: [authnGuard],
};

@Component({
	selector: 'app-edit-content-page',
	imports: [RouterLink, FormAction, HlmCardImports, HlmLabelImports, HlmInputImports, HlmButtonImports],
	template: `
		<section class="section flex flex-col items-center justify-center p-4">
			<div class="w-full max-w-2xl">
				<div class="flex justify-between items-center mb-6">
					<a routerLink="/contents/{{ content()?.id }}" hlmBtn variant="outline">Back to Content</a>
				</div>

				<hlm-card class="w-full">
					<hlm-card-header>
						<h3 hlmCardTitle>Edit Content</h3>
						<p hlmCardDescription>Update the content details</p>
					</hlm-card-header>

					<form
						id="edit-content-form"
						method="post"
						class="flex flex-col gap-4"
					>
						<div hlmCardContent class="flex flex-col gap-4">
							<div class="grid gap-2">
								<label hlmLabel for="title">Title</label>
								<input type="text" id="title" name="title" [value]="content()?.title" placeholder="Content title" required hlmInput />
							</div>

							<div class="grid gap-2">
								<label hlmLabel for="description">Description</label>
								<input type="text" id="description" name="description" [value]="content()?.description" placeholder="Short description" required hlmInput />
							</div>

							<div class="grid gap-2">
								<label hlmLabel for="content">Content</label>
								<textarea id="content" name="content" [value]="content()?.content" placeholder="Main content" required hlmInput rows="6"></textarea>
							</div>

							<div class="grid gap-2">
								<label hlmLabel for="isPrivate">Private</label>
								<select id="isPrivate" name="isPrivate" hlmInput>
									<option value="false">Public</option>
									<option value="true">Private</option>
								</select>
							</div>
							<input type="hidden" name="contentId" [value]="content()?.id" />
						</div>

						<hlm-card-footer class="flex-col gap-2">
							<button hlmBtn type="submit" class="w-full" form="edit-content-form">Update Content</button>
							<a routerLink="/contents/{{ content()?.id }}" hlmBtn variant="outline" class="w-full">Cancel</a>
						</hlm-card-footer>
					</form>
				</hlm-card>
			</div>
		</section>
	`,
})
export default class AppEditContentPage {
  data = toSignal(injectLoad<typeof load>(), { requireSync: true });

  content = signal<ContentDetail | null>(this.data() ?? null);

}
