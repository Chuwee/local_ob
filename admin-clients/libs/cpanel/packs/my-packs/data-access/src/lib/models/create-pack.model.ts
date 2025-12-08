
export interface CreatePackRequest {
    name: string;
    type: 'AUTOMATIC' | 'MANUAL';
    main_item: {
        item_id: number;
        type: 'EVENT' | 'SESSION';
        venue_template_id?: number;
        sub_item_ids?: number[];
    };
    entity_id: number;
    tax_id: number;
}
