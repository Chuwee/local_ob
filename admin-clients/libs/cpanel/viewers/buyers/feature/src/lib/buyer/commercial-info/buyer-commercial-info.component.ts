import { BuyersService } from '@admin-clients/cpanel-viewers-buyers-data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { IdName } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, UntypedFormArray } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { forkJoin, of, type Observable } from 'rxjs';
import { filter, map, shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { BuyerSubscriptionListSelDialogComponent } from './buyer-subscription-list-sel-dialog/buyer-subscription-list-sel-dialog.component';

@Component({
    selector: 'app-buyer-commercial-info',
    templateUrl: './buyer-commercial-info.component.html',
    styleUrls: ['./buyer-commercial-info.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class BuyerCommercialInfoComponent implements OnInit, WritingComponent {
    readonly #changeDet = inject(ChangeDetectorRef);
    readonly #fb = inject(FormBuilder);
    readonly #translateSrv = inject(TranslateService);
    readonly #matDialog = inject(MatDialog);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #buyersSrv = inject(BuyersService);
    readonly #destroyRef = inject(DestroyRef);

    readonly error$ = this.#buyersSrv.getBuyerError$();

    readonly subscriptionLists: UntypedFormArray = this.#fb.array([]);

    readonly form = this.#fb.group({
        allowCommercialMailing: null as boolean,
        subscriptionLists: this.subscriptionLists
    });

    readonly reqInProgress$ = booleanOrMerge([
        this.#buyersSrv.isBuyerLoading$(),
        this.#buyersSrv.isUpdatingBuyer$()
    ]);

    channels$: Observable<IdName[]>;
    collectives$: Observable<IdName[]>;

    ngOnInit(): void {
        const notNullBuyer = this.#buyersSrv.getBuyer$().pipe(filter(buyer => !!buyer));
        this.channels$ = notNullBuyer.pipe(
            map(buyer => this.sortCol(buyer.channels)),
            shareReplay(1)
        );
        this.collectives$ = notNullBuyer.pipe(
            map(buyer => this.sortCol(buyer.collectives)),
            shareReplay(1)
        );
        this.initForm();
    }

    save$(): Observable<[string, void]> {
        return this.#buyersSrv.getBuyer$()
            .pipe(
                take(1),
                switchMap(buyer => forkJoin([
                    of(buyer.id),
                    this.#buyersSrv.updateBuyer({
                        id: buyer.id,
                        allow_commercial_mailing: this.form.value.allowCommercialMailing,
                        subscription_lists: this.getSubscriptionListsValue()
                    })
                ])),
                tap(([buyerId]) => {
                    this.#buyersSrv.loadBuyer(buyerId);
                    this.#ephemeralMessageService.showSaveSuccess();
                })
            );
    }

    save(): void {
        this.save$().subscribe();
    }

    cancel(): void {
        this.#buyersSrv.getBuyer$()
            .pipe(take(1))
            .subscribe(buyer => {
                this.form.reset();
                this.#buyersSrv.loadBuyer(buyer.id);
            });
    }

    addSubscriptionList(): void {
        this.#buyersSrv.getBuyer$()
            .pipe(take(1))
            .subscribe(buyer => {
                this.#matDialog.open(
                    BuyerSubscriptionListSelDialogComponent,
                    new ObMatDialogConfig(
                        {
                            entityId: buyer.entity_id,
                            availableSubscriptionLists: this.subscriptionLists.controls.map(control => control.value.id)
                        })
                )
                    .beforeClosed()
                    .subscribe(subscriptionLists => {
                        if (subscriptionLists?.length) {
                            this.subscriptionLists.controls
                                .map(control => control.value)
                                .forEach(subscriptionList => subscriptionLists.push(subscriptionList));
                            this.subscriptionLists.clear();
                            this.sortCol(subscriptionLists);
                            this.addSubscriptionListsControls(subscriptionLists);
                            this.subscriptionLists.markAsDirty();
                            this.#changeDet.markForCheck();
                        }
                    });
            });
    }

    removeSubscriptionList(idName: IdName): void {
        this.#messageDialogService.showWarn({
            size: DialogSize.SMALL,
            title: this.#translateSrv.instant('TITLES.WARNING'),
            message: this.#translateSrv.instant('BUYERS.DELETE_SUBSCRIPTION_LIST_WARN', { name: idName.name })
        })
            .subscribe(success => {
                if (success) {
                    let isFound = false;
                    this.subscriptionLists.controls.forEach((control, index) => {
                        if (!isFound && control.value.id === idName.id) {
                            this.subscriptionLists.markAsDirty();
                            this.subscriptionLists.removeAt(index);
                            this.#changeDet.markForCheck();
                            isFound = true;
                        }
                    });
                }
            });
    }

    private initForm(): void {
        this.#buyersSrv.getBuyer$()
            .pipe(
                filter(buyer => !!buyer),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(buyer => {
                this.form.reset();
                this.form.get('allowCommercialMailing').setValue(buyer.allow_commercial_mailing);
                this.subscriptionLists.clear();
                this.sortCol(buyer.subscription_lists);
                this.addSubscriptionListsControls(buyer.subscription_lists);
            });
    }

    private addSubscriptionListsControls(subscriptionLists: IdName[]): void {
        subscriptionLists?.forEach(subscriptionList => this.subscriptionLists.push(this.#fb.control(subscriptionList)));
    }

    private getSubscriptionListsValue(): IdName[] {
        return this.subscriptionLists.value.filter(idName => idName.id !== null);
    }

    private sortCol(col: IdName[]): IdName[] {
        return col?.sort((a, b) => a.name?.localeCompare(b.name)) || [];
    }
}
