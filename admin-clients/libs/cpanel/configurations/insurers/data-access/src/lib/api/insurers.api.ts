import { ListResponse } from '@OneboxTM/utils-state';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { RangeElement } from '@admin-clients/shared-utility-models';
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Insurer, InsurerListElem, PostInsurer } from '../models/insurer.model';
import { PolicyTermsConditions, PutPolicyTermsConditionsFile, PutPolicyTermsConditionsPayload } from '../models/policy-terms-conditions.model';
import { Policy, PostPolicy } from '../models/policy.model';
import { PutInsurerPolicy } from '../models/put-insurer-policy.model';

@Injectable()
export class InsurersApi {
    readonly #BASE_API = inject(APP_BASE_API);
    readonly #INSURERS_API = `${this.#BASE_API}/mgmt-api/v1/insurers`;

    readonly #http = inject(HttpClient);

    getInsurers(): Observable<ListResponse<InsurerListElem>> {
        return this.#http.get<ListResponse<InsurerListElem>>(this.#INSURERS_API);
    }

    getInsurer(insurerId: number): Observable<Insurer> {
        return this.#http.get<Insurer>(`${this.#INSURERS_API}/${insurerId}`);
    }

    putInsurer(insurerId: number, reqBody: Partial<Insurer>): Observable<void> {
        return this.#http.put<void>(`${this.#INSURERS_API}/${insurerId}`, reqBody);
    }

    postInsurer(insurer: PostInsurer): Observable<{ id: number }> {
        return this.#http.post<{ id: number }>(this.#INSURERS_API, insurer);
    }

    getPolicies(insurerId: number): Observable<ListResponse<Policy>> {
        return this.#http.get<ListResponse<Policy>>(`${this.#INSURERS_API}/${insurerId}/policies/`);
    }

    getPolicy(insurerId: number, policyId: number): Observable<Policy> {
        return this.#http.get<Policy>(`${this.#INSURERS_API}/${insurerId}/policies/${policyId}`);
    }

    putInsurerPolicy(insurerId: number, policyId: number, reqBody: Partial<PutInsurerPolicy>): Observable<void> {
        return this.#http.put<void>(`${this.#INSURERS_API}/${insurerId}/policies/${policyId}`, reqBody);
    }

    postPolicy(insurerId: number, body: PostPolicy): Observable<{ id: number }> {
        return this.#http.post<{ id: number }>(`${this.#INSURERS_API}/${insurerId}/policies`, body);
    }

    getPolicyRanges(insurerId: number, policyId: number): Observable<RangeElement[]> {
        return this.#http.get<RangeElement[]>(`${this.#INSURERS_API}/${insurerId}/policies/${policyId}/ranges`);
    }

    postPolicyRanges(insurerId: number, policyId: number, body: RangeElement[]): Observable<void> {
        return this.#http.post<void>(`${this.#INSURERS_API}/${insurerId}/policies/${policyId}/ranges`, body);
    }

    getPolicyTermsConditions(insurerId: number, policyId: number): Observable<ListResponse<PolicyTermsConditions>> {
        return this.#http.get<ListResponse<PolicyTermsConditions>>(`${this.#INSURERS_API}/${insurerId}/policies/${policyId}/terms-conditions`);
    }

    putInsurerPolicyTermsConditions(insurerId: number, policyId: number, termsId: number, reqBody: Partial<PutPolicyTermsConditionsPayload>): Observable<void> {
        return this.#http.put<void>(`${this.#INSURERS_API}/${insurerId}/policies/${policyId}/terms-conditions/${termsId}`, reqBody);
    }

    getTermsConditionsFile(insurerId: number, policyId: number, termsId: number): Observable<string> {
        return this.#http.get<string>(`${this.#INSURERS_API}/${insurerId}/policies/${policyId}/terms-conditions/${termsId}/file-content`);
    }

    putTermsConditionsFile(insurerId: number, policyId: number, termsId: number, reqBody: Partial<PutPolicyTermsConditionsFile>): Observable<void> {
        return this.#http.put<void>(`${this.#INSURERS_API}/${insurerId}/policies/${policyId}/terms-conditions/${termsId}/file-content`, reqBody);
    }

}