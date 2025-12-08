import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Channel, ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import {
    ChannelPromotion, ChannelPromotionsService, ChannelPromotionEventScope,
    PutChannelPromotionEvents, PutChannelPromotionSessions, PutChannelPromotionPriceTypes
} from '@admin-clients/cpanel-channels-promotions-data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, OnInit, QueryList, ViewChildren } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UntypedFormBuilder, UntypedFormGroup, ValidationErrors, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { Router } from '@angular/router';
import { forkJoin, Observable, of, throwError } from 'rxjs';
import { catchError, filter, mergeMap, tap, withLatestFrom } from 'rxjs/operators';
import { PromotionEventListElement } from '../events/promotion-events.model';

@Component({
    selector: 'app-channel-promotion-zones',
    templateUrl: './channel-promotion-zones.component.html',
    styleUrls: ['./channel-promotion-zones.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelPromotionZonesComponent implements OnInit, WritingComponent {
    readonly #router = inject(Router);
    readonly #channelPromotionsService = inject(ChannelPromotionsService);
    readonly #channelService = inject(ChannelsService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #onDestroy = inject(DestroyRef);

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanels: QueryList<MatExpansionPanel>;

    readonly reqInProgress$ = booleanOrMerge([
        this.#channelPromotionsService.isPromotionInProgress$(),
        this.#channelPromotionsService.isPromotionEventsInProgress$(),
        this.#channelPromotionsService.isPromotionSessionsInProgress$()
    ]);

    channel: Channel;
    promotion: ChannelPromotion;
    errors: Record<string, boolean> = {};
    form: UntypedFormGroup;

    ngOnInit(): void {
        this.initForm();

        this.#channelPromotionsService.getPromotion$()
            .pipe(
                filter(promotion => !!promotion),
                withLatestFrom(this.#channelService.getChannel$()),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(([promotion, channel]) => {
                this.channel = channel;
                this.promotion = promotion;
                if (promotion.subtype === 'PACK') {
                    this.#router.navigate(['/channels', channel.id, 'promotions', promotion.id, 'general-data']);
                }
            });
    }

    cancel(): void {
        this.loadPromotionModels();
    }

    save$(): Observable<void[]> {
        const formEvents = this.form.get('events');
        const formSessions = this.form.get('sessions');
        const formPriceTypes = this.form.get('price_types');

        type ElemType = { [key: string]: unknown };

        // validations and filter sessions/pricetypes not in restricted selected events
        if (formEvents.get('type').value === ChannelPromotionEventScope.restricted) {
            const selectedEvents: PromotionEventListElement[] = formEvents.value.selected;
            if (formSessions.dirty) {
                const filteredSelectedSessions = formSessions.value.selected?.filter((elem: ElemType) =>
                    selectedEvents?.find(event => event.saleReqId === elem['catalog_sale_request_id'])
                );
                const selectedSessionIds = formSessions.value.selected?.map((elem: ElemType) => elem['id']);
                const filteredSelectedSessionIds = filteredSelectedSessions?.map((elem: ElemType) => elem['id']);
                if (JSON.stringify(filteredSelectedSessionIds) !== JSON.stringify(selectedSessionIds)) {
                    formSessions.get('selected').setValue(filteredSelectedSessions);
                    formSessions.markAsDirty();
                }
            }
            if (formPriceTypes.dirty) {
                const filteredSelectedPriceTypes = formPriceTypes.value.selected?.filter((elem: ElemType) =>
                    selectedEvents?.find(event => event.saleReqId === elem['catalog_sale_request_id'])
                );
                const selectedPriceTypeIds = formPriceTypes.value.selected?.map((elem: ElemType) => elem['id']);
                const filteredSelectedPriceTypeIds = filteredSelectedPriceTypes?.map((elem: ElemType) => elem['id']);
                if (JSON.stringify(filteredSelectedPriceTypeIds) !== JSON.stringify(selectedPriceTypeIds)) {
                    formPriceTypes.get('selected').setValue(filteredSelectedPriceTypes);
                    formPriceTypes.markAsDirty();
                }
            }
        }

        if (this.form.valid) {
            this.errors = {};
            let updateEvents$: Observable<void> = of(null); // initializes with void in case events haven't changed

            if (this.form.get('events').dirty) {
                const selectedEventIds: number[] = formEvents.value.selected?.map((elem: ElemType) => elem['id']);
                const req: PutChannelPromotionEvents = { type: formEvents.value.type, events: selectedEventIds };
                updateEvents$ =
                    this.#channelPromotionsService.updatePromotionEvents(this.channel.id, this.promotion.id, req)
                        .pipe(
                            catchError(error => {
                                this.errors['savePromotionEvents'] = true;
                                this.#ref.detectChanges();
                                throw error;
                            })
                        );
            }
            const eventRelatedUpdates$: Observable<void>[] = [of(null)];  // initializes with void in case sessions haven't changed

            if (this.form.get('sessions').dirty) {
                const selectedSessionIds = formSessions.value.selected?.map((elem: ElemType) => elem['id']);
                const req: PutChannelPromotionSessions = { type: formSessions.value.type, sessions: selectedSessionIds };
                eventRelatedUpdates$.push(
                    this.#channelPromotionsService.updatePromotionSessions(this.channel.id, this.promotion.id, req)
                        .pipe(
                            catchError(error => {
                                this.errors['savePromotionSessions'] = true;
                                this.#ref.detectChanges();
                                throw error;
                            })
                        )
                );
            }

            if (formPriceTypes.dirty) {
                const selectedPriceTypeIds = formPriceTypes.value.selected?.map((elem: ElemType) => elem['id']);
                const req: PutChannelPromotionPriceTypes = { type: formPriceTypes.value.type, price_types: selectedPriceTypeIds };
                eventRelatedUpdates$.push(
                    this.#channelPromotionsService.updatePromotionPriceTypes(this.channel.id, this.promotion.id, req)
                        .pipe(
                            catchError(error => {
                                this.errors['savePromotionPriceTypes'] = true;
                                this.#ref.detectChanges();
                                throw error;
                            })
                        )
                );
            }

            if (this.form.get('packs').dirty) {
                const packs = this.form.get('packs').value;
                const promotion: ChannelPromotion = { packs };
                eventRelatedUpdates$.push(
                    this.#channelPromotionsService.updatePromotion(this.channel.id, this.promotion.id, promotion)
                        .pipe(
                            catchError(error => {
                                this.errors['savePacks'] = true;
                                this.#ref.detectChanges();
                                throw error;
                            })
                        )
                );
            }

            return updateEvents$.pipe(
                mergeMap(() => forkJoin(eventRelatedUpdates$))
            ).pipe(
                tap(() => {
                    this.#ephemeralMessageService.showSaveSuccess();
                    this.loadPromotionModels();
                })
            );
        } else {
            this.form.markAllAsTouched();
            // workaraund to refresh validations and show them
            this.form.get('events.type').setValue(this.form.get('events.type').value);
            this.form.get('sessions.type').setValue(this.form.get('sessions.type').value);
            this.form.get('packs').patchValue(this.form.get('packs')?.value);
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanels);
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe(() => {
            this.form.markAsPristine();
        });
    }

    private initForm(): void {
        this.form = this.#fb.group({
            events: this.#fb.group({
                type: [null, Validators.required],
                selected: [{ value: [], disabled: true }, Validators.required]
            }),
            sessions: this.#fb.group({
                type: [{ value: null, disabled: true }, Validators.required],
                selected: [{ value: [], disabled: true }, Validators.required]
            }),
            price_types: this.#fb.group({
                type: [{ value: null, disabled: true }, Validators.required],
                selected: [{ value: [], disabled: true }, Validators.required]
            }),
            packs: this.#fb.group({
                enabled: false,
                events: [
                    { value: null, disabled: true },
                    [Validators.required, Validators.min(1)]
                ],
                sessions: [
                    { value: null, disabled: true },
                    [Validators.required, Validators.min(2)]
                ]
            }, { validators: [this.sessionsGreaterThanEvents()] })
        });
    }

    private loadPromotionModels(): void {
        this.#channelPromotionsService.loadPromotion(this.channel.id, this.promotion.id);
        this.#channelPromotionsService.loadPromotionEvents(this.channel.id, this.promotion.id);
        this.#channelPromotionsService.loadPromotionSessions(this.channel.id, this.promotion.id);
        this.#channelPromotionsService.loadPromotionPriceTypes(this.channel.id, this.promotion.id);
    }

    private sessionsGreaterThanEvents() {
        return (group: UntypedFormGroup): ValidationErrors | null => {
            const hasError = group.get('sessions').value < group.get('events').value;
            return hasError ? { sessionsGreaterThanEvents: true } : null;
        };
    }
}
