import { Component, inject, signal } from "@angular/core";
import IndexService from "@/pages/(index)/index.service";
import { debounceTime, distinctUntilChanged, Subject } from "rxjs";
import { toSignal } from "@angular/core/rxjs-interop";
import { ContentSummary } from "@/types/content";
import { injectLoad, RouteMeta } from "@analogjs/router";
import { authnGuard } from "@/guards/authn.guard";
import { RouterLink } from "@angular/router";
import { load } from "@/pages/(index)/index.server";

export const routeMeta: RouteMeta = {
  canActivate: [authnGuard],
}

@Component({
    selector: "app-index-page",
    imports: [RouterLink],
    host: { class: "w-full" },
    template: `
        <a routerLink="/contents/create" class="text-blue-600 hover:underline text-sm font-medium absolute top-20
         right-4">
          Create new content
        </a>
        <section class="section flex flex-col gap-4 max-w-4xl mx-auto p-4 w-full">
            <input
              #searchInput
              type="text"
              placeholder="🔍 Search contents..."
              (input)="onSearchChange(searchInput.value)"
              class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />

            <div class="flex flex-col gap-2 my-4">
              @for (content of data(); track content.id) {
                <div class="p-4 border rounded-xl hover:bg-slate-50 transition-colors flex justify-between items-center">
                  <div>
                    <h3 class="font-bold text-lg text-slate-900">{{ content.title }}</h3>
                    <p class="text-sm text-slate-500">{{ content.description || 'No description provided' }}</p>
                  </div>
                  <a [routerLink]="['/contents', content.id]" class="text-blue-600 hover:underline text-sm font-medium">
                    View Detail →
                  </a>
                </div>
              } @empty {
                <div class="text-center p-8 text-slate-400 border border-dashed rounded-xl">
                  No records found matching your current parameters.
                </div>
              }
            </div>

            @if (data() && data()!.length > 0 || currentPage() > 1) {
              <div class="flex items-center justify-center gap-2 mt-4 border-t pt-4">
                <button
                  (click)="goToPage(currentPage() - 1)"
                  [disabled]="currentPage() === 1"
                  class="px-3 py-1.5 rounded-lg border text-sm font-medium transition-all hover:bg-slate-50 disabled:opacity-50 disabled:pointer-events-none"
                >
                  Previous
                </button>

                <div class="flex items-center gap-1">
                  <span class="px-3 py-1.5 rounded-lg bg-blue-50 text-blue-600 border border-blue-200 text-sm font-semibold">
                    Page {{ currentPage() }}
                  </span>
                </div>

                <button
                  (click)="goToPage(currentPage() + 1)"
                  [disabled]="data() && data()!.length < pageSize"
                  class="px-3 py-1.5 rounded-lg border text-sm font-medium transition-all hover:bg-slate-50 disabled:opacity-50 disabled:pointer-events-none"
                >
                  Next
                </button>
              </div>
            }
        </section>
    `
})
export default class ContentsPage {
    inputData = toSignal(injectLoad<typeof load>(), { requireSync: true });
    data = signal<ContentSummary[] | null>(this.inputData() || null);

    isEditing = signal<"information" | "password" | false>(false);
    isSubmitting = signal<boolean>(false);

    // Pagination State Indicators
    currentPage = signal<number>(1);
    pageSize = 10; // Change this to match whatever your backend limits (e.g., 5, 10, 20)
    
    // Tracks current clean text search criteria value frames across page shifts
    private currentSearchQuery = '';

    private searchSubject = new Subject<string>();
    private indexService = inject(IndexService);

    constructor() {
        this.searchSubject
            .pipe(debounceTime(1000), distinctUntilChanged())
            .subscribe((searchTerm) => {
                this.currentSearchQuery = searchTerm.trim();
                // When searching a brand new phrase value, reset viewport frame focus back to Page 1
                this.currentPage.set(1); 
                this.fetchCurrentPageData();
            });
    }

    onSearchChange(value: string) {
        this.searchSubject.next(value);
    }

    goToPage(pageNumber: number) {
        if (pageNumber < 1) return;
        
        this.currentPage.set(pageNumber);
        this.fetchCurrentPageData();
    }

    private fetchCurrentPageData() {
        if (!this.currentSearchQuery) {
            // Optional: If no query terms are typed, fetch basic root context page listings if supported,
            // or restore initial load arrays directly if you only fetch once.
            this.indexService.searchCredentials("", this.currentPage()).subscribe({
                next: (data) => this.data.set(data),
                error: (err) => console.error("Pagination load exception caught:", err)
            });
            return;
        }

        this.indexService.searchCredentials(this.currentSearchQuery, this.currentPage()).subscribe({
            next: (data) => {
                this.data.set(data);
            },
            error: (error) => {
                console.error("Error executing query page sweep: ", error);
            },
        });
    }
}