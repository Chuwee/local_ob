export interface ImageRestrictions {
    /** in pixels */
    width: number;
    /** in pixels */
    height: number;
    /** in bytes */
    size: number;
    /** in pixels, usefully when width and height are not set */
    maxWidth?: number;
    /** in pixels, usefully when width and height are not set */
    maxHeight?: number;
}
