export interface User {
    id: number;
    username: string;
    email: string;
    password: string;
    createdAt: Date;
}

export interface UserCreation {
    username: string;
    email: string;
    password: string;
}

export interface UserSummary {
    id: number;
    username: string;
    email: string;
}

export interface UserDetail {
    id: number;
    username: string;
    email: string;
    createdAt: string;
}

export interface UserUpdateInformation {
    username: string;
    email: string;
}

export interface UserUpdatePassword {
    password: string;
}
