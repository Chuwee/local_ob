export interface CategorySelectionItem {
    entity: { id: number };
    settings: {
        categories: {
            base: {
                id: number;
                parent_id: number;
                code: string;
                description: string;
            };
            custom: {
                id: number;
                parent_id: number;
                code: string;
                description: string;
            };
        };
        tour: {
            enable: boolean;
            id: number;
        };
    };
}

export interface PutCategorySelectionRequest {
    settings?: {
        tour?: {
            enable: boolean;
            id?: number;
        };
        categories?: {
            base?: {
                id?: number;
                code?: string;
                description?: string;
            };
            custom?: {
                id?: number;
                code?: string;
                description?: string;
            };

        };
    };
}