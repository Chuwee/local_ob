export interface PriceTypeRow {
    id: number | string; name: string;
    placeholders: { [key: string]: string };
    occupancy: { [key: string]: number };
}
