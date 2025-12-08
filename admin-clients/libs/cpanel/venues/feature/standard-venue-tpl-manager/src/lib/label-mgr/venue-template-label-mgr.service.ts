    import {
    DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig, openDialog
} from '@admin-clients/shared/common/ui/components';
import { GateUpdateType } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { inject, Injectable, ViewContainerRef } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable, combineLatest } from 'rxjs';
import { filter, map, switchMap, take, tap } from 'rxjs/operators';
import { NnzPartialApplyDialogComponent } from '../dialog/nnz-partial-apply-dialog/nnz-partial-apply-dialog.component';
import { NnzQuotaPartialApplyDialogComponent } from '../dialog/nnz-quota-partial-apply-dialog/nnz-quota-partial-apply-dialog.component';
import { SeasonTicketLinkableDialogComponent } from '../dialog/season-ticket-linkable-dialog/season-ticket-linkable-dialog.component';
import { SessionPackLinkDialogComponent } from '../dialog/session-pack-link-dialog/session-pack-link-dialog.component';
import { SpreadGateChangesDialogComponent } from '../dialog/spread-gate-changes-dialog/spread-gate-changes-dialog.component';
import { VenueTemplateLabel, VenueTemplateLabelGroup } from '../models/label-group/venue-template-label-group-list.model';
import { VenueTemplateLabelGroupType } from '../models/label-group/venue-template-label-group-type.enum';
import { NnzPartialApplyData } from '../models/nnz-partial-apply-data.model';
import { StandardVenueTemplateBaseService } from '../services/standard-venue-template-base.service';
import { StandardVenueTemplateChangesService } from '../services/standard-venue-template-changes.service';
import { StandardVenueTemplateFilterService } from '../services/standard-venue-template-filter.service';
import { StandardVenueTemplateSelectionService } from '../services/standard-venue-template-selection.service';
import { EditLabelDialogData } from './label-dialog/edit-label-dialog-data';
import { EditLabelDialogComponent } from './label-dialog/edit-label-dialog.component';
import { EditLabelGroupDialogComponent } from './label-group-dialog/edit-label-group-dialog.component';
import { EditLabelGroupDialogData } from './label-group-dialog/edit-label-group-dialog.data';

@Injectable()
export class VenueTemplateLabelMgrService {

    readonly #standardVenueTemplateSrv = inject(StandardVenueTemplateBaseService);
    readonly #standardVenueTemplateFilterSrv = inject(StandardVenueTemplateFilterService);
    readonly #standardVenueTemplateSelectionSrv = inject(StandardVenueTemplateSelectionService);
    readonly #standardVenueTemplateChangesSrv = inject(StandardVenueTemplateChangesService);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #dialog = inject(MatDialog);
    readonly #messageDialogSrv = inject(MessageDialogService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #viewCont = inject(ViewContainerRef);

    openEditLabelGroupDialog(templateId: number, labelGroup?: VenueTemplateLabelGroup): Observable<number> {
        const data: EditLabelGroupDialogData = {
            templateId,
            isCreation: !labelGroup,
            currentName: labelGroup?.customName,
            currentCode: labelGroup?.code,
            title: labelGroup ? 'VENUE_TPL_MGR.TITLES.CUSTOM_GROUP_EDITION' : 'VENUE_TPL_MGR.TITLES.NEW_CUSTOM_GROUP',
            id: labelGroup?.customGroupId
        };
        return openDialog(this.#dialog, EditLabelGroupDialogComponent, data, this.#viewCont)
            .beforeClosed()
            .pipe(
                filter(result => result?.saved),
                tap(result =>
                    this.#ephemeralSrv.showSuccess({
                        msgKey: labelGroup ? 'VENUE_TPLS.UPDATE_LABEL_GROUP_SUCCESS' : 'VENUE_TPLS.CREATE_LABEL_GROUP_SUCCESS',
                        msgParams: { name: result.name }
                    })
                ),
                map(result => result.id)
            );
    }

    deleteLabelGroup(templateId: number, labelGroup: VenueTemplateLabelGroup): Observable<void> {
        return this.#messageDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.WARNING',
            message: 'VENUE_TPLS.DELETE_LABEL_GROUP_WARN',
            messageParams: { name: labelGroup.customName }
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this.#venueTemplatesSrv.venueTplCustomTagGroups.delete(templateId, labelGroup.customGroupId)),
                tap(() => {
                    this.#ephemeralSrv.showSuccess({
                        msgKey: 'VENUE_TPLS.DELETE_LABEL_GROUP_SUCCESS',
                        msgParams: { name: labelGroup.customName }
                    });
                    this.#standardVenueTemplateChangesSrv.removeCustomTagValues(labelGroup.id);
                })
            );
    }

    openNewLabelDialog(templateId: number, labelGroup: VenueTemplateLabelGroup): Observable<void> {
        const data: EditLabelDialogData = {
            templateId,
            isCreation: true,
            labelGroupType: labelGroup.id,
            groupId: labelGroup.customGroupId
        };
        return openDialog(this.#dialog, EditLabelDialogComponent, data, this.#viewCont)
            .beforeClosed()
            .pipe(
                filter(Boolean),
                map(() => null)
            );
    }

    openEditLabelDialog(templateId: number, labelGroup: VenueTemplateLabelGroup, label: VenueTemplateLabel): Observable<void> {
        const data: EditLabelDialogData = {
            templateId,
            isCreation: false,
            labelGroupType: labelGroup.id,
            groupId: labelGroup.customGroupId,
            label
        };
        return openDialog(this.#dialog, EditLabelDialogComponent, data, this.#viewCont)
            .beforeClosed()
            .pipe(
                filter(Boolean),
                tap(() => this.#ephemeralSrv.showSaveSuccess()),
                map(() => null)
            );
    }

    deleteLabel(templateId: number, label: VenueTemplateLabel): Observable<VenueTemplateLabelGroup> {
        return this.#standardVenueTemplateSrv.getLabelGroups$()
            .pipe(
                take(1),
                map(labelGroups => labelGroups.find(group => group.labels.includes(label))),
                switchMap(labelGroup =>
                    this.#messageDialogSrv.showWarn({
                        size: DialogSize.SMALL,
                        title: 'TITLES.WARNING',
                        message: this.#getDeleteWarningMessageKey(labelGroup),
                        messageParams: { name: label.literal }
                    })
                        .pipe(
                            filter(Boolean),
                            switchMap(() => this.deleteLabelFromLabelGroup(templateId, labelGroup, label).pipe(map(() => labelGroup))),
                            tap(labelGroup => {
                                this.#standardVenueTemplateChangesSrv.replaceLabelByDefaultLabel(labelGroup, label);
                                this.#standardVenueTemplateFilterSrv.filterLabel();
                                this.#standardVenueTemplateSelectionSrv.checkFilteredSelectedItems();
                                this.#ephemeralSrv.showSuccess({
                                    msgKey: this.#getDeleteSuccessMessageKey(labelGroup),
                                    msgParams: { name: label.literal }
                                });
                            }),
                            map(() => labelGroup)
                        )
                )
            );
    }

    deleteLabelFromLabelGroup(tplId: number, labelGroup: VenueTemplateLabelGroup, label: VenueTemplateLabel): Observable<unknown> {
        switch (labelGroup.id) {
            case VenueTemplateLabelGroupType.blockingReason:
                return this.#venueTemplatesSrv.deleteVenueTemplateBlockingReason(tplId, label.id);
            case VenueTemplateLabelGroupType.priceType:
                return this.#venueTemplatesSrv.deleteVenueTemplatePriceType(tplId, label.id);
            case VenueTemplateLabelGroupType.quota:
                return this.#venueTemplatesSrv.deleteVenueTemplateQuota(tplId, label.id);
            case VenueTemplateLabelGroupType.gate:
                return this.#venueTemplatesSrv.deleteVenueTemplateGate(tplId, label.id);
            case VenueTemplateLabelGroupType.firstCustomLabelGroup:
                return this.#venueTemplatesSrv.firstCustomTagGroupLabels.delete(tplId, labelGroup.customGroupId, label.id);
            case VenueTemplateLabelGroupType.secondCustomLabelGroup:
                return this.#venueTemplatesSrv.secondCustomTagGroupLabels.delete(tplId, labelGroup.customGroupId, label.id);
        }
    }

    applyNnzQuotaPartial(label: VenueTemplateLabel): Observable<void> {
        return this.#standardVenueTemplateSelectionSrv.getSelectedVenueItems$()
            .pipe(
                take(1),
                switchMap(selectedVenueItems =>
                    this.#standardVenueTemplateSrv.getVenueItems$().pipe(
                        map(venueItems => venueItems.nnzs.get(Array.from(selectedVenueItems.nnzs)[0]))
                    )
                ),
                take(1),
                switchMap(selectedNNZ => {
                    const data: NnzPartialApplyData = {
                        label,
                        nnz: selectedNNZ
                    };
                    return this.#dialog.open(NnzQuotaPartialApplyDialogComponent, new ObMatDialogConfig(data, this.#viewCont))
                        .beforeClosed()
                        .pipe(
                            filter(Boolean),
                            map(() => null)
                        );
                })
            );
    }

    applyNnzStatusAndBlockingReasonPartial(label: VenueTemplateLabel): Observable<void> {
        return combineLatest([
            this.#standardVenueTemplateSelectionSrv.getSelectedVenueItems$(),
            this.#standardVenueTemplateSrv.getVenueItems$()
        ])
            .pipe(
                take(1),
                map(([selectedVenueItems, venueItems]) => venueItems.nnzs.get(Array.from(selectedVenueItems.nnzs)[0])),
                switchMap(nnz => {
                    const data: NnzPartialApplyData = { label, nnz };
                    return this.#dialog.open(NnzPartialApplyDialogComponent, new ObMatDialogConfig(data, this.#viewCont))
                        .beforeClosed()
                        .pipe(
                            filter(Boolean),
                            map(() => null)
                        );
                })
            );
    }

    applySessionPackLinks(eventId: number, sessionId: number, unrestrictedPack: boolean, label: VenueTemplateLabel): Observable<void> {
        return this.#standardVenueTemplateSelectionSrv.getSelectedVenueItems$()
            .pipe(
                take(1),
                switchMap(items =>
                    this.#dialog.open(
                        SessionPackLinkDialogComponent,
                        new ObMatDialogConfig({ eventId, sessionId, unrestrictedPack, label, items }, this.#viewCont)
                    ).beforeClosed()
                ),
                filter(Boolean),
                map(() => null)
            );
    }

    applySeasonTicketLinkable(label: VenueTemplateLabel): Observable<void> {
        return this.#standardVenueTemplateSelectionSrv.getSelectedVenueItems$()
            .pipe(
                take(1),
                switchMap(selectedItems =>
                    this.#dialog.open(
                        SeasonTicketLinkableDialogComponent,
                        new ObMatDialogConfig({ label, items: selectedItems }, this.#viewCont)
                    ).beforeClosed()
                ),
                filter(Boolean), map(() => null)
            );
    }

    openSpreadSoldGateChangesDialog(): Observable<GateUpdateType> {
        return this.#dialog.open<SpreadGateChangesDialogComponent, void, GateUpdateType>(
            SpreadGateChangesDialogComponent, new ObMatDialogConfig(null, this.#viewCont)
        )
            .afterClosed()
            .pipe(filter(Boolean));
    }

    #getDeleteWarningMessageKey(selectedLabelGroup: VenueTemplateLabelGroup): string {
        const id = selectedLabelGroup.id;
        return id === VenueTemplateLabelGroupType.blockingReason ? 'VENUE_TPLS.DELETE_BLOCKING_REASON_WARNING' :
            id === VenueTemplateLabelGroupType.priceType ? 'VENUE_TPLS.DELETE_PRICE_TYPE_WARNING' :
                id === VenueTemplateLabelGroupType.quota ? 'VENUE_TPLS.DELETE_QUOTA_WARNING' :
                    id === VenueTemplateLabelGroupType.gate ? 'VENUE_TPLS.DELETE_GATE_WARNING' :
                        'VENUE_TPLS.DELETE_CUSTOM_GROUP_LABEL_WARNING'; // first custom or second custom
    }

    #getDeleteSuccessMessageKey(selectedLabelGroup: VenueTemplateLabelGroup): string {
        const id = selectedLabelGroup.id;
        return id === VenueTemplateLabelGroupType.blockingReason ? 'VENUE_TPLS.DELETE_BLOCKING_REASON_SUCCESS' :
            id === VenueTemplateLabelGroupType.priceType ? 'VENUE_TPLS.DELETE_PRICE_TYPE_SUCCESS' :
                id === VenueTemplateLabelGroupType.quota ? 'VENUE_TPLS.DELETE_QUOTA_SUCCESS' :
                    id === VenueTemplateLabelGroupType.gate ? 'VENUE_TPLS.DELETE_GATE_SUCCESS' :
                        'VENUE_TPLS.DELETE_CUSTOM_GROUP_LABEL_SUCCESS'; // first custom or second custom
    }
}
