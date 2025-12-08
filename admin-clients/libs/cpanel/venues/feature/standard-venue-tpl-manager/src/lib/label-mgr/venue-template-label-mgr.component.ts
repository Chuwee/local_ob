import {
    DialogSize, EphemeralMessageService, MessageDialogService, MessageType
} from '@admin-clients/shared/common/ui/components';
import { IdName } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import {
    GateUpdateType, StdVenueTplService, VENUE_MAP_SERVICE, VenueMap, VenueMapService
} from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import {
    VenueTemplate, VenueTemplateBlockingReasonCode, VenueTemplatesService, VenueTemplateStatus
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { NgStyle } from '@angular/common';
import {
    ChangeDetectionStrategy, ChangeDetectorRef, Component, computed, DestroyRef, effect, inject, input, signal, untracked, ViewContainerRef
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { MatIconButton } from '@angular/material/button';
import { MatButtonToggle } from '@angular/material/button-toggle';
import { MatRipple } from '@angular/material/core';
import { MatDivider } from '@angular/material/divider';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTooltip } from '@angular/material/tooltip';
import { DomSanitizer } from '@angular/platform-browser';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import { debounceTime, filter, map, take } from 'rxjs/operators';
import { ApplyLabelResult } from '../models/label-group/apply-label-result';
import {
    generateCustomVenueTemplateLabelGroup, VenueTemplateLabel, VenueTemplateLabelGroup, VenueTemplateLabelGroupList
} from '../models/label-group/venue-template-label-group-list.model';
import { VenueTemplateLabelGroupType } from '../models/label-group/venue-template-label-group-type.enum';
import { VenueTemplateEditorType } from '../models/venue-template-editor-type.model';
import { LabelErrors, StandardVenueTemplateBaseService } from '../services/standard-venue-template-base.service';
import { StandardVenueTemplateChangesService } from '../services/standard-venue-template-changes.service';
import { StandardVenueTemplateFilterService } from '../services/standard-venue-template-filter.service';
import { StandardVenueTemplateSelectionService } from '../services/standard-venue-template-selection.service';
import { VenueTemplateLabelMgrService } from './venue-template-label-mgr.service';

interface PartialSession {
    id?: number;
    name?: string;
    venue_template?: VenueTemplate;
    event?: Partial<IdName>;
}

@Component({
    imports: [
        TranslatePipe, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle,
        MatAccordion, MatTooltip, MatIcon, MatIconButton, MatRipple, MatButtonToggle, MatDivider,
        MatProgressSpinner, NgStyle, EllipsifyDirective, LocalNumberPipe
    ],
    selector: 'app-venue-template-label-mgr',
    templateUrl: './venue-template-label-mgr.component.html',
    styleUrls: ['./venue-template-label-mgr.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [{
        provide: VenueTemplateLabelMgrService,
        deps: [ViewContainerRef]
    }]
})
export class VenueTemplateLabelMgrComponent {
    readonly #destroyRef = inject(DestroyRef);
    readonly #standardVenueTemplateSrv = inject(StandardVenueTemplateBaseService);
    readonly #standardVenueTemplateFilterSrv = inject(StandardVenueTemplateFilterService);
    readonly #standardVenueTemplateSelectionSrv = inject(StandardVenueTemplateSelectionService);
    readonly #standardVenueTemplateChangesSrv = inject(StandardVenueTemplateChangesService);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #venueMapSrv: VenueMapService = inject(VENUE_MAP_SERVICE, { optional: true }) ?? inject(StdVenueTplService);
    readonly #venueTemplateLabelMgrSrv = inject(VenueTemplateLabelMgrService);
    readonly #changeDetector = inject(ChangeDetectorRef);
    readonly #messageDialogSrv = inject(MessageDialogService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #translateSrv = inject(TranslateService);
    readonly #sanitizer = inject(DomSanitizer);

    // when a seat map refresh comes with new labels, it triggers an inconsistency load,
    // this flag indicates this type of load, to prevent recursive loads
    #inconsistencyLoad = false;
    // when a custom tag group is created, requires to load it and assign new default values to seats,
    // this "flag" indicates that this must be done in label group - labels generation
    #newCustomTagGroupId: number;
    // when dynamic labels are loaded, this component tries to mount the UI, but cannot be completed before everything is loaded, if any
    // load fails, a timeout error is shown, this var allows to clear this timeout after everything is loaded.
    #loadConsoleErrorTimeout: number;

    readonly $venueTemplate = input<VenueTemplate>(null, { alias: 'venueTemplate' });
    readonly $disabled = input<boolean>(false, { alias: 'disabled' });
    readonly $visibleSeats = input<boolean>(false, { alias: 'visibleSeats' });
    readonly $editorType = input<VenueTemplateEditorType>(null, { alias: 'editorType' });
    readonly $session = input<PartialSession>(null, { alias: 'session' });
    readonly $linkedSessions = input<{ id: number; name: string; color?: string }[]>(null, { alias: 'linkedSessions' });
    readonly $unrestrictedPack = input<boolean>(false, { alias: 'unrestrictedPack' });
    readonly $loading = input<boolean>(false, { alias: 'loading' });
    readonly $loaded = signal(false);
    readonly $labelGroups = toSignal(this.#standardVenueTemplateSrv.getLabelGroups$());
    readonly $selectedLabelGroup = toSignal(this.#standardVenueTemplateSrv.getSelectedLabelGroup$());
    readonly $venueTemplateInProgress = computed(() => this.$venueTemplate()?.status === VenueTemplateStatus.inProgress);
        readonly $addLabelGroupBtnDisabled = toSignal(
        this.#venueTemplatesSrv.venueTplCustomTagGroups.get$().pipe(map(tagGroups => tagGroups?.length >= 2))
    );

    // this instance controls current and previous available template, this is used before to check which type of load or reload is req.
    readonly #venueTemplateWithPrevious = {
        previous: null as VenueTemplate,
        signal: computed(() => {
            const result = {
                current: this.$venueTemplate(),
                previous: this.#venueTemplateWithPrevious.previous
            };
            this.#venueTemplateWithPrevious.previous = result.current;
            return result;
        })
    };

    constructor() {
        effect(() => {
            const venueTemplateWithPrevious = this.#venueTemplateWithPrevious.signal();
            untracked(() => this.#setVenueTemplate(venueTemplateWithPrevious.current, venueTemplateWithPrevious.previous));
        });
        effect(() => {
            if (this.$disabled()) {
                this.#standardVenueTemplateSrv.setSelectedLabelGroup(null);
            }
        });
        this.#venueTemplatesSrv.venueTplCustomTagGroups.get$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(customTagGroups => {
                this.#venueTemplatesSrv.firstCustomTagGroupLabels.clear();
                this.#venueTemplatesSrv.secondCustomTagGroupLabels.clear();
                customTagGroups.forEach((tagGroup, index) => {
                    if (index === 0) {
                        this.#venueTemplatesSrv.firstCustomTagGroupLabels.load(this.$venueTemplate().id, tagGroup.id);
                    } else {
                        this.#venueTemplatesSrv.secondCustomTagGroupLabels.load(this.$venueTemplate().id, tagGroup.id);
                    }
                });
            });
        this.#subscribeToDynamicSources();
        //refreshes view when counters are updated without a subject value set, getRefreshViewCountersTrigger is autocompleted
        this.#standardVenueTemplateSrv.getRefreshViewCountersTrigger$().subscribe(() => this.#changeDetector.markForCheck());
    }

    setSelectedLabelGroup(venueTemplateLabelGroup: VenueTemplateLabelGroup): void {
        this.#standardVenueTemplateSrv.setSelectedLabelGroup(venueTemplateLabelGroup);
    }

    selectLabel(label: VenueTemplateLabel, gateSpreadStrategy?: GateUpdateType): void {
        this.#standardVenueTemplateChangesSrv.applyLabelToSelection(label, this.$editorType(), gateSpreadStrategy)
            .pipe(take(1))
            .subscribe(applyResult => {
                switch (applyResult) {
                    case ApplyLabelResult.noSelection:
                        break;
                    case ApplyLabelResult.noChanges:
                        this.#ephemeralSrv.show({ type: MessageType.alert, msgKey: 'FORMS.FEEDBACK.NO_CHANGES' });
                        break;
                    case ApplyLabelResult.ok:
                        this.#standardVenueTemplateFilterSrv.filterLabel();
                        this.#standardVenueTemplateSelectionSrv.checkFilteredSelectedItems();
                        break;
                    case ApplyLabelResult.partialApply:
                        this.#standardVenueTemplateFilterSrv.filterLabel();
                        this.#standardVenueTemplateSelectionSrv.checkFilteredSelectedItems();
                        this.#messageDialogSrv.showWarn({
                            size: DialogSize.SMALL,
                            title: 'TITLES.NOTICE',
                            message: 'VENUE_TPL_MGR.DIALOGS.PARTIAL_CHANGES_WARNING',
                            showCancelButton: false
                        });
                        break;
                    case ApplyLabelResult.gateSpreadStrategyReq:
                        this.#venueTemplateLabelMgrSrv.openSpreadSoldGateChangesDialog()
                            .subscribe(gateSpreadStrategy => this.selectLabel(label, gateSpreadStrategy));
                        break;
                    case ApplyLabelResult.cannotApply:
                        this.#messageDialogSrv.showWarn({
                            size: DialogSize.SMALL,
                            title: 'TITLES.NOTICE',
                            message: 'VENUE_TPL_MGR.DIALOGS.CANNOT_APPLY_CHANGES_WARNING',
                            showCancelButton: false
                        });
                        break;
                    case ApplyLabelResult.invalidNnzSelection:
                        this.#messageDialogSrv.showAlert({
                            size: DialogSize.SMALL,
                            title: this.#translateSrv.instant('TITLES.ERROR_DIALOG'),
                            message: this.#translateSrv.instant('VENUE_TPL_MGR.DIALOGS.MULTIPLE_NNZ_STATUS_CHANGE_ALERT')
                        });
                        break;
                    case ApplyLabelResult.nnzStatusOrBRPartialApply:
                        this.#venueTemplateLabelMgrSrv.applyNnzStatusAndBlockingReasonPartial(label)
                            .subscribe(() => this.#changeDetector.markForCheck());
                        break;
                    case ApplyLabelResult.nnzQuotaPartialApply:
                        this.#venueTemplateLabelMgrSrv.applyNnzQuotaPartial(label).subscribe(() => this.#changeDetector.markForCheck());
                        break;
                    case ApplyLabelResult.cannotApplyWithPendingChanges:
                        this.#messageDialogSrv.showWarn({
                            size: DialogSize.SMALL,
                            title: 'TITLES.NOTICE',
                            message: 'VENUE_TPL_MGR.DIALOGS.CANNOT_APPLY_WITH_PENDING_CHANGES',
                            showCancelButton: false
                        });
                        break;
                    case ApplyLabelResult.sessionPackLinkApply:
                        this.#venueTemplateLabelMgrSrv.applySessionPackLinks(
                            this.$session().event.id, this.$session().id, this.$unrestrictedPack(), label
                        )
                            .subscribe(() => {
                                this.#venueTemplatesSrv.venueTpl.load(this.$venueTemplate().id);
                                this.#standardVenueTemplateSelectionSrv.unselectAll();
                            });
                        break;
                    case ApplyLabelResult.seasonTicketLinkableApply:
                        this.#venueTemplateLabelMgrSrv.applySeasonTicketLinkable(label)
                            .subscribe(() => {
                                this.#venueTemplatesSrv.venueTpl.load(this.$venueTemplate().id);
                                this.#venueTemplatesSrv.venueTplCustomTagGroups.load(this.$venueTemplate().id);
                                this.#standardVenueTemplateSelectionSrv.unselectAll();
                            });
                        break;
                }
            });
    }

    filterLabel(label: VenueTemplateLabel): void {
        this.#standardVenueTemplateFilterSrv.filterLabel(label);
        this.#standardVenueTemplateSelectionSrv.checkFilteredSelectedItems();
    }

    isSomeFilterInLabelGroup(labelGroup: VenueTemplateLabelGroup): boolean {
        return labelGroup?.labels?.some(label => label.filtering);
    }

    openEditLabelGroupDialog(labelGroup?: VenueTemplateLabelGroup): void {
        this.#venueTemplateLabelMgrSrv.openEditLabelGroupDialog(this.$venueTemplate().id, labelGroup).subscribe();
    }

    openNewLabelGroupDialog(): void {
        if (!this.$addLabelGroupBtnDisabled()) {
            this.#venueTemplateLabelMgrSrv.openEditLabelGroupDialog(this.$venueTemplate().id)
                .subscribe(id => this.#newCustomTagGroupId = id);
        }
    }

    deleteLabelGroup(labelGroup: VenueTemplateLabelGroup): void {
        this.#venueTemplateLabelMgrSrv.deleteLabelGroup(this.$venueTemplate().id, labelGroup).subscribe(() =>
            this.#standardVenueTemplateSrv.setSelectedLabelGroup(
                this.$labelGroups().find(group => group.id === VenueTemplateLabelGroupType.state)
            )
        );
    }

    openNewLabelDialog(labelGroup: VenueTemplateLabelGroup): void {
        this.#venueTemplateLabelMgrSrv.openNewLabelDialog(this.$venueTemplate().id, labelGroup).subscribe(() =>
            this.#reloadDynamicLabels(labelGroup.id, labelGroup.customGroupId || null)
        );
    }

    openEditLabelDialog(labelGroup: VenueTemplateLabelGroup, label: VenueTemplateLabel): void {
        this.#venueTemplateLabelMgrSrv.openEditLabelDialog(this.$venueTemplate().id, labelGroup, label).subscribe(() =>
            this.#reloadDynamicLabels(labelGroup.id, labelGroup.customGroupId || null)
        );
    }

    deleteLabel(label: VenueTemplateLabel): void {
        this.#venueTemplateLabelMgrSrv.deleteLabel(this.$venueTemplate().id, label)
            .subscribe(labelGroup => this.#reloadDynamicLabels(labelGroup.id, labelGroup.customGroupId || null));
    }

    changeSorting(labelGroup: VenueTemplateLabelGroup): void {
        labelGroup.sorted = !labelGroup.sorted;
        labelGroup.labels.sort(
            labelGroup.sorted ?
            (a, b) => a.literal.localeCompare(b.literal, undefined, { sensitivity: 'base' })
            : (a, b) => Number(a.id) - Number(b.id)
        );
    }

    #setVenueTemplate(venueTemplate: VenueTemplate, prevVenueTemplate: VenueTemplate): void {
        this.$loaded.set(false);
        // inits static label groups
        const labelGroups = new VenueTemplateLabelGroupList(
            this.$editorType(), this.$venueTemplate().type, this.$unrestrictedPack(), this.$venueTemplate().inventory_provider === 'sga'
        );
        // adds translations to the static labels, dynamic ones doesn't have, sanitize icon to allow template render
        labelGroups.flatMap(labelGroup => labelGroup.labels ?? []).forEach(label => {
            label.literal = this.#translateSrv.instant(label.literalKey);
            label.safeIcon = label.icon ? this.#sanitizer.bypassSecurityTrustHtml(label.icon) : null;
        });
        // sets the initial labelGroups to the state
        this.#standardVenueTemplateSrv.setLabelGroups(labelGroups);
        // when venue template is active or, it's been changed, it reloads dynamic labels
        if (venueTemplate && venueTemplate.status === VenueTemplateStatus.active
            && (venueTemplate.id !== prevVenueTemplate?.id || venueTemplate.status !== prevVenueTemplate.status)
        ) {
            this.#loadLabelGroupIfRequired(VenueTemplateLabelGroupType.blockingReason);
            this.#loadLabelGroupIfRequired(VenueTemplateLabelGroupType.priceType);
            this.#loadLabelGroupIfRequired(VenueTemplateLabelGroupType.quota);
            this.#loadLabelGroupIfRequired(VenueTemplateLabelGroupType.gate);
            this.#venueTemplatesSrv.venueTplCustomTagGroups.load(venueTemplate.id);
        }
        // selects default label group
        this.#standardVenueTemplateSrv.setSelectedLabelGroup(labelGroups.find(labelGroup => labelGroup.selectedByDefault));
    }

    #loadLabelGroupIfRequired(type: VenueTemplateLabelGroupType): void {
        if (this.$labelGroups()?.find(labelGroup => labelGroup.id === type) !== undefined) {
            this.#reloadDynamicLabels(type);
        }
    }

    #reloadDynamicLabels(labelGroupType: VenueTemplateLabelGroupType, groupId?: number): void {
        switch (labelGroupType) {
            case VenueTemplateLabelGroupType.blockingReason:
                this.#venueTemplatesSrv.clearVenueTemplateBlockingReasons();
                this.#venueTemplatesSrv.loadVenueTemplateBlockingReasons(this.$venueTemplate().id);
                break;
            case VenueTemplateLabelGroupType.priceType:
                this.#venueTemplatesSrv.clearVenueTemplatePriceTypes();
                this.#venueTemplatesSrv.loadVenueTemplatePriceTypes(this.$venueTemplate().id);
                break;
            case VenueTemplateLabelGroupType.quota:
                this.#venueTemplatesSrv.clearVenueTemplateQuotas();
                this.#venueTemplatesSrv.loadVenueTemplateQuotas(this.$venueTemplate().id);
                break;
            case VenueTemplateLabelGroupType.gate:
                this.#venueTemplatesSrv.clearVenueTemplateGates();
                this.#venueTemplatesSrv.loadVenueTemplateGates(this.$venueTemplate().id);
                break;
            case VenueTemplateLabelGroupType.firstCustomLabelGroup:
                this.#venueTemplatesSrv.firstCustomTagGroupLabels.clear();
                this.#venueTemplatesSrv.firstCustomTagGroupLabels.load(this.$venueTemplate().id, groupId);
                break;
            case VenueTemplateLabelGroupType.secondCustomLabelGroup:
                this.#venueTemplatesSrv.secondCustomTagGroupLabels.clear();
                this.#venueTemplatesSrv.secondCustomTagGroupLabels.load(this.$venueTemplate().id, groupId);
                break;
        }
    }

    #subscribeToDynamicSources(): void {
        const customLabelGroups$ = combineLatest([
            this.#venueTemplatesSrv.venueTplCustomTagGroups.get$(),
            this.#venueTemplatesSrv.firstCustomTagGroupLabels.get$(),
            this.#venueTemplatesSrv.secondCustomTagGroupLabels.get$()
        ])
            .pipe(
                debounceTime(0),
                map(([tagGroups, firstDynamicTagGroupLabels, secondDynamicTagGroupLabels]) => {
                    const groupLabelsLoaded = (firstDynamicTagGroupLabels?.length ? 1 : 0) + (secondDynamicTagGroupLabels?.length ? 1 : 0);
                    if (tagGroups === null || (tagGroups.length !== groupLabelsLoaded)) {
                        return null;
                    }
                    const result = tagGroups?.map((tagGroup, index) =>
                        generateCustomVenueTemplateLabelGroup(
                            index === 0 ?
                                VenueTemplateLabelGroupType.firstCustomLabelGroup : VenueTemplateLabelGroupType.secondCustomLabelGroup,
                            tagGroup.code, tagGroup.id, tagGroup.name
                        )
                    );
                    this.#addDynamicLabels(result, VenueTemplateLabelGroupType.firstCustomLabelGroup, firstDynamicTagGroupLabels,
                        (label, labelGroup) => this.#createLabel(
                            labelGroup, label.id, label.name, label.code, label.default, label.color
                        ));
                    this.#addDynamicLabels(result, VenueTemplateLabelGroupType.secondCustomLabelGroup, secondDynamicTagGroupLabels,
                        (label, labelGroup) => this.#createLabel(
                            labelGroup, label.id, label.name, label.code, label.default, label.color
                        ));
                    return result;
                })
            );
        combineLatest([
            this.#venueMapSrv.getVenueMap$(),
            this.#venueTemplatesSrv.getVenueTemplateBlockingReasons$(),
            this.#venueTemplatesSrv.getVenueTemplatePriceTypes$(),
            this.#venueTemplatesSrv.getVenueTemplateQuotas$(),
            this.#venueTemplatesSrv.getVenueTemplateGates$(),
            customLabelGroups$
        ])
            .pipe(
                debounceTime(0),
                filter(sources => sources.every(Boolean)),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(([
                venueMap,
                blockingReasons,
                priceTypes,
                quotas,
                gates,
                customLabelGroups
            ]) => {
                // custom label groups, first removes this groups, and after pushes them every time
                let labelGroups = this.$labelGroups()?.filter(group => !group.custom) || [];
                labelGroups.push(...customLabelGroups);
                // dynamic labels
                this.#addDynamicLabels(labelGroups, VenueTemplateLabelGroupType.blockingReason, blockingReasons,
                    (lock, labelGroup) => this.#createLabel(
                        labelGroup, lock.id, lock.name, null, lock.default, lock.color,
                        lock.code === VenueTemplateBlockingReasonCode.socialDistancing
                    ));
                this.#addDynamicLabels(labelGroups, VenueTemplateLabelGroupType.priceType, priceTypes,
                    (price, labelGroup) => this.#createLabel(
                        labelGroup, price.id, price.name, price.code, price.default, price.color
                    ));
                this.#addDynamicLabels(labelGroups, VenueTemplateLabelGroupType.quota, quotas,
                    (quota, labelGroup) => this.#createLabel(
                        labelGroup, quota.id, quota.name, quota.code, quota.default, quota.color
                    ));
                this.#addDynamicLabels(labelGroups, VenueTemplateLabelGroupType.gate, gates,
                    (gate, labelGroup) => this.#createLabel(
                        labelGroup, gate.id, gate.name, gate.code, gate.default, gate.color
                    ));
                this.#addDynamicLabels(labelGroups, VenueTemplateLabelGroupType.sessionPacks, this.$linkedSessions(),
                    (sessionPack, labelGroup) => this.#createLabel(
                        labelGroup, sessionPack.id, sessionPack.name, sessionPack.name, false, sessionPack.color
                    ));
                const emptyLabelGroup = labelGroups.find(labelGroup => !labelGroup.labels);
                if (venueMap && emptyLabelGroup === undefined) {
                    labelGroups = labelGroups.filter(labelGroup => labelGroup.labels.length);
                    let labelGroupToOpen: VenueTemplateLabelGroup;
                    // when new label group is created, #newCustomTagGroupId is defined, this is bullshit, find a better way
                    if (this.#newCustomTagGroupId) {
                        const newCustomGroup = labelGroups.find(lg => lg.customGroupId === this.#newCustomTagGroupId);
                        this.#standardVenueTemplateSrv.setLabelGroups(labelGroups);
                        this.#standardVenueTemplateChangesSrv.replaceLabelByDefaultLabel(newCustomGroup, null);
                        labelGroupToOpen = newCustomGroup;
                        this.#newCustomTagGroupId = null;
                    }
                    const includeUnlinkedEl = this.$editorType() !== VenueTemplateEditorType.sessionPackTemplate;
                    const errorsMaps = this.#standardVenueTemplateSrv.setLabelsCounter(labelGroups, venueMap, includeUnlinkedEl);
                    this.#processLabelsError(labelGroups, errorsMaps);
                    this.#standardVenueTemplateSrv.setLabelGroups(labelGroups);
                    this.#standardVenueTemplateFilterSrv.updateFilteredLabels();
                    if (!labelGroupToOpen) {
                        const currentSelectedLabelGroup = this.$selectedLabelGroup();
                        if (currentSelectedLabelGroup) {
                            labelGroupToOpen = labelGroups.find(labelGroup => labelGroup.id === currentSelectedLabelGroup.id);
                        }
                    }
                    if (labelGroupToOpen) {
                        this.#standardVenueTemplateSrv.setSelectedLabelGroup(labelGroupToOpen);
                    }
                    this.$loaded.set(true);
                    this.#changeDetector.markForCheck();
                    this.#clearLoadTimeoutErrorsCheck();
                } else {
                    this.#loadTimeoutErrorsCheck(emptyLabelGroup, venueMap);
                }
            });
    }

    #clearLoadTimeoutErrorsCheck(): void {
        clearTimeout(this.#loadConsoleErrorTimeout);
        this.#loadConsoleErrorTimeout = null;
    }

    #loadTimeoutErrorsCheck(emptyLabelGroup: VenueTemplateLabelGroup, venueMap: VenueMap): void {
        this.#clearLoadTimeoutErrorsCheck();
        this.#loadConsoleErrorTimeout = setTimeout(() => {
            this.#loadConsoleErrorTimeout = null;
            if (!this.$loading()) {
                if (emptyLabelGroup) {
                    console.error('Venue template label group load failed', emptyLabelGroup);
                }
                if (!venueMap) {
                    console.error('Venue map load failed', venueMap);
                }
            }
        }, 5000);
    }

    #processLabelsError(labelGroups: VenueTemplateLabelGroup[], errorsMaps: LabelErrors): void {
        const prevInconsistencyLoad = this.#inconsistencyLoad;
        const excludedErrors: string[] = [
            VenueTemplateLabelGroupType.seasonTicketLinkable,
            VenueTemplateLabelGroupType.sessionPackLink,
            VenueTemplateLabelGroupType.sessionPacks
        ];
        const errors = Object.entries(errorsMaps)
            .filter(([groupType, labelSeat]) => !excludedErrors.includes(groupType) && labelSeat.size)
            .map(([groupType, labelSeat]) => ({ groupType, labelSeat }));
        if (errors.length) {
            const errorMessages = errors.map(error =>
                Array.from(error.labelSeat.keys()).map(label =>
                    [`${error.groupType} - ${label}`, error.labelSeat.get(label)]
                )
            )
                .flatMap(v => v);
            if (this.#inconsistencyLoad) {
                errorMessages.forEach(value => console.error(...value));
            } else {
                errorMessages.forEach(value => console.warn(...value));
                this.#reloadDynamicLabelsIfNeeded(VenueTemplateLabelGroupType.blockingReason, errorsMaps.blockingReason);
                this.#reloadDynamicLabelsIfNeeded(VenueTemplateLabelGroupType.priceType, errorsMaps.priceType);
                this.#reloadDynamicLabelsIfNeeded(VenueTemplateLabelGroupType.quota, errorsMaps.quota);
                this.#reloadDynamicLabelsIfNeeded(VenueTemplateLabelGroupType.gate, errorsMaps.gate);
                this.#processCustomTagGroups(labelGroups, errorsMaps);
            }
        }
        if (prevInconsistencyLoad) {
            this.#inconsistencyLoad = false;
        }
    }

    #reloadDynamicLabelsIfNeeded(type: VenueTemplateLabelGroupType, map: Map<string, number[]>): void {
        if (map.size) {
            this.#reloadDynamicLabels(type, null);
            this.#inconsistencyLoad = true;
        }
    }

    #processCustomTagGroups(labelGroups: VenueTemplateLabelGroup[], errorsMaps: LabelErrors): void {
        const firstGroupNeedsReload = !!errorsMaps.firstCustomLabelGroup.size;
        const secondGroupNeedsReload = !!errorsMaps.secondCustomLabelGroup.size;

        if (!firstGroupNeedsReload && !secondGroupNeedsReload) return;

        this.#inconsistencyLoad = true;

        const firstCustomGroup = labelGroups.find(lg => lg.id === VenueTemplateLabelGroupType.firstCustomLabelGroup);
        const secondCustomGroup = labelGroups.find(lg => lg.id === VenueTemplateLabelGroupType.secondCustomLabelGroup);

        // If an entire group is missing, it reloads all custom groups, this triggers the custom groups labels
        if ((firstGroupNeedsReload && !firstCustomGroup) || (secondGroupNeedsReload && !secondCustomGroup)) {
            this.#venueTemplatesSrv.venueTplCustomTagGroups.load(this.$venueTemplate().id);
        } else {
            // first custom group reload
            if (errorsMaps.firstCustomLabelGroup.size) {
                this.#reloadDynamicLabels(VenueTemplateLabelGroupType.firstCustomLabelGroup, firstCustomGroup.customGroupId);
            }
            // second custom group reload
            if (errorsMaps.secondCustomLabelGroup.size) {
                this.#reloadDynamicLabels(VenueTemplateLabelGroupType.secondCustomLabelGroup, secondCustomGroup.customGroupId);
            }
        }
    }

    #createLabel(
        labelGroup: VenueTemplateLabelGroup, id: number, literal: string,
        code: string, defaultLabel: boolean, color: string, fixed = false
    ): VenueTemplateLabel {
        while (color?.length < 6) {
            color = '0' + color;
        }
        color = color ?? 'FFFFFF';
        return {
            id: String(id),
            labelGroupId: labelGroup.id,
            literal,
            code,
            default: defaultLabel,
            color: '#' + color,
            disabled: labelGroup.disabled,
            deletable: !labelGroup.disabled && !defaultLabel && labelGroup.deletable && !fixed,
            editable: labelGroup.editable && !fixed,
            count: 0
        };
    }

    #addDynamicLabels<T>(
        labelGroups: VenueTemplateLabelGroup[],
        type: VenueTemplateLabelGroupType,
        content: T[],
        labelGenerator: (element: T, labelGroup: VenueTemplateLabelGroup) => VenueTemplateLabel
    ): void {
        if (content) {
            const labelGroup: VenueTemplateLabelGroup = labelGroups?.find(labelGroup => labelGroup.id === type);
            if (labelGroup) {
                labelGroup.labels = content.map(element => labelGenerator(element, labelGroup));
                if (labelGroup.sorted) {
                    labelGroup.labels.sort((a, b) => a.literal.toLowerCase() > b.literal.toLowerCase() ? 1 : -1);
                }
            }
        }
    }
}
