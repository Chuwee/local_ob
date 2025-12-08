export enum ProducerFieldsRestrictions {
    producerNameLength = 50,
    producerSocialReasonMaxLength = 50,
    producerTaxIdMaxLength = 25,
    producerTaxIdPattern = '[\.a-zA-Z0-9_-\\s]*',
    producerAdressMaxLength = 50,
    producerCityMaxLength = 50,
    producerPostalCodeMaxLength = 10,
    producerPostalCodePattern = '[\.a-zA-Z0-9_-\\s]*',
    producerContactMaxLength = 50,
    producerPhoneMaxLength = 30,
    producerPhonePattern = '^[+]{0,1}[\s0-9]*$',
    producerEmailMaxLength = 50
}
