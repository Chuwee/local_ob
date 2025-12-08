export interface RefundAmount {
    tickets: number;
    products?: number;
    discounts: number;
    promotions: number;

    /**
     * Sumarized order sales
     * (order_automatic + order_collective)
     */
    orderSales: number;

    totalTickets: number;
    fees: number;
    delivery: number;
    insurance: number;
    gateway?: number;
    max: number;
    toRefund?: number;
}
