export const customerFriendRelation = ['FRIEND', 'MANAGER'] as const;
export type CustomerFriendRelation = typeof customerFriendRelation[number];
export type CustomerFriendStatus = 'ACTIVE' | 'PENDING' | 'LOCKED';
export const customerFriendsStatusAction = ['LOCK', 'UNLOCK'] as const ;
export type CustomerFriendsStatusAction = typeof customerFriendsStatusAction[number];

export interface CustomerFriends {
    id: string;
    name: string;
    surname: string;
    email: string;
    birthday: string;
    member_id: string;
    avatar: string;
    relation: CustomerFriendRelation;
    customer_status: CustomerFriendStatus;
    customer_types: {
        id: number;
        name: string;
        code: string;
    }[];
    customer_types_text?: string;
}

export interface PostCustomerFriend {
    id: string;
    relation: CustomerFriendRelation;
}
