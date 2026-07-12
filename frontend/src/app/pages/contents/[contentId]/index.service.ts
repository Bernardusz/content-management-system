import { CommentDetail } from "@/types/comment";
import { ContentDetail } from "@/types/content";
import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { forkJoin, map, Observable, switchMap } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export default class ContentService {
    private http = inject(HttpClient)
    getData(contentId: number): Observable<{contents: ContentDetail, comments: CommentDetail[]}> {
        const contentRequest = this.http.get<ContentDetail>(`https://localhost:8443/api/contents/${contentId}`);
        const commentsRequest = this.http.get<CommentDetail[]>(`https://localhost:8443/api/comments?contentId=${contentId}&limit=50&offset=0`);
        
        return forkJoin({
            contents: contentRequest,
            comments: commentsRequest
        }).pipe(
            map(({ contents, comments }) => {
                return {
                    contents: contents,
                    comments: comments
                }
            })
        )
    }

    likeContent(contentId: number): Observable<void>{
        return this.http.post<void>(`https://localhost:8443/api/contents/${contentId}/like`, {});
    }

    decreaseLikeContent(contentId: number): Observable<void>{
        return this.http.delete<void>(`https://localhost:8443/api/contents/${contentId}/like`, {});
    }

    dislikeContent(contentId: number): Observable<void>{
        return this.http.post<void>(`https://localhost:8443/api/contents/${contentId}/dislike`, {});
    }

    decreaseDislikeContent(contentId: number): Observable<void>{
        return this.http.delete<void>(`https://localhost:8443/api/contents/${contentId}/dislike`, {});
    }

    deleteContent(contentId: number): Observable<void>{
        return this.http.delete<void>(`https://localhost:8443/api/contents/${contentId}`);
    }

    likeComment(commendId: number): Observable<void> {
        return this.http.post<void>(`https://localhost:8443/api/comments/${commendId}/like`, {});
    }

    decreaseLikeComment(commendId: number): Observable<void> {
        return this.http.delete<void>(`https://localhost:8443/api/comments/${commendId}/like`, {});
    }

    dislikeComment(commendId: number): Observable<void> {
        return this.http.post<void>(`https://localhost:8443/api/comments/${commendId}/dislike`, {});
    }

    decreaseDislikeComment(commendId: number): Observable<void> {
        return this.http.delete<void>(`https://localhost:8443/api/comments/${commendId}/dislike`, {});
    }

    deleteComment(commentId: number): Observable<void> {
        return this.http.delete<void>(`https://localhost:8443/api/comments/${commentId}`);
    }
}