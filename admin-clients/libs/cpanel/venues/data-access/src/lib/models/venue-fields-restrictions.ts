export const venueFieldsRestrictions = {
    venuePhonePattern: /^[+]{0,1}[\s0-9]*$/,
    venueCapacityMinLength: 1,
    venueLatitudeMinValue: -90.0,
    venueLatitudeMaxValue: 90.0,
    venueLongitudeMinValue: -180.0,
    venueLongitudeMaxValue: 180.0,
    venueCoordinatesMaxDecimals: 7
};
