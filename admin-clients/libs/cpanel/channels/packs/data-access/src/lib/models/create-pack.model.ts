export interface CreatePackRequest {
    name: string;
    type: 'AUTOMATIC' | 'MANUAL';
    main_item: {
        item_id: number;
        type: 'EVENT' | 'SESSION';
        venue_template_id?: number;
    };
}
