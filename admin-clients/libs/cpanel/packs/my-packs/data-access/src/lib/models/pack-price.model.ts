export interface PackPrice {
    price_type: {
        id: number;
        code: string;
        description: string;
    };
    rate: {
        id: number;
        name: string;
    };
    value: number;
}
