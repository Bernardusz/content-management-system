export interface CommentUpdate {
    title: string;
    content: string;
}

export interface CommentCreation {
    title: string;
    content: string;
    userId: number;
    contentId: number;
}

export interface CommentDetail {
    id: number;
    title: string;
    content: string;
    createdAt: Date;
    edited: Date;
    likesCount: number;
    dislikesCount: number;
    userId: number;
    contentId: number;
    alreadyLiked: boolean;
    alreadyDisliked: boolean;
}

export interface Comment {
    id: number;
    title: string;
    content: string;
    createdAt: Date;
    edited: Date;
    likesCount: number;
    dislikesCount: number;
    userId: number;
    contentId: number;
}
