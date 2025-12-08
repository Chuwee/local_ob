import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    AfterViewInit, ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, QueryList, ViewChild, ViewChildren
} from '@angular/core';
import { AbstractControl, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { combineLatest, concat, Observable, of, shareReplay, Subject, throwError } from 'rxjs';
import { catchError, filter, first, map, switchMap, takeUntil, tap } from 'rxjs/operators';
import { GetChannelsRequest, ChannelType, ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { CommunicationTextContent, CommunicationTextContentFormData } from '@admin-clients/cpanel/shared/data-access';
import {
    PutVoucherGroup, VoucherChannelsType, VoucherGroup,
    VoucherGroupFieldRestrictions, VoucherGroupType, VouchersService
} from '@admin-clients/cpanel-vouchers-data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService, SearchablePaginatedSelectionLoadEvent } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { CommunicationTextContentComponent, convertContentsIntoFormData } from '@admin-clients/shared-common-ui-communication-texts';

const PAGE_SIZE = 10;

@Component({
    selector: 'app-group-redeem',
    templateUrl: './group-redeem.component.html',
    styleUrls: ['./group-redeem.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class GroupRedeemComponent implements OnInit, OnDestroy, AfterViewInit {
    private readonly _onDestroy = new Subject<void>();

    private readonly _voucherSrv = inject(VouchersService);
    private readonly _entitiesService = inject(EntitiesBaseService);
    private readonly _channelsService = inject(ChannelsService);
    private readonly _fb = inject(UntypedFormBuilder);
    private readonly _ephemeralSrv = inject(EphemeralMessageService);

    private _filters: GetChannelsRequest = { limit: PAGE_SIZE, offset: 0 };
    private _groupId: number;

    @ViewChild(CommunicationTextContentComponent)
    private _communicationContent: CommunicationTextContentComponent;

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanels: QueryList<MatExpansionPanel>;

    readonly fieldRestrictions = VoucherGroupFieldRestrictions;
    readonly channelType = ChannelType;
    readonly pageSize = PAGE_SIZE;
    readonly voucherChannelsType = VoucherChannelsType;
    readonly voucherGroupType = VoucherGroupType;

    readonly form = this._fb.group({
        contents: this._fb.group({
            name: [null, [Validators.required]]
        }),
        channels: this._fb.group({
            type: [null, Validators.required],
            selected: [{ value: [], disabled: true }, Validators.required]
        })
    });

    readonly reqInProgress$ = booleanOrMerge([
        this._voucherSrv.isVoucherGroupSaving$(),
        this._voucherSrv.isVoucherGroupContentsSaving$(),
        this._channelsService.isChannelsListLoading$()
    ]);

    readonly languages$ = this._entitiesService.getEntity$()
        .pipe(
            filter(Boolean),
            map(entity => entity.settings?.languages.available)
        );

    readonly channelsListData$ = this._voucherSrv.getVoucherGroup$()
        .pipe(
            first(Boolean),
            switchMap(group => {
                this._groupId = group.id;
                this._filters = {
                    ...this._filters,
                    limit: this.pageSize,
                    offset: 0,
                    sort: 'name:asc',
                    entityId: group.entity.id
                };
                this._channelsService.channelsList.load(this._filters);
                return this._channelsService.channelsList.getList$();
            }),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly channelsListMetadata$ = this._channelsService.channelsList.getMetadata$();

    get selectedForm(): AbstractControl {
        return this.form.get(['channels', 'selected']);
    }

    ngOnInit(): void {
        this.form.get(['channels', 'type']).valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(type => {
                if (type === VoucherChannelsType.all) {
                    this.selectedForm.disable();
                } else {
                    this.selectedForm.enable();
                }
            });
    }

    ngAfterViewInit(): void {
        this.formChangesHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._channelsService.channelsList.clear();
    }

    filterChannels(type: ChannelType): void {
        this._filters = { ...this._filters, type: type || null };
        this._channelsService.channelsList.load(this._filters);
    }

    loadChannelsList({ limit, q, offset }: SearchablePaginatedSelectionLoadEvent): void {
        this._filters = { ...this._filters, limit, offset, name: q?.length ? q : null };
        if (this._filters.entityId) {
            this._channelsService.channelsList.load(this._filters);
        }
    }

    cancel(): void {
        this.loadVoucherGroup();
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            let contentsObs$: Observable<void>;
            let channelsObs$: Observable<void>;
            const channelsForm = this.form.get('channels');
            const contentsForm = this.form.get('contents');
            if (channelsForm?.dirty) {
                const updatedVoucherGroup: PutVoucherGroup = {
                    channels: {
                        scope: channelsForm.value.type,
                        ids: channelsForm.value.type === VoucherChannelsType.filtered ?
                            this.selectedForm.value?.map(channel => channel.id) : []
                    }
                };
                channelsObs$ = this._voucherSrv.saveVoucherGroupChannels(this._groupId, updatedVoucherGroup);
            }
            if (contentsForm?.dirty) {
                const redeemContents: CommunicationTextContent[] = this._communicationContent.getContents();
                contentsObs$ = this._voucherSrv.saveVoucherGroupContents(this._groupId, redeemContents);
            }
            if (channelsObs$) {
                if (!contentsObs$) {
                    contentsObs$ = of();
                }
                return concat(contentsObs$, channelsObs$)
                    .pipe(
                        catchError(error => throwError(error)),
                        tap(() => {
                            this._ephemeralSrv.showSaveSuccess();
                            this.loadVoucherGroup();
                        })
                    );
            } else {
                return contentsObs$.pipe(tap(() => {
                    this._ephemeralSrv.showSaveSuccess();
                    this.loadVoucherGroup();
                }));
            }
        } else {
            this.showValidationErrors();
            return throwError(() => 'invalid form');
        }
    }

    private formChangesHandler(): void {
        combineLatest([
            this._voucherSrv.getVoucherGroup$(),
            this._voucherSrv.getVoucherGroupContents$()
                .pipe(
                    filter(Boolean),
                    map(convertContentsIntoFormData),
                    shareReplay({ refCount: true, bufferSize: 1 })
                ),
            this.languages$ // only used as a trigger
        ])
            .pipe(
                filter(sources => sources.every(Boolean)),
                takeUntil(this._onDestroy)
            )
            .subscribe(([group, redeemContents]) => {
                this.updateForm(redeemContents, group);
                FormControlHandler.checkAndRefreshDirtyState(this.form.get(['channels', 'type']), group?.type);
                FormControlHandler.checkAndRefreshDirtyState(this.selectedForm, group?.channels?.items || []);
            });
    }

    private showValidationErrors(): void {
        this.form.markAllAsTouched();
        this._communicationContent.showValidationErrors();
        scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanels);
    }

    private updateForm(contents: CommunicationTextContentFormData, group: VoucherGroup): void {
        this.form.patchValue({ contents, channels: { type: group?.channels?.scope, selected: group?.channels?.items } });
    }

    private loadVoucherGroup(): void {
        this._voucherSrv.loadVoucherGroup(this._groupId);
        this.form.markAsPristine();
    }

}
