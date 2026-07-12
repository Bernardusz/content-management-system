import { FormAction, RouteMeta } from '@analogjs/router';
import { Component, computed, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { HlmButtonImports } from '@spartan-ng/helm/button';
import { HlmCardImports } from '@spartan-ng/helm/card';
import { HlmInputImports } from '@spartan-ng/helm/input';
import { HlmLabelImports } from '@spartan-ng/helm/label';
import { Router, RouterLink } from '@angular/router';
import { authnGuard } from '@/guards/authn.guard';
import type { ContentDetail } from '@/types/content.d';
import type { CommentDetail } from '@/types/comment.d';
import { DatePipe } from '@angular/common';
import { signal } from '@angular/core';
import ContentService from '@/pages/contents/[contentId]/index.service';
import AuthService from '@/shared/auth.service';

export const routeMeta: RouteMeta = {
	canActivate: [authnGuard],
};

@Component({
	selector: 'app-content-detail-page',
	imports: [RouterLink, DatePipe, FormAction ,HlmCardImports, HlmLabelImports, DatePipe, HlmInputImports, HlmButtonImports],
	host: { class: 'w-full' },
	template: `
		<section class="section flex flex-col items-center justify-center p-4 w-full">
			<div class="w-full max-w-4xl">
				<div class="flex justify-between items-center mb-6">
					<a routerLink="/" hlmBtn variant="outline">Back to Contents</a>
					@if(content()?.userId == userId()){
						<a routerLink="/contents/{{ content()?.id }}/edit" hlmBtn>Edit Content</a>
					}
				</div>

				<hlm-card class="mb-6 w-full">
					<hlm-card-header>
						<h1 hlmCardTitle>{{ content()?.title }}</h1>
						<p hlmCardDescription>{{ content()?.description }}</p>
					</hlm-card-header>
					<div hlmCardcontent class="flex flex-col gap-4 p-4">
						<div class="prose mb-4">
							<p>{{ content()?.content }}</p>
						</div>
						<div class="text-sm">
							<p>Created: {{ content()?.createdAt | date }}</p>
							<p>Comments: {{ content()?.commentsCount }}</p>
							<p>Likes: {{ likeCount() }}</p>
							<p>Dislikes: {{ dislikeCount() }}</p>
						</div>
					</div>
					<hlm-card-footer class="flex gap-2">
						<button 
							hlmBtn 
							variant="outline" 
							(click)="likeContent()"
							[disabled]="content()?.alreadyLiked"
						>
							{{ content()?.alreadyLiked ? 'Liked' : 'Like' }}
						</button>
						<button 
							hlmBtn 
							variant="outline" 
							(click)="dislikeContent()"
							[disabled]="content()?.alreadyDisliked"
						>
							{{ content()?.alreadyDisliked ? 'Disliked' : 'Dislike' }}
						</button>
						@if(content()?.userId === userId()){
							<button 
							hlmBtn 
							variant="outline" 
							(click)="deleteContent()"
						>
							Delete
						</button>
						} 
					</hlm-card-footer>
				</hlm-card>

				<hlm-card class="mb-6">
					<hlm-card-header>
						<h3 hlmCardTitle>Add Comment</h3>
					</hlm-card-header>
					<div class="flex flex-col gap-4" hlmCardContent>
						<form id="comment-form" method="post" class="flex flex-col gap-4" (onSuccess)="addComment($event)">
							<div class="grid gap-2">
								<label hlmLabel for="commentTitle">Title</label>
								<input type="text" id="commentTitle" name="title" placeholder="Comment title" required hlmInput />
							</div>
							<div class="grid gap-2">
								<label hlmLabel for="commentContent">Comment</label>
								<textarea id="commentContent" name="content" placeholder="Your comment" required hlmInput rows="3"></textarea>
							</div>
							<input type="hidden" name="contentId" [value]="content()?.id" />
							<input type="hidden" name="userId" [value]="userId()" />
							<button hlmBtn type="submit" form="comment-form">Post Comment</button>
						</form>
					</div>
				</hlm-card>

				<h2 class="text-xl font-bold mb-4">Comments ({{ comments()?.length }})</h2>
				<div class="grid gap-4">
					@for (comment of comments(); track comment.id) {
						<hlm-card>
							<hlm-card-header>
								<h4 hlmCardTitle>{{ comment.title }}</h4>
							</hlm-card-header>
							<div hlmCardContent class="flex flex-col gap-4">
								<p>{{ comment.content }}</p>
								<div class="text-sm text-gray-500 mt-2">
									<p>Created: {{ comment.createdAt | date}}</p>
									<p>Likes: {{ comment.likesCount }}</p>
									<p>Dislikes: {{ comment.dislikesCount }}</p>
								</div>
							</div>
							<hlm-card-footer class="flex gap-2">
								<button 
									hlmBtn 
									variant="outline" 
									size="sm"
									(click)="likeComment(comment.id)"
									[disabled]="comment.alreadyLiked"
								>
									{{ comment.alreadyLiked ? 'Liked' : 'Like' }}
								</button>
								<button 
									hlmBtn 
									variant="outline" 
									size="sm"
									(click)="dislikeComment(comment.id)"
									[disabled]="comment.alreadyDisliked"
								>
									{{ comment.alreadyDisliked ? 'Disliked' : 'Dislike' }}
								</button>
								@if(comment.userId === userId()){
									<button 
										hlmBtn 
										variant="outline" 
										size="sm"
										(click)="deleteComment(comment.id)"
									>
										Delete
									</button>
								}
							</hlm-card-footer>
						</hlm-card>
					}
				</div>
			</div>
		</section>
	`,
})
export default class AppContentDetailPage implements OnInit{
	@Input() contentId!: number;
	content = signal<ContentDetail | null>(null);
  	comments = signal<CommentDetail[] | null>(null);

	
	private readonly router = inject(Router);
	likeCount = computed(() => {
		return this.content()?.likesCount
	});
	dislikeCount = computed(() => {
		return this.content()?.dislikesCount
	});

	private readonly contentService = inject(ContentService);
	private readonly authService = inject(AuthService);

	userId = computed(() => {
		return this.authService.currentUser()?.id
	})


	ngOnInit(){
		this.getData();
	}

	reloadPage() {
		this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
			this.router.navigate([this.router.url]);
		});
	}

	addComment(event: any){
		this.comments.update((currentComments) => {
			if (!currentComments) return null;
			return [event.data, ...currentComments];
		})
	}


	getData() {
		this.contentService.getData(this.contentId).subscribe({
			next: (data) => {
				this.content.set(data.contents);
				this.comments.set(data.comments);
			},
			error: (error) => {
				console.error("Error executing query page sweep: ", error);
			}
		})
	}

	likeContent() {
    const current = this.content();
    if (!current) return;

    if (current.alreadyLiked) {
        // If it's already liked, clicking should decrease it (Unlike)
        this.contentService.decreaseLikeContent(this.contentId).subscribe({
            next: () => this.updateContentState('UNLIKE'),
            error: (error) => console.error("Unlike error: ", error)
        });
    } else {
        // If it's not liked yet, click to Like it
        this.contentService.likeContent(this.contentId).subscribe({
            next: () => this.updateContentState('LIKE'),
            error: (error) => console.error("Like error: ", error)
        });
    }
}

dislikeContent() {
    const current = this.content();
    if (!current) return;

    if (current.alreadyDisliked) {
        // If it's already disliked, clicking should decrease it (Undislike)
        this.contentService.decreaseDislikeContent(this.contentId).subscribe({
            next: () => this.updateContentState('UNDISLIKE'),
            error: (error) => console.error("Undislike error: ", error)
        });
    } else {
        // If it's not disliked yet, click to Dislike it
        this.contentService.dislikeContent(this.contentId).subscribe({
            next: () => this.updateContentState('DISLIKE'),
            error: (error) => console.error("Dislike error: ", error)
        });
    }
}

	deleteContent() {
		if (confirm('Are you sure you want to delete this content?')) {
			this.contentService.deleteContent(this.contentId).subscribe({
				next: () => {
					this.router.navigate(['/'])
				},
				error: (error) => {
					console.error("Error executing query page sweep: ", error);
				}
			})
		}
	}

	likeComment(commentId: number) {
		// Find the current live state of this comment inside your signal array
		const targetComment = this.comments()?.find(c => c.id === commentId);
		if (!targetComment) return;

		// Determine the exact action required based on current flags
		if (targetComment.alreadyLiked) {
			// Condition A: It's already liked -> user wants to UNLIKE it
			this.contentService.decreaseLikeComment(commentId).subscribe({
				next: () => this.updateCommentState(commentId, 'UNLIKE'),
				error: (err) => console.error("Unlike failed", err)
			});
		} else {
			// Condition B: It's not liked yet -> user wants to LIKE it 
			// (This will also automatically wipe out a dislike if it exists!)
			this.contentService.likeComment(commentId).subscribe({
				next: () => this.updateCommentState(commentId, 'LIKE'),
				error: (err) => console.error("Like failed", err)
			});
		}
	}

	dislikeComment(commentId: number) {
		const targetComment = this.comments()?.find(c => c.id === commentId);
		if (!targetComment) return;

		if (targetComment.alreadyDisliked) {
			// Condition A: It's already disliked -> user wants to UNDISLIKE it
			this.contentService.decreaseDislikeComment(commentId).subscribe({
				next: () => this.updateCommentState(commentId, 'UNDISLIKE'),
				error: (err) => console.error("Undislike failed", err)
			});
		} else {
			// Condition B: It's not disliked yet -> user wants to DISLIKE it
			// (This will also automatically wipe out a like if it exists!)
			this.contentService.dislikeComment(commentId).subscribe({
				next: () => this.updateCommentState(commentId, 'DISLIKE'),
				error: (err) => console.error("Dislike failed", err)
			});
		}
	}

	private updateContentState(action: 'LIKE' | 'UNLIKE' | 'DISLIKE' | 'UNDISLIKE') {
		this.content.update((currentContent) => {
			if (!currentContent) return null;

			let payload: ContentDetail = currentContent;
			
			switch (action) {
				case 'LIKE':
					payload = {
						...currentContent,
						likesCount: currentContent.likesCount + 1,
						dislikesCount: currentContent.alreadyDisliked ? currentContent.dislikesCount - 1 : currentContent.dislikesCount,
						alreadyLiked: true,
						alreadyDisliked: false
					};
					return payload;
				case 'UNLIKE':
					payload = {
						...currentContent,
						likesCount: currentContent.likesCount - 1,
						alreadyLiked: false
					};
					return payload;
				case 'DISLIKE':
					payload = {
						...currentContent,
						dislikesCount: currentContent.dislikesCount + 1,
						likesCount: currentContent.alreadyLiked ? currentContent.likesCount - 1 : currentContent.likesCount,
						alreadyLiked: false,
						alreadyDisliked: true
					};
					return payload;
				case 'UNDISLIKE':
					payload = {
						...currentContent,
						dislikesCount: currentContent.dislikesCount - 1,
						alreadyDisliked: false
					};
					return payload;
				default:
					payload = currentContent;
					return payload;
			}
			});
		};
	

	private updateCommentState(commentId: number, action: 'LIKE' | 'UNLIKE' | 'DISLIKE' | 'UNDISLIKE') {
		this.comments.update((currentComments) => {
			if (!currentComments) return null;

			return currentComments.map((comment) => {
				if (comment.id !== commentId) return comment;

				switch (action) {
					case 'LIKE':
						return {
							...comment,
							likesCount: comment.likesCount + 1,
							dislikesCount: comment.alreadyDisliked ? comment.dislikesCount - 1 : comment.dislikesCount,
							alreadyLiked: true,
							alreadyDisliked: false
						};
					case 'UNLIKE':
						return {
							...comment,
							likesCount: comment.likesCount - 1,
							alreadyLiked: false
						};
					case 'DISLIKE':
						return {
							...comment,
							dislikesCount: comment.dislikesCount + 1,
							likesCount: comment.alreadyLiked ? comment.likesCount - 1 : comment.likesCount,
							alreadyLiked: false,
							alreadyDisliked: true
						};
					case 'UNDISLIKE':
						return {
							...comment,
							dislikesCount: comment.dislikesCount - 1,
							alreadyDisliked: false
						};
					default:
						return comment;
				}
			});
		});
	}

	deleteComment(commentId: number) {
		if (confirm('Are you sure you want to delete this comment?')) {
			this.contentService.deleteComment(commentId).subscribe({
				next: () => {
					this.comments.update((currentComments) => {
					// 1. If comments are currently null, return null immediately
						if (!currentComments) return null;

						// 2. Return the new mapped array back to the signal update handler
						return currentComments.filter((comment) => comment.id !== commentId);
					});
				},
				error: (error) => {
					console.error("Error executing query page sweep: ", error);
				}
			})
		}
	}
}
