export interface Content {
    id: number;
    title: string;
    description: string;
    content: string;
    createdAt: Date;
    updatedAt: Date;
    commentsCount: number;
    likesCount: number;
    dislikesCount: number;
    isPrivate: boolean;
    userId: number;
}

export interface ContentSummary {
    id: number;
    title: string;
    description: string;
    createdAt: Date;
    updatedAt: Date;
    commentsCount: number;
    likesCount: number;
    dislikesCount: number;
    userId: number;
}

export interface ContentDetail {
    id: number;
    title: string;
    description: string;
    content: string;
    createdAt: Date;
    updatedAt: Date;
    commentsCount: number;
    likesCount: number;
    dislikesCount: number;
    userId: number;
    alreadyLiked: boolean;
    alreadyDisliked: boolean;
}

export interface ContentCreation {
    title: string;
    description: string;
    content: string;
    isPrivate: boolean;
    userId: number;
}

export interface ContentUpdate {
    title: string;
    description: string;
    content: string;
    isPrivate: boolean;
}
