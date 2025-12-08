export interface EntityFriends {
    limits: {
        default?: number;
        exceptions?: {
            name?: string;
            id: number;
            value: number;
        }[];
    };
    friends_relation_mode: 'BIDIRECTIONAL' | 'UNIDIRECTIONAL';
}