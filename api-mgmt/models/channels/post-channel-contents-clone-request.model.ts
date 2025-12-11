import { ImportComContentsGroups } from './common-types';

export interface PostChannelContentsCloneRequest {
    channel_id: number;
    contents: ImportComContentsGroups[];
}
