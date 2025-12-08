export interface ChannelVouchers {
    allow_redeem_vouchers?: boolean;
    allow_refund_to_vouchers?: boolean;
    gift_card?: {
        enable: boolean;
        id?: number;
    };
    gift_cards?: {
        enable: boolean;
        gift_card_ids?: [
            {
                gift_card_id: number;
                currency_code: string;
            }
        ];
    };
}
