import { ImageRestrictions } from '@admin-clients/shared/data-access/models';

export const elementInfoSliderRestrictions: ImageRestrictions = {
    width: 1500,
    height: 816,
    size: 192160
};

export const elementInfoSliderThumbnailRestrictions: ImageRestrictions = {
    width: 1500 / 10,
    height: 816 / 10,
    size: 192160 / 10
};

export const elementInfoHighlightedImageRestrictions: ImageRestrictions = {
    width: 680,
    height: 370,
    size: 64053
};
