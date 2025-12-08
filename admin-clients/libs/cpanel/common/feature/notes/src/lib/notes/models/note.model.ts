export interface Note {
    id: string;
    dates: {
        create_date: string;
        update_date: string;
    };
    title: string;
    description: string;
    users?: {
        create_user: {
            id: number;
            username: string;
            name: string;
        };
        update_user: {
            id: number;
            username: string;
            name: string;
        };
    };
}
