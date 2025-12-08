import {
    MembersLanguages,
    PlatformLanguages
} from './platform-languages.enum';

export interface ChannelLiteralsTextContent {
    key: string;
    value?: string;
}

export type PostLiterals = Record<PlatformLanguages | MembersLanguages, ChannelLiteralsTextContent[]>;
