export enum BannerContentTextsType {
    seatSelectionDisclaimer = 'SEAT_SELECTION_DISCLAIMER'
}

export interface SaleRequestAdditionalBannerTexts {
    language: string;
    type: BannerContentTextsType;
    value: string;
}

export interface SaleRequestAdditionalBannerTextField {
    formField: string;
    type: BannerContentTextsType;
}
