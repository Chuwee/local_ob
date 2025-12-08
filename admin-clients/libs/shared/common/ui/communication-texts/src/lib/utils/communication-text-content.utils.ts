import { CommunicationTextContent, CommunicationTextContentFormData } from '@admin-clients/cpanel/shared/data-access';

/** an util to transform Promotion Content to PromotionContentFormData (an usable form type)  */
export const convertContentsIntoFormData = (contents: CommunicationTextContent[]): CommunicationTextContentFormData =>
    contents.reduce<CommunicationTextContentFormData>((acc, content) => (
        acc[content.language] = { ...acc[content.language], [content.type.toLowerCase()]: content.value }, acc
    ), {});
