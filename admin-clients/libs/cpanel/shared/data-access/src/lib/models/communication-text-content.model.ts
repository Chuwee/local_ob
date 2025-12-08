export enum CommunicationContentTextType {
    name = 'NAME',
    description = 'DESCRIPTION',
    emailBody = 'EMAIL_BODY',
    emailSubject = 'EMAIL_SUBJECT',
    emailCopyright = 'EMAIL_COPYRIGHT'
}

export interface CommunicationTextContent {
    type: CommunicationContentTextType;
    value: string;
    language: string;
}

/** this is how the Promotion Contents is used in forms with a formgroup structure */
export interface CommunicationTextContentFormData {
    [lang: string]: {
        [type: string]: string;
    };
}
