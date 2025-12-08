export interface ChannelBookingSettings {
    allow_booking: boolean;
    allow_customer_assignation: boolean;
    allow_presale_restrictions: boolean;
    allow_booking_checkout: boolean;
    booking_checkout_domain: string;
    booking_checkout_payment_methods: string[];
}
