export interface ChannelInvitationsSettings {
    enabled: boolean;
    selection_mode?: ChannelInvitationsSelectionMode;
}

export enum ChannelInvitationsSelectionMode {
    auto = 'AUTO',
    manualAll = 'MANUAL_ALL',
    manualNone = 'MANUAL_NONE'
}