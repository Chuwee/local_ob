export interface BaseContentImage<T = string> {
    language: string;
    type: T;
    image?: string;
}

export interface ContentImage<T = string> extends BaseContentImage<T> {
    alt_text?: string;
}
