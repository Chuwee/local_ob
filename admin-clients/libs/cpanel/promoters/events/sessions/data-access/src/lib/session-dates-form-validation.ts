import { Event } from '@admin-clients/cpanel/promoters/events/data-access';
import { Entity } from '@admin-clients/shared/common/data-access';
import {
    dateIsAfter, dateIsBefore, dateIsSameOrAfter, dateIsSameOrBefore, dateTimeValidator, joinCrossValidations
} from '@admin-clients/shared/utility/utils';
import { Injectable } from '@angular/core';
import { AbstractControl, ValidatorFn, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { SessionStatus } from './models/session-status.enum';

@Injectable()
export class SessionDatesFormValidation {
    constructor(private _translate: TranslateService) { }

    addSessionDateValidations(
        sessionControls: {
            status?: AbstractControl;
            startDate?: AbstractControl;
            endDate?: AbstractControl;
            releaseEnable?: AbstractControl;
            releaseDate?: AbstractControl;
            bookingEnable?: AbstractControl;
            bookingStartDate?: AbstractControl;
            bookingEndDate?: AbstractControl;
            saleEnable?: AbstractControl;
            saleStartDate?: AbstractControl;
            saleEndDate?: AbstractControl;
            secondaryMarketSaleEnable?: AbstractControl;
            secondaryMarketSaleStartDate?: AbstractControl;
            secondaryMarketSaleEndDate?: AbstractControl;
            accessControlOverride?: AbstractControl;
            accessControlStartDate?: AbstractControl;
            accessControlEndDate?: AbstractControl;
        },
        event: Event,
        destroy: Observable<void>,
        entity?: Entity,
        hasFortressVenue?: boolean
    ): void {
        // TO ACCUMULATE VARIABLE BOOKING CONTROLS
        const bookingCtrToJoin: AbstractControl[] = [];
        const secondaryMarketCtrlToJoin: AbstractControl[] = [];
        // START DATE
        this.setDateValidators(
            sessionControls.startDate,
            () => sessionControls.endDate && this.mapActiveStatus(sessionControls.status.value),
            [Validators.required]
        );
        // END DATE
        this.setDateValidators(
            sessionControls.endDate,
            () => this.mapActiveStatus(sessionControls.status.value) || hasFortressVenue,
            [
                hasFortressVenue ? Validators.required : null,
                dateTimeValidator(
                    dateIsAfter, 'endDateBeforeStartDate', sessionControls.startDate,
                    this._translate.instant('DATES.START_DATE').toLowerCase()
                )
            ].filter(Boolean)
        );
        // RELEASE DATE
        this.setDateValidators(
            sessionControls.releaseDate,
            () => this.mapActiveStatus(sessionControls.status.value),
            [
                Validators.required,
                dateTimeValidator(
                    dateIsSameOrBefore, 'releaseDateAfterStartDate', sessionControls.startDate,
                    this._translate.instant('DATES.START_DATE').toLowerCase()
                ),
                event.settings.bookings?.enable &&
                dateTimeValidator(
                    dateIsSameOrBefore, 'releaseDateAfterBookingStartDate', sessionControls.bookingStartDate,
                    this._translate.instant('EVENTS.SESSION.BOOKINGS_START').toLowerCase()
                ),
                dateTimeValidator(
                    dateIsSameOrBefore, 'releaseDateAfterSaleStartDate', sessionControls.saleStartDate,
                    this._translate.instant('EVENTS.SESSION.SALE_START').toLowerCase()
                ),
                entity?.settings?.allow_secondary_market &&
                dateTimeValidator(
                    dateIsSameOrBefore, 'releaseDateAfterSecondaryMarketSaleStartDate', sessionControls.saleStartDate,
                    this._translate.instant('EVENTS.SESSION.SECONDARY_MARKET_SALE_START').toLowerCase()
                )
            ]
        );
        // BOOKING DATES
        if (event.settings.bookings?.enable && sessionControls.bookingEnable?.value) {
            // BOOKING START DATE
            this.setDateValidators(sessionControls.bookingStartDate,
                () => this.mapActiveStatus(sessionControls.status.value),
                [
                    Validators.required,
                    dateTimeValidator(
                        dateIsSameOrAfter, 'bookingStartDateBeforeReleaseDate', sessionControls.releaseDate,
                        this._translate.instant('EVENTS.SESSION.CHANNEL_RELEASE').toLowerCase()
                    ),
                    dateTimeValidator(
                        dateIsSameOrBefore, 'bookingStartDateAfterStartDate', sessionControls.startDate,
                        this._translate.instant('DATES.START_DATE').toLowerCase()
                    ),
                    dateTimeValidator(
                        dateIsBefore, 'bookingStartDateAfterBookingEndDate', sessionControls.bookingEndDate,
                        this._translate.instant('EVENTS.SESSION.BOOKINGS_END').toLowerCase()
                    )
                ]
            );
            // BOOKING END DATE
            this.setDateValidators(sessionControls.bookingEndDate,
                () => this.mapActiveStatus(sessionControls.status.value),
                [
                    Validators.required,
                    dateTimeValidator(
                        dateIsAfter, 'bookingEndDateBeforeBookingStartDate', sessionControls.bookingStartDate,
                        this._translate.instant('EVENTS.SESSION.BOOKINGS_START').toLowerCase()
                    ),
                    dateTimeValidator(
                        dateIsSameOrBefore, 'bookingEndDateAfterSaleEndDate', sessionControls.saleEndDate,
                        this._translate.instant('EVENTS.SESSION.SALE_END').toLowerCase()
                    )
                ]
            );
            bookingCtrToJoin.push(
                sessionControls.bookingEnable, sessionControls.bookingStartDate, sessionControls.bookingEndDate
            );
        }
        // SALES START DATE
        this.setDateValidators(
            sessionControls.saleStartDate,
            () => this.mapActiveStatus(sessionControls.status.value),
            [
                Validators.required,
                dateTimeValidator(
                    dateIsSameOrAfter, 'saleStartDateBeforeReleaseDate', sessionControls.releaseDate,
                    this._translate.instant('EVENTS.SESSION.CHANNEL_RELEASE').toLowerCase()
                ),
                dateTimeValidator(
                    dateIsSameOrBefore, 'saleStartDateAfterStartDate', sessionControls.startDate,
                    this._translate.instant('DATES.START_DATE').toLowerCase()
                ),
                dateTimeValidator(
                    dateIsBefore, 'saleStartDateAfterSaleEndDate', sessionControls.saleEndDate,
                    this._translate.instant('EVENTS.SESSION.SALE_END').toLowerCase()
                )
            ]
        );
        // SALES END DATE
        this.setDateValidators(
            sessionControls.saleEndDate,
            () => this.mapActiveStatus(sessionControls.status.value),
            [
                Validators.required,
                dateTimeValidator(
                    dateIsAfter, 'saleEndDateBeforeSaleStartDate', sessionControls.saleStartDate,
                    this._translate.instant('EVENTS.SESSION.SALE_START').toLowerCase()
                ),
                event.settings.bookings?.enable &&
                dateTimeValidator(
                    dateIsSameOrAfter, 'saleEndDateBeforeBookingEndDate', sessionControls.bookingEndDate,
                    this._translate.instant('EVENTS.SESSION.BOOKINGS_END').toLowerCase()
                )
            ]
        );
        if (entity?.settings?.allow_secondary_market) {
            // SECONDARY MARKET SALES START DATE
            this.setDateValidators(
                sessionControls.secondaryMarketSaleStartDate,
                () => this.mapActiveStatus(sessionControls.status.value),
                [
                    Validators.required,
                    dateTimeValidator(
                        dateIsSameOrAfter, 'secondaryMarketSaleStartDateBeforeReleaseDate', sessionControls.releaseDate,
                        this._translate.instant('EVENTS.SESSION.CHANNEL_RELEASE').toLowerCase()
                    ),
                    dateTimeValidator(
                        dateIsSameOrBefore, 'secondaryMarketSaleStartDateAfterStartDate', sessionControls.startDate,
                        this._translate.instant('DATES.START_DATE').toLowerCase()
                    ),
                    dateTimeValidator(
                        dateIsBefore, 'secondaryMarketSaleStartDateAfterSecondaryMarketSaleEndDate',
                        sessionControls.secondaryMarketSaleEndDate,
                        this._translate.instant('EVENTS.SESSION.SECONDARY_MARKET_SALE_END').toLowerCase()
                    )
                ]
            );
            // SECONDARY MARKET SALES END DATE
            this.setDateValidators(
                sessionControls.secondaryMarketSaleEndDate,
                () => this.mapActiveStatus(sessionControls.status.value),
                [
                    Validators.required,
                    dateTimeValidator(
                        dateIsAfter, 'secondaryMarketSaleEndDateBeforeSecondaryMarketSaleStartDate',
                        sessionControls.secondaryMarketSaleStartDate,
                        this._translate.instant('EVENTS.SESSION.SECONDARY_MARKET_SALE_START').toLowerCase()
                    ),
                    event.settings.bookings?.enable &&
                    dateTimeValidator(
                        dateIsSameOrAfter, 'secondaryMarketSaleEndDateBeforeBookingEndDate', sessionControls.bookingEndDate,
                        this._translate.instant('EVENTS.SESSION.BOOKINGS_END').toLowerCase()
                    )
                ]
            );
            secondaryMarketCtrlToJoin.push(
                sessionControls.secondaryMarketSaleEnable, sessionControls.secondaryMarketSaleStartDate, sessionControls.secondaryMarketSaleEndDate
            );
        }

        // VALIDATION JOIN
        joinCrossValidations([
            sessionControls.status,
            sessionControls.startDate,
            sessionControls.endDate,
            sessionControls.releaseEnable,
            sessionControls.releaseDate,
            ...bookingCtrToJoin,
            sessionControls.saleEnable,
            sessionControls.saleStartDate,
            sessionControls.saleEndDate,
            ...secondaryMarketCtrlToJoin
        ], destroy);
        // admission start/end date
        if (sessionControls.accessControlOverride && sessionControls.accessControlStartDate && sessionControls.accessControlEndDate) {
            sessionControls.accessControlStartDate.setValidators([
                Validators.required,
                dateTimeValidator(dateIsBefore, 'admissionStartAfterAdmissionEnd', sessionControls.accessControlEndDate)
            ]);
            sessionControls.accessControlEndDate.setValidators([
                Validators.required,
                dateTimeValidator(dateIsAfter, 'admissionEndBeforeAdmissionStart', sessionControls.accessControlStartDate)
            ]);
            joinCrossValidations([
                sessionControls.accessControlOverride,
                sessionControls.accessControlStartDate,
                sessionControls.accessControlEndDate
            ], destroy);
        }
    }

    private setDateValidators(abstractControl: AbstractControl, enabled: () => boolean, validators: ValidatorFn[]): void {
        if (abstractControl) {
            abstractControl.setValidators([control => {
                if (enabled()) {
                    for (const validatorFn of validators) {
                        if (validatorFn) {
                            const result = validatorFn(control);
                            if (result && Object.keys(result).length > 0) {
                                return result;
                            }
                        }
                    }
                    return null;
                } else {
                    return null;
                }
            }]);
        }
    }

    private mapActiveStatus(status: string): boolean {
        return status === SessionStatus.ready || status === SessionStatus.scheduled;
    }
}
