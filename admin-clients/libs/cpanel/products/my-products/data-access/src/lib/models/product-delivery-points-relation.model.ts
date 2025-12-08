export interface ProductDeliveryPointsRelation {
    id: number;
    delivery_point: {
        id: number;
        name: string;
    };
}

export interface GetProductDeliveryPointsRelationReq {
    limit: number;
    offset: number;
}

export interface PostProductDeliveryPointsRelation {
    delivery_point_ids: number[];
}

export type VmProductPurchaseDeliveryDPs = Pick<PostProductDeliveryPointsRelation, 'delivery_point_ids'>;
