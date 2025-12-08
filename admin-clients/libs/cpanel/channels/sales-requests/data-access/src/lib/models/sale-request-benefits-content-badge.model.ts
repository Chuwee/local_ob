export type BenefitsContentBadgeType = 'EVENT_BADGE';
export const benefitsContentBadgeType: BenefitsContentBadgeType = 'EVENT_BADGE';

export const defaultBackgroundColor: string = '#E8E8E8';
export const defaultTextColor: string = '#7A7A7A';

export interface SaleRequestBenefitsContentBadge {
    visible: boolean;
    language: string;
    type: BenefitsContentBadgeType;
    value: string;
    backgroundColor: string;
    textColor: string;
}
