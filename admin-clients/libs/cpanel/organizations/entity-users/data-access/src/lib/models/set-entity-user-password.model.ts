export interface PostEntityUserPasswordRequest {
    password: string;
    token?: string;
}

export interface PostMyUserPasswordRequest {
    password: string;
    old_password: string;
}
