import { ChannelBuild } from './channel-build.model';
import { ChannelStatus } from './channel-status.model';
import { DonationType, SurchargeMode } from './channel.model';

export interface PutChannel {
    name?: string;
    public?: boolean;
    build?: ChannelBuild;
    status?: ChannelStatus;
    domain?: string;
    languages?: {
        default?: string;
        selected?: string[];
    };
    currency_codes?: string[];
    contact?: {
        name?: string;
        surname?: string;
        email?: string;
        phone?: string;
        web?: string;
        job_position?: string;
        entity?: {
            owner?: string;
            manager?: string;
        };
    };
    settings?: {
        automatic_seat_selection?: boolean;
        enable_b2b?: boolean;
        // eslint-disable-next-line @typescript-eslint/naming-convention
        allow_B2B_publishing?: boolean;
        enable_B2B_event_category_filter?: boolean;
        surcharges?: {
            calculation?: SurchargeMode;
        };
        use_multi_event?: boolean;
        use_currency_exchange?: boolean;
        currency_default_exchange?: string;
        allow_data_protection_fields?: boolean;
        allow_linked_customers?: boolean;
        use_robot_indexation?: boolean;
        donations?: {
            enabled?: boolean;
            provider?: {
                id?: number;
                target_id?: string;
            };
            settings?: {
                options?: number[];
                type?: DonationType;
            };
        };
        support_email?: {
            enabled: boolean;
            address: string;
        };
        whatsapp?: {
            override_entity_config?: boolean;
            whatsapp_template?: number;
        };
        destination_channel?: {
            destination_channel_id?: string;
            destination_channel_type?: string;
        };
    };
    limits?: {
        tickets?: {
            purchase_max?: number;
            booking_max?: number;
            issue_max?: number;
        };
    };
    virtual_queue?: {
        active?: boolean;
        alias?: string;
    };
}
