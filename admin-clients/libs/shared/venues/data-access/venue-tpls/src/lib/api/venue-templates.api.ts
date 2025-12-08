import { buildHttpParams } from '@OneboxTM/utils-http';
import { ListResponse } from '@OneboxTM/utils-state';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import {
    ExportJsonRequest, ExportRequest, ExportResponse, Id, IdNameListResponse
} from '@admin-clients/shared/data-access/models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { BaseVenueTemplatesRequest } from '../models/base-venue-templates-request.model';
import { CloneVenueTemplateRequest } from '../models/clone-venue-template-request.model';
import {
    BulkPutVenueTemplateElementInfoRequest
} from '../models/element-info/bulk-put-venue-template-element-info-request.model';
import { ElementsInfoFilterRequest } from '../models/element-info/get-venue-templates-element-info-request.model';
import { PostVenueTemplateElementInfoRequest } from '../models/element-info/post-venue-template-element-info-request.model';
import { PutVenueTemplateElementInfoRequest } from '../models/element-info/put-venue-template-element-info-request.model';
import { VenueTemplateElementInfoImage } from '../models/element-info/venue-template-element-info-image.model';
import { VenueTemplateElementInfoType } from '../models/element-info/venue-template-element-info-type.enum';
import { VenueTemplateElementInfo, VenueTemplateElementInfoDetail } from '../models/element-info/venue-template-element-info.model';
import { GetVenueTemplatesRequest } from '../models/get-venue-templates-request.model';
import { InteractiveVenueTemplateFileOption } from '../models/interactive/interactive-venue-template-file-option.enum';
import { VenueTemplateInteractive } from '../models/interactive/venue-template-interactive.model';
import { PostVenueTemplateRequest } from '../models/post-venue-template-request.model';
import { PutVenueTemplateRequest } from '../models/put-venue-template-request.model';
import { VenueTemplateBlockingReason, VenueTemplateBlockingReasonPost, VenueTemplateBlockingReasonPut } from '../models/venue-template-blocking-reason.model';
import {
    PostVenueTplDynamicTagGroupLabel, PutVenueTplDynamicTagGroupLabel, VenueTplDynamicTagGroup, VenueTplDynamicTagGroupLabel
} from '../models/venue-template-dynamic-tag-groups.model';
import { PostVenueTemplateGate, VenueTemplateGate } from '../models/venue-template-gate.model';
import { PostVenueTemplatePriceType, VenueTemplatePriceType } from '../models/venue-template-price-type.model';
import { PostVenueTemplateQuota, VenueTemplateQuota } from '../models/venue-template-quota.model';
import { VenueTemplate } from '../models/venue-template.model';

@Injectable({ providedIn: 'root' })
export class VenueTemplatesApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly VENUE_TEMPLATES_API = `${this.BASE_API}/mgmt-api/v1/venue-templates`;
    private readonly ELEMENTS_INFO = 'elements-info';
    private readonly DYNAMIC_TAG_GROUPS = '/dynamic-tag-groups';
    private readonly DYNAMIC_TAGS = '/tags';

    private readonly _http = inject(HttpClient);

    getVenueTemplates(request: GetVenueTemplatesRequest): Observable<ListResponse<VenueTemplate>> {
        const params = buildHttpParams({
            limit: request.limit,
            offset: request.offset,
            sort: request.sort,
            q: request.filter,
            venue_id: request.venueId,
            entity_id: request.entityId,
            venue_entity_id: request.venueEntityId,
            event_id: request.eventId,
            scope: request.scope,
            type: request.type,
            status: request.status,
            public: request.publicTpl,
            include_third_party_templates: request.includeThirdPartyTemplates,
            graphic: request.graphic,
            venue_city: request.city,
            has_avet_mapping: request.has_avet_mapping,
            inventory_provider: request.inventory_provider
        });
        return this._http.get<ListResponse<VenueTemplate>>(this.VENUE_TEMPLATES_API, { params });
    }

    getVenueTemplate(venueTemplateId: number): Observable<VenueTemplate> {
        return this._http.get<VenueTemplate>(`${this.VENUE_TEMPLATES_API}/${venueTemplateId}`);
    }

    postVenueTemplate(req: PostVenueTemplateRequest): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(`${this.VENUE_TEMPLATES_API}`, req);
    }

    cloneVenueTemplate(fromVenueTemplateId: number, request: CloneVenueTemplateRequest): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(`${this.VENUE_TEMPLATES_API}/${fromVenueTemplateId}/clone`, request);
    }

    putVenueTemplate(templateId: number, saveValue: PutVenueTemplateRequest): Observable<void> {
        return this._http.put<void>(`${this.VENUE_TEMPLATES_API}/${templateId}`, saveValue);
    }

    deleteVenueTemplate(venueTemplateId: string): Observable<void> {
        return this._http.delete<void>(`${this.VENUE_TEMPLATES_API}/${venueTemplateId}`);
    }

    // TEMPLATE INFO

    getVenueTemplatesElementInfo(templateId: number, request: ElementsInfoFilterRequest): Observable<ListResponse<VenueTemplateElementInfo>> {
        const params = buildHttpParams({
            limit: request.limit,
            offset: request.offset,
            sort: request.sort,
            q: request.q,
            type: request.type
        });
        return this._http.get<ListResponse<VenueTemplateElementInfo>>(
            `${this.VENUE_TEMPLATES_API}/${templateId}/${this.ELEMENTS_INFO}`, { params }
        );
    }

    getVenueTemplateElementInfo(venueTemplateId: number, elementInfoId: number, type: VenueTemplateElementInfoType): Observable<VenueTemplateElementInfoDetail> {
        return this._http.get<VenueTemplateElementInfoDetail>(
            `${this.VENUE_TEMPLATES_API}/${venueTemplateId}/${this.ELEMENTS_INFO}/${type}/${elementInfoId}`
        );
    }

    postVenueTemplateElementInfo(venueTemplateId: number, req: PostVenueTemplateElementInfoRequest): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(`${this.VENUE_TEMPLATES_API}/${venueTemplateId}/${this.ELEMENTS_INFO}`, req);
    }

    putVenueTemplateElementInfo(
        venueTemplateId: number, elementInfoId: number, type: VenueTemplateElementInfoType, saveValue: PutVenueTemplateElementInfoRequest
    ): Observable<void> {
        return this._http.put<void>(`${this.VENUE_TEMPLATES_API}/${venueTemplateId}/${this.ELEMENTS_INFO}/${type}/${elementInfoId}`, saveValue);
    }

    putMultipleVenueTemplateElementInfo(
        venueTemplateId: number, saveValue: BulkPutVenueTemplateElementInfoRequest, filters: ElementsInfoFilterRequest = {}
    ): Observable<void> {
        const params = buildHttpParams({ q: filters?.q?.length ? filters.q : null, type: filters.type });
        return this._http.put<void>(`${this.VENUE_TEMPLATES_API}/${venueTemplateId}/${this.ELEMENTS_INFO}/`, saveValue, { params });
    }

    deleteVenueTemplateElementInfo(venueTemplateId: number, elementInfoId: string): Observable<void> {
        return this._http.delete<void>(`${this.VENUE_TEMPLATES_API}/${venueTemplateId}/${this.ELEMENTS_INFO}/${elementInfoId}`);
    }

    deleteVenueTemplateElementInfos(venueTemplateId: number, elements: string[]): Observable<void> {
        const params = buildHttpParams({ elements });
        return this._http.delete<void>(`${this.VENUE_TEMPLATES_API}/${venueTemplateId}/${this.ELEMENTS_INFO}`, { params });
    }

    deleteAllVenueTemplateElementInfos(venueTemplateId: number, filters: ElementsInfoFilterRequest = {}): Observable<void> {
        const params = buildHttpParams({ all_elements: true, q: filters?.q?.length ? filters.q : null, type: filters.type });
        return this._http.delete<void>(`${this.VENUE_TEMPLATES_API}/${venueTemplateId}/${this.ELEMENTS_INFO}`, { params });
    }

    postVenueTemplateElementInfoImages(venueTemplateId: number, elementInfoId: number, type: VenueTemplateElementInfoType, contents: VenueTemplateElementInfoImage[]): Observable<void> {
        return this._http.post<void>(`${this.VENUE_TEMPLATES_API}/${venueTemplateId}/${this.ELEMENTS_INFO}/${type}/${elementInfoId}/images`, contents);
    }

    deleteVenueTemplateElementInfoImage(
        venueTemplateId: number, elementInfoId: number, type: VenueTemplateElementInfoType, language: string, imageType: 'SLIDER' | 'HIGHLIGHTED', position: number
    ): Observable<void> {
        const params = buildHttpParams({ position });
        return this._http.delete<void>(
            `${this.VENUE_TEMPLATES_API}/${venueTemplateId}/${this.ELEMENTS_INFO}/${type}/${elementInfoId}/images/${imageType}/languages/${language}`,
            { params }
        );
    }

    // PRICE TYPES

    getVenueTplPriceTypes(venueTplId: number): Observable<VenueTemplatePriceType[]> {
        return this._http.get<VenueTemplatePriceType[]>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/price-types`);
    }

    postVenueTplPriceType(venueTplId: number, priceType: PostVenueTemplatePriceType): Observable<number> {
        return this._http.post<{ id: number }>(
            `${this.VENUE_TEMPLATES_API}/${venueTplId}/price-types`,
            {
                name: priceType.name,
                code: priceType.code,
                color: priceType.color?.replace('#', '')
            }).pipe(map(idObject => idObject.id));
    }

    putVenueTplPriceType(venueTplId: number, priceType: VenueTemplatePriceType): Observable<VenueTemplatePriceType> {
        const newPriceType = {
            id: priceType.id,
            ticket_type: priceType.ticketType,
            name: priceType.name ? priceType.name : null,
            code: priceType.code ? priceType.code : null,
            color: priceType.color ? priceType.color.replace('#', '') : null,
            priority: priceType.priority === 0 || priceType.priority ? priceType.priority : null,
            additional_config: priceType.additional_config
        } as VenueTemplatePriceType;

        for (const key in newPriceType) {
            if (newPriceType[key] !== 0 && !newPriceType[key]) {
                delete newPriceType[key];
            }
        }
        return this._http.put<VenueTemplatePriceType>(
            `${this.VENUE_TEMPLATES_API}/${venueTplId}/price-types/${priceType.id}`, newPriceType);
    }

    deleteVenueTplPriceType(venueTplId: number, priceTypeId: string): Observable<void> {
        return this._http.delete<void>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/price-types/${priceTypeId}`);
    }

    // QUOTA

    getVenueTplQuotas(venueTplId: number): Observable<VenueTemplateQuota[]> {
        return this._http.get<VenueTemplateQuota[]>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/quotas`);
    }

    postVenueTplQuota(venueTplId: number, quota: PostVenueTemplateQuota): Observable<number> {
        return this._http.post<{ id: number }>(
            `${this.VENUE_TEMPLATES_API}/${venueTplId}/quotas`,
            {
                name: quota.name,
                code: quota.code,
                color: quota.color?.replace('#', '')
            }).pipe(map(idObject => idObject.id));
    }

    putVenueTplQuota(venueTplId: number, quota: VenueTemplateQuota): Observable<VenueTemplateQuota> {
        return this._http.put<VenueTemplateQuota>(
            `${this.VENUE_TEMPLATES_API}/${venueTplId}/quotas/${quota.id}`,
            {
                id: quota.id,
                name: quota.name,
                code: quota.code,
                color: quota.color?.replace('#', '')
            });
    }

    deleteVenueTplQuota(venueTplId: number, quotaId: string): Observable<void> {
        return this._http.delete<void>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/quotas/${quotaId}`);
    }

    // BLOCKING REASON

    getVenueTplBlockingReasons(venueTplId: number): Observable<VenueTemplateBlockingReason[]> {
        return this._http.get<VenueTemplateBlockingReason[]>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/blocking-reasons`);
    }

    postVenueTplBlockingReason(venueTplId: number, post: VenueTemplateBlockingReasonPost): Observable<number> {
        return this._http.post<{ id: number }>(
            `${this.VENUE_TEMPLATES_API}/${venueTplId}/blocking-reasons`,
            {
                name: post.name,
                color: post.color?.replace('#', '')
            }).pipe(map(idObject => idObject.id));
    }

    putVenueTplBlockingReason(venueTplId: number, blockingReason: VenueTemplateBlockingReasonPut): Observable<VenueTemplateBlockingReason> {
        blockingReason.color = blockingReason.color?.replace('#', '');
        return this._http.put<VenueTemplateBlockingReason>(
            `${this.VENUE_TEMPLATES_API}/${venueTplId}/blocking-reasons/${blockingReason.id}`, blockingReason
        );
    }

    deleteVenueTplBlockingReason(venueTplId: number, blockingReasonId: string): Observable<void> {
        return this._http.delete<void>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/blocking-reasons/${blockingReasonId}`);
    }

    // GATE

    getVenueTplGates(venueTplId: number): Observable<VenueTemplateGate[]> {
        return this._http.get<VenueTemplateGate[]>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/gates`);
    }

    postVenueTplGate(venueTplId: number, gate: PostVenueTemplateGate): Observable<number> {
        return this._http.post<{ id: number }>(
            `${this.VENUE_TEMPLATES_API}/${venueTplId}/gates`,
            {
                name: gate.name,
                code: gate.code,
                color: gate.color?.replace('#', '')
            }).pipe(map(idObject => idObject.id));
    }

    putVenueTplGate(venueTplId: number, gate: VenueTemplateGate): Observable<VenueTemplateGate> {
        return this._http.put<VenueTemplateGate>(
            `${this.VENUE_TEMPLATES_API}/${venueTplId}/gates/${gate.id}`,
            {
                id: gate.id,
                name: gate.name,
                code: gate.code,
                color: gate.color?.replace('#', ''),
                default: gate.default
            });
    }

    deleteVenueTplGate(venueTplId: number, gateId: string): Observable<void> {
        return this._http.delete<void>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/gates/${gateId}`);
    }

    // FILTER OPTIONS

    getFilterOptions(filterName: string, request: BaseVenueTemplatesRequest): Observable<IdNameListResponse> {
        const params = buildHttpParams(request);
        return this._http.get<IdNameListResponse>(this.VENUE_TEMPLATES_API + '/filters/' + filterName, { params });
    }

    // INTERACTIVE OPTIONS

    getVenueTplInteractiveOptions(venueTplId: number): Observable<VenueTemplateInteractive> {
        return this._http.get<VenueTemplateInteractive>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/interactive-venue`);
    }

    putVenueTplInteractiveOptions(venueTplId: number, interactiveOptions: VenueTemplateInteractive): Observable<VenueTemplateInteractive> {
        return this._http.put<VenueTemplateInteractive>(
            `${this.VENUE_TEMPLATES_API}/${venueTplId}/interactive-venue`,
            { ...interactiveOptions });
    }

    // Venue template export list file
    postInteractiveVenueTemplateFile<T extends ExportRequest | ExportJsonRequest>(
        venueTemplateId: number, request: T, exportType: InteractiveVenueTemplateFileOption
    ): Observable<ExportResponse> {
        return this._http.post<ExportResponse>(`${this.VENUE_TEMPLATES_API}/${venueTemplateId}/exports/${exportType}`, request);
    }

    // CUSTOM TAG GROUPS

    getVenueTplCustomTagGroups(venueTplId: number): Observable<VenueTplDynamicTagGroup[]> {
        return this._http.get<VenueTplDynamicTagGroup[]>(`${this.VENUE_TEMPLATES_API}/${venueTplId}${this.DYNAMIC_TAG_GROUPS}`);
    }

    postVenueTplCustomTagGroup(venueTplId: number, tagGroup: VenueTplDynamicTagGroup): Observable<Id> {
        return this._http.post<Id>(`${this.VENUE_TEMPLATES_API}/${venueTplId}${this.DYNAMIC_TAG_GROUPS}`, tagGroup);
    }

    putVenueTplCustomTagGroup(venueTplId: number, tagGroup: VenueTplDynamicTagGroup): Observable<void> {
        return this._http.put<void>(`${this.VENUE_TEMPLATES_API}/${venueTplId}${this.DYNAMIC_TAG_GROUPS}/${tagGroup.id}`, tagGroup);
    }

    deleteVenueTplCustomTagGroup(venueTplId: number, groupId: number): Observable<void> {
        return this._http.delete<void>(`${this.VENUE_TEMPLATES_API}/${venueTplId}${this.DYNAMIC_TAG_GROUPS}/${groupId}`);
    }

    // CUSTOM TAG GROUP TAGS

    getVenueTplCustomTagGroupLabels(venueTplId: number, groupId: number): Observable<VenueTplDynamicTagGroupLabel[]> {
        return this._http.get<VenueTplDynamicTagGroupLabel[]>(
            `${this.VENUE_TEMPLATES_API}/${venueTplId}${this.DYNAMIC_TAG_GROUPS}/${groupId}${this.DYNAMIC_TAGS}`);
    }

    postVenueTplCustomTagGroupLabel(venueTplId: number, groupId: number, label: PostVenueTplDynamicTagGroupLabel): Observable<Id> {
        return this._http.post<Id>(
            `${this.VENUE_TEMPLATES_API}/${venueTplId}${this.DYNAMIC_TAG_GROUPS}/${groupId}${this.DYNAMIC_TAGS}`,
            {
                name: label.name,
                code: label.code,
                color: label.color?.replace('#', '')
            });
    }

    putVenueTplCustomTagGroupLabel(
        venueTplId: number, groupId: number, labelId: number, updatedLabel: PutVenueTplDynamicTagGroupLabel
    ): Observable<void> {
        return this._http.put<void>(
            `${this.VENUE_TEMPLATES_API}/${venueTplId}${this.DYNAMIC_TAG_GROUPS}/${groupId}${this.DYNAMIC_TAGS}/${labelId}`,
            {
                name: updatedLabel.name,
                code: updatedLabel.code,
                color: updatedLabel.color?.replace('#', '')
            });
    }

    deleteVenueTplCustomTagGroupLabel(venueTplId: number, groupId: number, labelId: string): Observable<void> {
        return this._http.delete<void>(
            `${this.VENUE_TEMPLATES_API}/${venueTplId}${this.DYNAMIC_TAG_GROUPS}/${groupId}${this.DYNAMIC_TAGS}/${labelId}`);
    }

    // ---------------------------------------------
}
