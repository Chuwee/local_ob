import { ListResponse, StateProperty } from '@OneboxTM/utils-state';
import { RangeElement } from '@admin-clients/shared-utility-models';
import { Injectable } from '@angular/core';
import { Insurer, InsurerListElem } from '../models/insurer.model';
import { PolicyTermsConditions } from '../models/policy-terms-conditions.model';
import { Policy } from '../models/policy.model';

@Injectable()
export class InsurersState {
    readonly insurersList = new StateProperty<ListResponse<InsurerListElem>>();
    readonly insurer = new StateProperty<Insurer>();
    readonly policiesList = new StateProperty<ListResponse<Policy>>();
    readonly policy = new StateProperty<Policy>();
    readonly policyRanges = new StateProperty<RangeElement[]>();
    readonly policyTermsConditions = new StateProperty<ListResponse<PolicyTermsConditions>>();
    readonly policyTermsConditionsFile = new StateProperty<string>();
}
