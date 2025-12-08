import {
    PromotionStatus,
    PromotionValidityPeriodType,
    PromotionWithStatusAndValidity
} from '@admin-clients/cpanel-common-promotions-utility-models';
import moment from 'moment';

export function promotionVirtualActiveStatus(
    startDate: moment.Moment,
    endDate: moment.Moment
): PromotionStatus {
    const today = moment();
    if (!!startDate && !!endDate) {
        if (!startDate.isSameOrBefore(today)) {
            return PromotionStatus.upcoming;
        }
        if (!endDate.isSameOrAfter(today)) {
            return PromotionStatus.expired;
        }
    }
    return PromotionStatus.active;
}

export function mapPromoVirtualStatus<T extends PromotionWithStatusAndValidity>() {
    return (promo: T) => {
        if (promo.status === PromotionStatus.active &&
            (promo.validity_period?.type === PromotionValidityPeriodType.period || promo.dates?.start && promo.dates?.end)) {
            promo.status = promotionVirtualActiveStatus(
                moment(promo.validity_period?.start_date || promo.dates?.start),
                moment(promo.validity_period?.end_date || promo.dates?.end)
            );
        }
        return promo;
    };
}
