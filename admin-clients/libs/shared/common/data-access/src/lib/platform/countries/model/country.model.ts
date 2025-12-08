export interface Country {
    code: string;
    name: string;
}

export interface CountryWithId extends Country {
    id: number;
}

export interface CountryWithTaxCalculation extends Country {
    tax_calculation: CountryTaxCalculation;
}

export type CountryTaxCalculation =
    'COUNTRY' |
    'COUNTRY_ZIPCODE' |
    'CATEGORY' |
    'COUNTRY_PROVINCE';
;
