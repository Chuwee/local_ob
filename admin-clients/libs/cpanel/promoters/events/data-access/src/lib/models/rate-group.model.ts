export interface RateGroup {
    id: number;
    name: string;
    default: boolean;
    external_description?: string;
    position: number;
    texts?: {
        name: { [key: string]: string };
    };
}

export interface PostRateGroup {
    name: string;
    external_description: string;
    texts: {
        name: { [key: string]: string };
    };
}

export interface PutRateGroup {
    id: number;
    name: string;
    external_description?: string;
    position: number;
    texts: {
        name: { [key: string]: string };
    };
}

export enum EventRateGroupFieldsRestrictions {
    rateNameMaxLength = 50,
    avetNameMaxLength = 50
}
