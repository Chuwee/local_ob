import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, map, shareReplay, takeUntil, tap } from 'rxjs/operators';
import {
    CommunicationContentTextType, CommunicationTextContent,
    CommunicationTextContentFormData
} from '@admin-clients/cpanel/shared/data-access';
import {
    VoucherGroupType, VoucherGroup, GiftCardGroupContentImage,
    VouchersService, VoucherGroupStatus
} from '@admin-clients/cpanel-vouchers-data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge, getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { convertContentsIntoFormData } from '@admin-clients/shared-common-ui-communication-texts';

@Component({
    selector: 'app-group-container',
    templateUrl: './group-container.component.html',
    styleUrls: ['./group-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class GroupContainerComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _voucherGroupId: number;
    private _contents$: Observable<CommunicationTextContentFormData>;

    reqInProgress$: Observable<boolean>;
    groupType: VoucherGroupType;
    deepPath$ = getDeepPath$(this._router, this._route);
    voucherGroup$: Observable<VoucherGroup>;
    statusCtrl: UntypedFormControl;
    voucherGroupType = VoucherGroupType;
    giftCardImageContents$: Observable<GiftCardGroupContentImage[]>;
    giftCardTextContents$: Observable<CommunicationTextContent[]>;

    constructor(
        private _voucherSrv: VouchersService,
        private _entitiesService: EntitiesBaseService,
        private _router: Router,
        private _route: ActivatedRoute,
        private _fb: UntypedFormBuilder,
        private _ephemeralSrv: EphemeralMessageService
    ) {
    }

    ngOnInit(): void {
        this.statusCtrl = this._fb.control({ value: null, disabled: true });
        this.reqInProgress$ = booleanOrMerge([
            this._voucherSrv.isVoucherGroupLoading$(),
            this._voucherSrv.isVoucherGroupContentsLoading$(),
            this._voucherSrv.isGiftCardGroupConfigLoading$(),
            this._voucherSrv.isGiftCardImageContentsLoading$(),
            this._voucherSrv.isGiftCardTextContentsLoading$()
        ]);
        this.voucherGroup$ = this._voucherSrv.getVoucherGroup$()
            .pipe(
                filter(group => !!group),
                tap(group => {
                    this.statusCtrl.patchValue(group?.status === VoucherGroupStatus.active);
                    this.statusCtrl.enable();
                    this._voucherGroupId = group.id;
                    this.groupType = group.type;
                    this._entitiesService.loadEntity(group.entity.id);
                    this._voucherSrv.loadVoucherGroupContents(group.id);
                    if (group.type === this.voucherGroupType.giftCard) {
                        this._voucherSrv.loadGiftCardGroupConfig(group.id);
                        this._voucherSrv.loadGiftCardImageContents(group.id);
                        this._voucherSrv.loadGiftCardTextContents(group.id);
                    }
                }),
                shareReplay(1)
            );

        this._contents$ = this._voucherSrv.getVoucherGroupContents$()
            .pipe(
                filter(contents => !!contents),
                map(convertContentsIntoFormData)
            );

        this.giftCardImageContents$ = this._voucherSrv.getGiftCardImageContents$();

        this.giftCardTextContents$ = this._voucherSrv.getGiftCardTextContents$();

        combineLatest([
            this.voucherGroup$,
            this._contents$,
            this.giftCardImageContents$,
            this.giftCardTextContents$
        ]).pipe(
            takeUntil(this._onDestroy)
        ).subscribe(([group, groupContents, images, giftCardContents]) => {
            const isActiveVoucherGroup = group.status === VoucherGroupStatus.active;
            this.statusCtrl.patchValue(isActiveVoucherGroup);
            if (Object.keys(groupContents).length === 0
                || (group.type !== this.voucherGroupType.giftCard && !group?.channels)
                || (group.type === this.voucherGroupType.giftCard
                    && (!group?.channels
                        || images?.length === 0
                        || !(giftCardContents?.some(content => content.type === CommunicationContentTextType.name))
                        || !(giftCardContents?.some(content => content.type === CommunicationContentTextType.description))
                        || !(giftCardContents?.some(content => content.type === CommunicationContentTextType.emailBody))
                        || !(giftCardContents?.some(content => content.type === CommunicationContentTextType.emailSubject))
                    )
                )
            ) {
                this.statusCtrl.disable();
            } else {
                this.statusCtrl.enable();
            }
        });
    }

    ngOnDestroy(): void {
        this._voucherSrv.clearVoucherGroupContents();
        this._voucherSrv.clearGiftCardGroupConfig();
        this._voucherSrv.clearGiftCardTextContents();
        this._voucherSrv.clearGiftCardImageContents();
        this._entitiesService.clearEntity();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    saveVoucherGroupStatus(isActive: boolean): void {
        const status: VoucherGroupStatus = isActive ? VoucherGroupStatus.active : VoucherGroupStatus.inactive;
        this._voucherSrv.updateVoucherGroupStatus(this._voucherGroupId, status)
            .subscribe(
                () => {
                    this._ephemeralSrv.showSaveSuccess();
                    this._voucherSrv.loadVoucherGroup(this._voucherGroupId);
                }
            );
    }

}
