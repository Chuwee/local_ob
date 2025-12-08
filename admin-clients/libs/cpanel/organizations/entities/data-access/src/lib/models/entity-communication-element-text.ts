export enum EntityCommunicationElementTextType {
    headerBannerUrl = 'HEADER_BANNER_URL'
}

export interface EntityCommunicationElementText {
    type?: EntityCommunicationElementTextType;
    language?: string;
    language_id?: string;
    value?: string;
}
