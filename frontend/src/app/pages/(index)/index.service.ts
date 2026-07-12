import { ContentSummary } from "@/types/content";
import { HttpClient, HttpParams } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable({
	providedIn: "root",


})
export default class IndexService {
	private http = inject(HttpClient);
	private url = "https://localhost:8443/api/contents";

	searchCredentials(searchQuery: string | null, page: number): Observable<ContentSummary[]> {
		const currentOffset = (page - 1) * 10;
        
        const params = new HttpParams()
            .set("offset", currentOffset)
            .set("limit", "10")
			.set("identifier", searchQuery || "");

		return this.http.get<ContentSummary[]>(`${this.url}`, {
			params,
		});
	}
}
