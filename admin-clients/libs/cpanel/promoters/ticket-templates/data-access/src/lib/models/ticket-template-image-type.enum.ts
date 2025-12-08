export enum TicketTemplatePdfImageType {
    /** Imagen Cabecera */
    header = 'HEADER',
    /** Imagen Principal del ticket */
    body = 'BODY',
    /** Imagen de Fondo del ticket */
    eventLogo = 'EVENT_LOGO',
    /** Banner principal */
    bannerMain = 'BANNER_MAIN',
    /** Banner secundario */
    bannerSecondary = 'BANNER_SECONDARY',
    /** Banner canal */
    bannerChannelLogo = 'BANNER_CHANNEL_LOGO'
}

export enum TicketTemplatePrinterImageType {
    main = 'MAIN',
    bannerMain = 'BANNER_MAIN'
}

export type TicketTemplateImageType = TicketTemplatePdfImageType | TicketTemplatePrinterImageType;
