export interface CancellationServiceBase {
    id: number;
    enabled: boolean;
    default_selected: boolean;
}

export interface CancellationService extends CancellationServiceBase {
    name: string;
    default_allowed: boolean;
}

export interface ChannelCancellationServices {
    providers: CancellationService[];
}

export interface PutChannelCancellationServices {
    providers: CancellationServiceBase[];
}
