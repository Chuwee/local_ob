import { ImportComContentsGroups } from '@admin-clients/cpanel/channels/communication/data-access';

export interface PostChannelContentsCloneRequest {
    channel_id: number;
    contents: ImportComContentsGroups[];
}
