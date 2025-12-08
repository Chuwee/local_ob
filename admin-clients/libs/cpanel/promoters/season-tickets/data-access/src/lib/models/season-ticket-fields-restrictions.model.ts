/* eslint-disable @typescript-eslint/no-duplicate-enum-values */
export enum SeasonTicketFieldsRestrictions {
    seasonTicketNameLength = 50,
    seasonTicketSurnameLength = 50,
    seasonTicketReferenceLength = 50,
    seasonTicketNamePattern = '^[^|]*$',
    seasonTicketContactNamePattern = '[a-zA-Z0-9çÇñÑ\\s]*',
    seasonTicketEmailLength = 150,
    seasonTicketPhoneLength = 25,
    // eslint-disable-next-line no-useless-escape
    seasonTicketPhonePattern = '^[+]{0,1}[\s0-9]*$'
}
