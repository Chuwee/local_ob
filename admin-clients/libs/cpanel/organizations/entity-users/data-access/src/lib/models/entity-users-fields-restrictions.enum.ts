export enum UserFieldsRestrictions {
    userNameLength = 50,
    userSurnameLength = 50,
    userEmailLength = 150,
    userPositionLength = 100,
    userPhoneLength = 15,
    userPhonePattern = '^[+]{0,1}[\s0-9]*$',
    userCityMaxLength = 50,
    userAdressMaxLength = 50,
    userPostalCodeMaxLength = 10,
    userPostalCodePattern = '[\.a-zA-Z0-9_-\\s]*',
    notesMaxLength = 1000
}
