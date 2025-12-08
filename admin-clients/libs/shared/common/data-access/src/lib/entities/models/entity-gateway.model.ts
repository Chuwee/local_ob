export interface EntityGateway {
    gateway_sid: string;
    name: string;
    description: string;
    synchronous: boolean;
    refund: boolean;
    retry: boolean;
    retries: number;
    live: boolean;
    wallet: boolean;
    available_gateway_asociation: string[];
}
