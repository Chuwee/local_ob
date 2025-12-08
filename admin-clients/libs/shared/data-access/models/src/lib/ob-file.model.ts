export interface ObFile {
    data?: string;
    name?: string;
    contentType?: string;
    size?: number;
    remote?: boolean;
    altText?: string;
}

export interface ObFileDimensions extends ObFile {
    width?: number;
    height?: number;
}
