export interface AutomaticSale {
    group: number;
    num: number;
    name: string;
    first_surname: string;
    second_surname: string;
    dni: string;
    phone: string;
    email: string;
    sector: string;
    price_zone: string;
    owner: boolean;
    seat_id: number;
    original_locator: string;
    language: string;
    processed: boolean;
    error_code: string;
    error_description: string;
    order_id: string;
    extra_field: string;
}

export interface AutomaticSalesPut {
    status: 'BLOCKED';
}
