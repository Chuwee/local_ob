import { ListResponse, Metadata, StateManager, StateProperty } from '@OneboxTM/utils-state';
import {
    ExportJsonRequest, ExportRequest, ExportResponse, Id, IdName
} from '@admin-clients/shared/data-access/models';
import { fetchAll } from '@admin-clients/shared/utility/utils';
import { HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { forkJoin, Observable, of, Subject, switchMap, zip } from 'rxjs';
import { catchError, finalize, map, takeUntil, tap } from 'rxjs/operators';
import { VenueTemplatesApi } from './api/venue-templates.api';
import { BaseVenueTemplatesRequest } from './models/base-venue-templates-request.model';
import { CloneVenueTemplateRequest } from './models/clone-venue-template-request.model';
import {
    BulkPutVenueTemplateElementInfoRequest
} from './models/element-info/bulk-put-venue-template-element-info-request.model';
import { ElementsInfoFilterRequest } from './models/element-info/get-venue-templates-element-info-request.model';
import { PostVenueTemplateElementInfoRequest } from './models/element-info/post-venue-template-element-info-request.model';
import { PutVenueTemplateElementInfoRequest } from './models/element-info/put-venue-template-element-info-request.model';
import { VenueTemplateElementInfoImage } from './models/element-info/venue-template-element-info-image.model';
import { VenueTemplateElementInfoType } from './models/element-info/venue-template-element-info-type.enum';
import { VenueTemplateElementInfo } from './models/element-info/venue-template-element-info.model';
import { GetVenueTemplatesRequest } from './models/get-venue-templates-request.model';
import { InteractiveVenueTemplateFileOption } from './models/interactive/interactive-venue-template-file-option.enum';
import { VenueTemplateInteractive } from './models/interactive/venue-template-interactive.model';
import { PostVenueTemplateRequest } from './models/post-venue-template-request.model';
import { PutVenueTemplateRequest } from './models/put-venue-template-request.model';
import {
    VenueTemplateBlockingReason, VenueTemplateBlockingReasonPost, VenueTemplateBlockingReasonPut
} from './models/venue-template-blocking-reason.model';
import {
    PostVenueTplDynamicTagGroupLabel, PutVenueTplDynamicTagGroupLabel, VenueTplDynamicTagGroup, VenueTplDynamicTagGroupLabel
} from './models/venue-template-dynamic-tag-groups.model';
import { PostVenueTemplateGate, VenueTemplateGate } from './models/venue-template-gate.model';
import { VenueTemplatePriceTypeGrouped } from './models/venue-template-price-type-grouped.model';
import { PostVenueTemplatePriceType, VenueTemplatePriceType } from './models/venue-template-price-type.model';
import { PostVenueTemplateQuota, VenueTemplateQuota } from './models/venue-template-quota.model';
import { VenueTemplate } from './models/venue-template.model';
import { VenueTemplatesState } from './state/venue-templates.state';

@Injectable({ providedIn: 'root' })
export class VenueTemplatesService {

    private _templateChange = new Subject<void>();

    private readonly _venueTemplatesState = inject(VenueTemplatesState);
    private readonly _venueTemplatesApi = inject(VenueTemplatesApi);

    readonly venueTpl = Object.freeze({
        load: (tplId: number) => StateManager.load(
            this._venueTemplatesState.venueTpl,
            this._venueTemplatesApi.getVenueTemplate(tplId)
        ),
        cancelLoad: () => this._venueTemplatesState.venueTpl.triggerCancellation(),
        get$: () => this._venueTemplatesState.venueTpl.getValue$(),
        error$: () => this._venueTemplatesState.venueTpl.getError$(),
        inProgress$: () => this._venueTemplatesState.venueTpl.isInProgress$(),
        clear: () => {
            this._venueTemplatesState.venueTpl.setError(null);
            this._venueTemplatesState.venueTpl.setValue(null);
        }
    });

    readonly venueTplsElementInfo = Object.freeze({
        load: (tplId: number, request: ElementsInfoFilterRequest) => StateManager.load(
            this._venueTemplatesState.venueTplElementInfoList,
            this._venueTemplatesApi.getVenueTemplatesElementInfo(tplId, request)
        ),
        loadAll: (tplId: number, request: ElementsInfoFilterRequest) => {
            const req: ElementsInfoFilterRequest = Object.assign({ offset: 0, limit: 999 }, request);

            return StateManager.load(
                this._venueTemplatesState.venueTplElementInfoList,
                fetchAll((offset: number) => this._venueTemplatesApi.getVenueTemplatesElementInfo(tplId, { ...req, offset }))
            );
        },
        getData$: () => this._venueTemplatesState.venueTplElementInfoList.getValue$()
            .pipe(map(venueTemplates => venueTemplates?.data)),
        getMetadata$: () => this._venueTemplatesState.venueTplElementInfoList.getValue$()
            .pipe(map(response =>
                (response?.metadata && Object.assign(new Metadata(), response.metadata)))),
        inProgress$: () => this._venueTemplatesState.venueTplElementInfoList.isInProgress$(),
        clear: () => {
            this._venueTemplatesState.venueTplElementInfoList.setError(null);
            this._venueTemplatesState.venueTplElementInfoList.setValue(null);
        }
    });

    readonly venueTplElementInfo = Object.freeze({
        load: (templateId: number, elementInfoId: number, type: VenueTemplateElementInfoType): void => StateManager.load(
            this._venueTemplatesState.venueTplElementInfo,
            this._venueTemplatesApi.getVenueTemplateElementInfo(templateId, elementInfoId, type)
        ),
        create: (templateId: number, elementInfo: PostVenueTemplateElementInfoRequest) =>
            StateManager.inProgress(
                this._venueTemplatesState.venueTplElementInfo,
                this._venueTemplatesApi.postVenueTemplateElementInfo(templateId, elementInfo)
            ),
        update: (
            templateId: number, elementInfoId: number, type: VenueTemplateElementInfoType, elementInfo: PutVenueTemplateElementInfoRequest
        ) =>
            StateManager.inProgress(
                this._venueTemplatesState.venueTplElementInfo,
                this._venueTemplatesApi.putVenueTemplateElementInfo(templateId, elementInfoId, type, elementInfo)
            ),
        updateMultiple: (
            templateId: number,
            elements: VenueTemplateElementInfo[],
            elementInfo: PutVenueTemplateElementInfoRequest,
            all: boolean,
            filters?: ElementsInfoFilterRequest
        ) =>
            StateManager.inProgress(
                this._venueTemplatesState.venueTplElementInfo,
                ((templateId, elements, elementInfo, all, filters) => {
                    const bulkReq: BulkPutVenueTemplateElementInfoRequest = {
                        update_all_elements_info: true,
                        element_info: elementInfo,
                        elements_type_related_id_map: null
                    };
                    if (!all) {
                        bulkReq.update_all_elements_info = false;
                        bulkReq.elements_type_related_id_map = {
                            [VenueTemplateElementInfoType.nnz]: elements
                                .filter(elementInfo => elementInfo.type === VenueTemplateElementInfoType.nnz)
                                .map(elementInfo => elementInfo.id),
                            [VenueTemplateElementInfoType.priceType]: elements
                                .filter(elementInfo => elementInfo.type === VenueTemplateElementInfoType.priceType)
                                .map(elementInfo => elementInfo.id),
                            [VenueTemplateElementInfoType.view]: elements
                                .filter(elementInfo => elementInfo.type === VenueTemplateElementInfoType.view)
                                .map(elementInfo => elementInfo.id)
                        };
                    }
                    return this._venueTemplatesApi.putMultipleVenueTemplateElementInfo(templateId, bulkReq, filters);
                })(templateId, elements, elementInfo, all, filters)
            ),
        delete: (templateId: number, elements: string[], all: boolean, filters?: ElementsInfoFilterRequest) =>
            StateManager.inProgress(
                this._venueTemplatesState.venueTplElementInfo,
                ((templateId, elements, all, filters) => {
                    let obs$: Observable<void>;
                    if (elements.length === 1) {
                        obs$ = this._venueTemplatesApi.deleteVenueTemplateElementInfo(templateId, elements[0]);
                    } else if (all) {
                        obs$ = this._venueTemplatesApi.deleteAllVenueTemplateElementInfos(templateId, filters);
                    } else {
                        obs$ = this._venueTemplatesApi.deleteVenueTemplateElementInfos(templateId, elements);
                    }
                    return obs$;
                })(templateId, elements, all, filters)
            ),
        get$: () => this._venueTemplatesState.venueTplElementInfo.getValue$(),
        inProgress$: () => this._venueTemplatesState.venueTplElementInfo.isInProgress$(),
        clear: () => this._venueTemplatesState.venueTplElementInfo.setValue(null)
    });

    readonly venueTplElementInfoImages = Object.freeze({
        delete: (
            venueTemplateId: number,
            elementInfoId: number,
            type: VenueTemplateElementInfoType,
            imagesToDelete: VenueTemplateElementInfoImage[]
        ) => StateManager.inProgress(
            this._venueTemplatesState.venueTplElementInfoImages,
            zip(...imagesToDelete.map(request =>
                this._venueTemplatesApi.deleteVenueTemplateElementInfoImage(
                    venueTemplateId, elementInfoId, type, request.language, request.type, request.position
                )
            )).pipe(
                switchMap(() => of(null)),
                catchError(() => of(null))
            )),
        loading$: () => this._venueTemplatesState.venueTplElementInfoImages.isInProgress$(),
        clear: () => this._venueTemplatesState.venueTplElementInfoImages.setValue(null)
    });

    readonly venueTplCustomTagGroups = Object.freeze({
        load: (templateId: number): void => StateManager.load(
            this._venueTemplatesState.venueTplCustomTagGroups,
            this._venueTemplatesApi.getVenueTplCustomTagGroups(templateId)
        ),
        create: (templateId: number, group: VenueTplDynamicTagGroup) =>
            StateManager.inProgress(
                this._venueTemplatesState.venueTplCustomTagGroups,
                this._venueTemplatesApi.postVenueTplCustomTagGroup(templateId, group)
            ).pipe(
                tap(() => this.venueTplCustomTagGroups.load(templateId))
            ),
        update: (templateId: number, group: VenueTplDynamicTagGroup) =>
            StateManager.inProgress(
                this._venueTemplatesState.venueTplCustomTagGroups,
                this._venueTemplatesApi.putVenueTplCustomTagGroup(templateId, group)
            ).pipe(
                tap(() => this.venueTplCustomTagGroups.load(templateId))
            ),
        delete: (templateId: number, groupId: number) =>
            StateManager.inProgress(
                this._venueTemplatesState.venueTplCustomTagGroups,
                this._venueTemplatesApi.deleteVenueTplCustomTagGroup(templateId, groupId)
            ).pipe(
                tap(() => this.venueTplCustomTagGroups.load(templateId))
            ),
        get$: () => this._venueTemplatesState.venueTplCustomTagGroups.getValue$(),
        error$: () => this._venueTemplatesState.venueTplCustomTagGroups.getError$(),
        loading$: () => this._venueTemplatesState.venueTplCustomTagGroups.isInProgress$(),
        clear: () => this._venueTemplatesState.venueTplCustomTagGroups.setValue(null)
    });

    readonly firstCustomTagGroupLabels
        = new CustomTagGroupLabels(this._venueTemplatesApi, this._venueTemplatesState.venueTplCustomFirstTagGroupLabels);

    readonly secondCustomTagGroupLabels
        = new CustomTagGroupLabels(this._venueTemplatesApi, this._venueTemplatesState.venueTplCustomSecondTagGroupLabels);

    clearVenueTemplateData(): void {
        this._templateChange.next();
        this._venueTemplatesState.priceTypes.setValue(null);
        this._venueTemplatesState.quotas.setValue(null);
        this._venueTemplatesState.blockingReasons.setValue(null);
        this._venueTemplatesState.gates.setValue(null);
        this.firstCustomTagGroupLabels.clear();
        this.secondCustomTagGroupLabels.clear();
        this.venueTplCustomTagGroups.clear();
    }

    // VENUE TEMPLATES

    loadVenueTemplatesList(request: GetVenueTemplatesRequest): void {
        this._venueTemplatesState.venueTplList.setInProgress(true);
        this._venueTemplatesApi.getVenueTemplates(request)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._venueTemplatesState.venueTplList.setInProgress(false))
            )
            .subscribe(venueTemplates => this._venueTemplatesState.venueTplList.setValue(venueTemplates));
    }

    getVenueTemplatesList$(): Observable<ListResponse<VenueTemplate>> {
        return this._venueTemplatesState.venueTplList.getValue$();
    }

    getVenueTemplatesListData$(): Observable<VenueTemplate[]> {
        return this._venueTemplatesState.venueTplList.getValue$()
            .pipe(map(venueTemplates => venueTemplates?.data));
    }

    getVenueTemplatesListMetadata$(): Observable<Metadata> {
        return this._venueTemplatesState.venueTplList.getValue$().pipe(map(response =>
            (response?.metadata && Object.assign(new Metadata(), response.metadata))
        ));
    }

    isVenueTemplatesListLoading$(): Observable<boolean> {
        return this._venueTemplatesState.venueTplList.isInProgress$();
    }

    clearVenueTemplateList(): void {
        this._venueTemplatesState.venueTplList.setValue(null);
    }

    // UPDATE / CLONE / DELETE VENUE TEMPLATE

    updateVenueTemplate(venueTemplateId: number, request: PutVenueTemplateRequest): Observable<void> {
        this._venueTemplatesState.venueTplUpdate.setInProgress(true);
        return this._venueTemplatesApi.putVenueTemplate(venueTemplateId, request)
            .pipe(finalize(() => this._venueTemplatesState.venueTplUpdate.setInProgress(false)));
    }

    createVenueTemplate(req: PostVenueTemplateRequest): Observable<{ id: number }> {
        this._venueTemplatesState.venueTplUpdate.setInProgress(true);
        return this._venueTemplatesApi.postVenueTemplate(req)
            .pipe(finalize(() => this._venueTemplatesState.venueTplUpdate.setInProgress(false)));
    }

    cloneVenueTemplate(fromVenueTemplateId: number, request: CloneVenueTemplateRequest): Observable<number> {
        this._venueTemplatesState.venueTplUpdate.setInProgress(true);
        return this._venueTemplatesApi.cloneVenueTemplate(fromVenueTemplateId, request).pipe(
            map(response => response.id),
            finalize(() => this._venueTemplatesState.venueTplUpdate.setInProgress(false)));
    }

    deleteVenueTemplate(id: string): Observable<void> {
        this._venueTemplatesState.venueTplUpdate.setInProgress(true);
        return this._venueTemplatesApi.deleteVenueTemplate(id)
            .pipe(finalize(() => this._venueTemplatesState.venueTplUpdate.setInProgress(false)));
    }

    isVenueTemplateSaving$(): Observable<boolean> {
        return this._venueTemplatesState.venueTplUpdate.isInProgress$();
    }

    // PRICE TYPES

    loadVenueTemplatePriceTypes(venueTemplateId: number): void {
        this._venueTemplatesState.priceTypes.setInProgress(true);
        this._venueTemplatesApi.getVenueTplPriceTypes(venueTemplateId)
            .pipe(
                takeUntil(this._templateChange),
                finalize(() => this._venueTemplatesState.priceTypes.setInProgress(false))
            )
            .subscribe(priceTypes => this._venueTemplatesState.priceTypes.setValue(priceTypes));
    }

    getVenueTemplatePriceTypes$(): Observable<VenueTemplatePriceType[]> {
        return this._venueTemplatesState.priceTypes.getValue$();
    }

    isVenueTemplatePriceTypesLoading$(): Observable<boolean> {
        return this._venueTemplatesState.priceTypes.isInProgress$();
    }

    addVenueTemplatePriceType(venueTemplateId: number, priceType: PostVenueTemplatePriceType): Observable<number> {
        this._venueTemplatesState.priceTypeUpdate.setInProgress(true);
        return this._venueTemplatesApi.postVenueTplPriceType(venueTemplateId, priceType)
            .pipe(finalize(() => this._venueTemplatesState.priceTypeUpdate.setInProgress(false)));
    }

    updateVenueTemplatePriceType(venueTemplateId: number, priceType: VenueTemplatePriceType): Observable<VenueTemplatePriceType> {
        this._venueTemplatesState.priceTypeUpdate.setInProgress(true);
        return this._venueTemplatesApi.putVenueTplPriceType(venueTemplateId, priceType)
            .pipe(finalize(() => {
                this._venueTemplatesState.priceTypeUpdate.setInProgress(false);
            }));
    }

    isVenueTemplatePriceTypeSaving$(): Observable<boolean> {
        return this._venueTemplatesState.priceTypeUpdate.isInProgress$();
    }

    deleteVenueTemplatePriceType(venueTemplateId: number, priceTypeId: string): Observable<void> {
        this._venueTemplatesState.priceTypeUpdate.setInProgress(true);
        return this._venueTemplatesApi.deleteVenueTplPriceType(venueTemplateId, priceTypeId)
            .pipe(finalize(() => {
                this._venueTemplatesState.priceTypeUpdate.setInProgress(false);
            }));
    }

    clearVenueTemplatePriceTypes(): void {
        this._venueTemplatesState.priceTypes.setValue(null);
    }

    loadMultipleVenueTemplatePriceTypes(venueTemplates: VenueTemplate[]): void {
        forkJoin(venueTemplates.map(tpl =>
            this._venueTemplatesApi.getVenueTplPriceTypes(tpl.id))
        )
            .pipe(
                map(data => {
                    const result: VenueTemplatePriceTypeGrouped[] = [];
                    data.forEach((zones, index) =>
                        result.push({
                            venueTemplateName: venueTemplates[index].name,
                            priceTypes: zones
                        })
                    );
                    return result;
                })
            )
            .subscribe(priceTypes => this._venueTemplatesState.priceTypesGrouped.setValue(priceTypes));
    }

    getGroupedVenueTemplatePriceTypes$(): Observable<VenueTemplatePriceTypeGrouped[]> {
        return this._venueTemplatesState.priceTypesGrouped.getValue$();
    }

    clearGroupedVenueTemplatePriceTypes(): void {
        this._venueTemplatesState.priceTypesGrouped.setValue(null);
    }

    // QUOTAS

    loadVenueTemplateQuotas(venueTemplateId: number): void {
        this._venueTemplatesState.quotas.setInProgress(true);
        this._venueTemplatesApi.getVenueTplQuotas(venueTemplateId)
            .pipe(
                takeUntil(this._templateChange),
                finalize(() => this._venueTemplatesState.quotas.setInProgress(false))
            )
            .subscribe(quotas => this._venueTemplatesState.quotas.setValue(quotas));
    }

    getVenueTemplateQuotas$(): Observable<VenueTemplateQuota[]> {
        return this._venueTemplatesState.quotas.getValue$();
    }

    isVenueTemplateQuotasLoading$(): Observable<boolean> {
        return this._venueTemplatesState.quotas.isInProgress$();
    }

    addVenueTemplateQuota(venueTemplateId: number, quota: PostVenueTemplateQuota): Observable<number> {
        this._venueTemplatesState.quotas.setInProgress(true);
        return this._venueTemplatesApi.postVenueTplQuota(venueTemplateId, quota)
            .pipe(finalize(() => this._venueTemplatesState.quotas.setInProgress(false)));
    }

    updateVenueTemplateQuota(venueTemplateId: number, quota: VenueTemplateQuota): Observable<VenueTemplateQuota> {
        this._venueTemplatesState.quotas.setInProgress(true);
        return this._venueTemplatesApi.putVenueTplQuota(venueTemplateId, quota)
            .pipe(finalize(() => this._venueTemplatesState.quotas.setInProgress(false)));
    }

    deleteVenueTemplateQuota(venueTemplateId: number, quotaId: string): Observable<void> {
        this._venueTemplatesState.quotas.setInProgress(true);
        return this._venueTemplatesApi.deleteVenueTplQuota(venueTemplateId, quotaId)
            .pipe(finalize(() => this._venueTemplatesState.quotas.setInProgress(false)));
    }

    isVenueTemplateQuotaSaving$(): Observable<boolean> {
        return this._venueTemplatesState.quotas.isInProgress$();
    }

    clearVenueTemplateQuotas(): void {
        this._venueTemplatesState.quotas.setValue(null);
    }

    // BLOCKING REASONS

    loadVenueTemplateBlockingReasons(venueTemplateId: number): void {
        this._venueTemplatesState.blockingReasons.setInProgress(true);
        this._venueTemplatesApi.getVenueTplBlockingReasons(venueTemplateId)
            .pipe(
                takeUntil(this._templateChange),
                finalize(() => this._venueTemplatesState.blockingReasons.setInProgress(false))
            )
            .subscribe(blockingReasons => this._venueTemplatesState.blockingReasons.setValue(blockingReasons));
    }

    getVenueTemplateBlockingReasons$(): Observable<VenueTemplateBlockingReason[]> {
        return this._venueTemplatesState.blockingReasons.getValue$();
    }

    clearVenueTemplateBlockingReasons(): void {
        this._venueTemplatesState.blockingReasons.setValue(null);
    }

    isVenueTemplateBlockingReasonsLoading$(): Observable<boolean> {
        return this._venueTemplatesState.blockingReasons.isInProgress$();
    }

    addVenueTemplateBlockingReason(venueTemplateId: number, req: VenueTemplateBlockingReasonPost): Observable<number> {
        this._venueTemplatesState.blockingReasonUpdate.setInProgress(true);
        return this._venueTemplatesApi.postVenueTplBlockingReason(venueTemplateId, req)
            .pipe(finalize(() => this._venueTemplatesState.blockingReasonUpdate.setInProgress(false)));
    }

    updateVenueTemplateBlockingReason(
        venueTemplateId: number,
        blockingReason: VenueTemplateBlockingReasonPut
    ): Observable<VenueTemplateBlockingReason> {
        this._venueTemplatesState.blockingReasonUpdate.setInProgress(true);
        return this._venueTemplatesApi.putVenueTplBlockingReason(venueTemplateId, blockingReason)
            .pipe(finalize(() => this._venueTemplatesState.blockingReasonUpdate.setInProgress(false)));
    }

    deleteVenueTemplateBlockingReason(venueTemplateId: number, blockingReasonId: string): Observable<void> {
        this._venueTemplatesState.blockingReasonUpdate.setInProgress(true);
        return this._venueTemplatesApi.deleteVenueTplBlockingReason(venueTemplateId, blockingReasonId)
            .pipe(finalize(() => this._venueTemplatesState.blockingReasonUpdate.setInProgress(false)));
    }

    // GATES

    loadVenueTemplateGates(venueTemplateId: number): void {
        this._venueTemplatesState.gates.setInProgress(true);
        this._venueTemplatesApi.getVenueTplGates(venueTemplateId)
            .pipe(
                takeUntil(this._templateChange),
                finalize(() => this._venueTemplatesState.gates.setInProgress(false))
            )
            .subscribe(gates => this._venueTemplatesState.gates.setValue(gates));
    }

    getVenueTemplateGates$(): Observable<VenueTemplateGate[]> {
        return this._venueTemplatesState.gates.getValue$();
    }

    isVenueTemplateGatesLoading$(): Observable<boolean> {
        return this._venueTemplatesState.gates.isInProgress$();
    }

    clearVenueTemplateGates(): void {
        this._venueTemplatesState.gates.setValue(null);
    }

    addVenueTemplateGate(venueTemplateId: number, gate: PostVenueTemplateGate): Observable<number> {
        this._venueTemplatesState.gates.setInProgress(true);
        return this._venueTemplatesApi.postVenueTplGate(venueTemplateId, gate)
            .pipe(finalize(() => this._venueTemplatesState.gates.setInProgress(false)));
    }

    updateVenueTemplateGate(venueTemplateId: number, gate: VenueTemplateGate): Observable<VenueTemplateGate> {
        this._venueTemplatesState.gates.setInProgress(true);
        return this._venueTemplatesApi.putVenueTplGate(venueTemplateId, gate)
            .pipe(finalize(() => this._venueTemplatesState.gates.setInProgress(false)));
    }

    deleteVenueTemplateGate(venueTemplateId: number, gateId: string): Observable<void> {
        this._venueTemplatesState.gates.setInProgress(true);
        return this._venueTemplatesApi.deleteVenueTplGate(venueTemplateId, gateId)
            .pipe(finalize(() => this._venueTemplatesState.gates.setInProgress(false)));
    }

    isVenueTemplateGatesSaving$(): Observable<boolean> {
        return this._venueTemplatesState.gates.isInProgress$();
    }

    // VENUE ENTITIES LIST

    loadFilterVenueEntitiesList(request: BaseVenueTemplatesRequest): void {
        this._venueTemplatesApi.getFilterOptions('venue-entities', request)
            .pipe(catchError(() => of(null)))
            .subscribe(response => this._venueTemplatesState.filterVenueEntitiesList.setValue(response?.data));
    }

    getFilterVenueEntitiesList$(): Observable<IdName[]> {
        return this._venueTemplatesState.filterVenueEntitiesList.getValue$();
    }

    // INTERACTIVE OPTIONS (MMC)

    loadVenueTemplateInteractive(venueTemplateId: number): void {
        this._venueTemplatesState.interactiveOptions.setInProgress(true);
        this._venueTemplatesApi.getVenueTplInteractiveOptions(venueTemplateId)
            .pipe(
                takeUntil(this._templateChange),
                finalize(() => this._venueTemplatesState.interactiveOptions.setInProgress(false))
            )
            .subscribe(interactiveOptions => this._venueTemplatesState.interactiveOptions.setValue(interactiveOptions));
    }

    getVenueTemplateInteractive$(): Observable<VenueTemplateInteractive> {
        return this._venueTemplatesState.interactiveOptions.getValue$();
    }

    isVenueTemplateInteractiveLoading$(): Observable<boolean> {
        return this._venueTemplatesState.interactiveOptions.isInProgress$();
    }

    clearVenueTemplateInteractive(): void {
        this._venueTemplatesState.interactiveOptions.setValue(null);
    }

    updateVenueTemplateInteractive(
        venueTemplateId: number,
        interactiveOptions: VenueTemplateInteractive
    ): Observable<VenueTemplateInteractive> {
        this._venueTemplatesState.interactiveOptionsUpdate.setInProgress(true);
        return this._venueTemplatesApi.putVenueTplInteractiveOptions(venueTemplateId, interactiveOptions)
            .pipe(finalize(() => this._venueTemplatesState.interactiveOptionsUpdate.setInProgress(false)));
    }

    // Venue template export list file
    exportInteractiveVenueTemplateFile<T extends ExportRequest | ExportJsonRequest>(
        venueTemplateId: number, request: T, exportType: InteractiveVenueTemplateFileOption
    ): Observable<ExportResponse> {
        this._venueTemplatesState.interactiveFileExport.setInProgress(true);
        return this._venueTemplatesApi.postInteractiveVenueTemplateFile(venueTemplateId, request, exportType)
            .pipe(finalize(() => this._venueTemplatesState.interactiveFileExport.setInProgress(false)));
    }

    isExportInteractiveVenueTemplatesFileLoading$(): Observable<boolean> {
        return this._venueTemplatesState.interactiveFileExport.isInProgress$();
    }
}

class CustomTagGroupLabels {

    constructor(private _venueTemplatesApi: VenueTemplatesApi, private _stateProp: StateProperty<VenueTplDynamicTagGroupLabel[]>) {
    }

    load(templateId: number, groupId: number): void {
        StateManager.load(this._stateProp, this._venueTemplatesApi.getVenueTplCustomTagGroupLabels(templateId, groupId));
    }

    create(templateId: number, groupId: number, label: PostVenueTplDynamicTagGroupLabel): Observable<Id> {
        return StateManager.inProgress(this._stateProp,
            this._venueTemplatesApi.postVenueTplCustomTagGroupLabel(templateId, groupId, label));
    }

    update(templateId: number, groupId: number, labelId: number, updatedLabel: PutVenueTplDynamicTagGroupLabel): Observable<void> {
        return StateManager.inProgress(
            this._stateProp,
            this._venueTemplatesApi.putVenueTplCustomTagGroupLabel(templateId, groupId, labelId, updatedLabel)
        );
    }

    delete(templateId: number, groupId: number, labelId: string): Observable<void> {
        return StateManager.inProgress(this._stateProp,
            this._venueTemplatesApi.deleteVenueTplCustomTagGroupLabel(templateId, groupId, labelId));
    }

    get$(): Observable<VenueTplDynamicTagGroupLabel[]> { return this._stateProp.getValue$(); }

    error$(): Observable<HttpErrorResponse> { return this._stateProp.getError$(); }

    loading$(): Observable<boolean> { return this._stateProp.isInProgress$(); }

    clear(): void { this._stateProp.setValue(null); }

}
