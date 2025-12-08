export interface SeasonTicketChannelSessionLink {
    language: string;
    link: string;
}

export interface SeasonTicketChannelLink {
    enabled: boolean;
    published: boolean;
    links: SeasonTicketChannelSessionLink[];
}
