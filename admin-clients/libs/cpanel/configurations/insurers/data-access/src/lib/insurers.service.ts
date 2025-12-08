import { getListData, getMetadata, mapMetadata, StateManager } from '@OneboxTM/utils-state';
import { RangeElement } from '@admin-clients/shared-utility-models';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { InsurersApi } from './api/insurers.api';
import { Insurer, PostInsurer } from './models/insurer.model';
import { PutPolicyTermsConditionsFile, PutPolicyTermsConditionsPayload } from './models/policy-terms-conditions.model';
import { PostPolicy } from './models/policy.model';
import { PutInsurerPolicy } from './models/put-insurer-policy.model';
import { InsurersState } from './state/insurers.state';

@Injectable()
export class InsurersService {
    readonly #api = inject(InsurersApi);
    readonly #state = inject(InsurersState);

    readonly insurer = Object.freeze({
        load: (insurerId: number) => StateManager.load(
            this.#state.insurer,
            this.#api.getInsurer(insurerId)
        ),
        get$: () => this.#state.insurer.getValue$(),
        inProgress$: () => this.#state.insurer.isInProgress$(),
        clear: () => this.#state.insurer.setValue(null),
        error$: () => this.#state.insurer.getError$(),
        create: (insurer: PostInsurer): Observable<{ id: number }> => StateManager.inProgress(
            this.#state.insurer,
            this.#api.postInsurer(insurer)
        ),
        update: (insurerId: number, reqBody: Partial<Insurer>) => StateManager.inProgress(
            this.#state.insurer,
            this.#api.putInsurer(insurerId, reqBody)
        )
    });

    readonly insurersList = Object.freeze({
        load: () => StateManager.load(
            this.#state.insurersList,
            this.#api.getInsurers().pipe(mapMetadata())
        ),
        getData$: () => this.#state.insurersList.getValue$().pipe(getListData()),
        getMetadata$: () => this.#state.insurersList.getValue$().pipe(getMetadata()),
        loading$: () => this.#state.insurersList.isInProgress$(),
        clear: () => this.#state.insurersList.setValue(null)
    });

    readonly policy = Object.freeze({
        load: (insurerId: number, policyId: number) => StateManager.load(
            this.#state.policy,
            this.#api.getPolicy(insurerId, policyId)
        ),
        get$: () => this.#state.policy.getValue$(),
        inProgress$: () => this.#state.policy.isInProgress$(),
        clear: () => this.#state.policy.setValue(null),
        error$: () => this.#state.policy.getError$(),
        update: (insurerId: number, policyId: number, reqBody: Partial<PutInsurerPolicy>) => StateManager.inProgress(
            this.#state.policy,
            this.#api.putInsurerPolicy(insurerId, policyId, reqBody)
        ),
        create: (insurerId: number, body: PostPolicy): Observable<{ id: number }> => StateManager.inProgress(
            this.#state.insurer,
            this.#api.postPolicy(insurerId, body)
        )
    });

    readonly policiesList = Object.freeze({
        load: (insurerId: number) => StateManager.load(
            this.#state.policiesList,
            this.#api.getPolicies(insurerId).pipe(mapMetadata())
        ),
        getData$: () => this.#state.policiesList.getValue$().pipe(getListData()),
        getMetadata$: () => this.#state.policiesList.getValue$().pipe(getMetadata()),
        loading$: () => this.#state.policiesList.isInProgress$(),
        clear: () => this.#state.policiesList.setValue(null)
    });

    readonly policyRanges = Object.freeze({
        load: (insurerId: number, policyId: number) => StateManager.load(
            this.#state.policyRanges,
            this.#api.getPolicyRanges(insurerId, policyId)
        ),
        get$: () => this.#state.policyRanges.getValue$(),
        inProgress$: () => this.#state.policyRanges.isInProgress$(),
        clear: () => this.#state.policyRanges.setValue(null),
        error$: () => this.#state.policyRanges.getError$(),
        post: (insurerId: number, policyId: number, body: RangeElement[]) => StateManager.inProgress(
            this.#state.policyRanges,
            this.#api.postPolicyRanges(insurerId, policyId, body)
        )
    });

    readonly policyTermsConditions = Object.freeze({
        load: (insurerId: number, policyId: number) => StateManager.load(
            this.#state.policyTermsConditions,
            this.#api.getPolicyTermsConditions(insurerId, policyId).pipe(mapMetadata())
        ),
        getData$: () => this.#state.policyTermsConditions.getValue$().pipe(getListData()),
        getMetadata$: () => this.#state.policyTermsConditions.getValue$().pipe(getMetadata()),
        loading$: () => this.#state.policyTermsConditions.isInProgress$(),
        clear: () => this.#state.policyTermsConditions.setValue(null),
        update: (insurerId: number, policyId: number, termsId: number, reqBody: Partial<PutPolicyTermsConditionsPayload>) =>
            StateManager.inProgress(
                this.#state.policyTermsConditions,
                this.#api.putInsurerPolicyTermsConditions(insurerId, policyId, termsId, reqBody)
            )
    });

    readonly policyTermsConditionsFile = Object.freeze({
        load: (insurerId: number, policyId: number, termsId: number) => StateManager.load(
            this.#state.policyTermsConditionsFile,
            this.#api.getTermsConditionsFile(insurerId, policyId, termsId)
        ),
        get$: () => this.#state.policyTermsConditionsFile.getValue$(),
        inProgress$: () => this.#state.policyTermsConditionsFile.isInProgress$(),
        clear: () => this.#state.policyTermsConditionsFile.setValue(null),
        error$: () => this.#state.policyTermsConditionsFile.getError$(),
        update: (insurerId: number, policyId: number, termsId: number, reqBody: Partial<PutPolicyTermsConditionsFile>) =>
            StateManager.inProgress(
                this.#state.policyTermsConditionsFile,
                this.#api.putTermsConditionsFile(insurerId, policyId, termsId, reqBody)
            )
    });
}
