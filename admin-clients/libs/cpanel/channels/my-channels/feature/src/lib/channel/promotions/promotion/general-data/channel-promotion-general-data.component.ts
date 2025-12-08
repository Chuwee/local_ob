import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { CommunicationTextContent, CommunicationTextContentFormData } from '@admin-clients/cpanel/shared/data-access';
import {
    ChannelPromotion, ChannelPromotionFieldRestrictions, ChannelPromotionsService
} from '@admin-clients/cpanel-channels-promotions-data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommunicationTextContentComponent, convertContentsIntoFormData } from '@admin-clients/shared-common-ui-communication-texts';
import { AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef, inject, QueryList, ViewChild, ViewChildren } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { combineLatest, forkJoin, throwError, type Observable } from 'rxjs';
import { filter, first, map, shareReplay, tap, withLatestFrom } from 'rxjs/operators';

@Component({
    selector: 'app-channel-promotion-general-data',
    templateUrl: './channel-promotion-general-data.component.html',
    styleUrls: ['./channel-promotion-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelPromotionGeneralDataComponent implements AfterViewInit, WritingComponent {
    readonly #onDestroy = inject(DestroyRef);
    readonly #channelSrv = inject(ChannelsService);
    readonly #channelPromotionsSrv = inject(ChannelPromotionsService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);

    #promotionId: number;
    #channelId: number;
    #contents$ = this.#channelPromotionsSrv.getPromotionContents$()
        .pipe(
            filter(Boolean),
            map(convertContentsIntoFormData)
        );

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanels: QueryList<MatExpansionPanel>;

    @ViewChild(CommunicationTextContentComponent)
    private _communicationContent: CommunicationTextContentComponent;

    readonly fieldRestrictions = ChannelPromotionFieldRestrictions;

    readonly promotion$ = this.#channelPromotionsSrv.getPromotion$()
        .pipe(
            filter(Boolean),
            withLatestFrom(this.#channelSrv.getChannel$()),
            tap(([promotion, channel]) => {
                this.#channelId = channel.id;
                this.#promotionId = promotion.id;
            }),
            map(([promotion]) => promotion),
            takeUntilDestroyed(this.#onDestroy),
            shareReplay(1)
        );

    readonly languages$ = this.#channelSrv.getChannel$()
        .pipe(
            first(Boolean),
            map(channel => channel.languages.selected),
            shareReplay(1)
        );

    readonly loading$ = booleanOrMerge([
        this.#channelPromotionsSrv.isPromotionInProgress$(),
        this.#channelPromotionsSrv.isPromotionContentsInProgress$()
    ]);

    readonly error$ = combineLatest([
        this.#channelPromotionsSrv.getPromotionError$(),
        this.#channelPromotionsSrv.getPromotionContentsError$()
    ]).pipe(map(errors => errors.some(error => !!error)));

    readonly form = this.#fb.group({
        name: [null, [
            Validators.required,
            Validators.minLength(this.fieldRestrictions.minNameLength),
            Validators.maxLength(this.fieldRestrictions.maxNameLength)
        ]],
        useNameForCommunication: null,
        contents: this.#fb.group({})
    });

    ngAfterViewInit(): void {
        combineLatest([
            this.promotion$,
            this.#contents$,
            this.languages$
        ]).pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(([promotion, contents, langs]) => {
                this.initFormChangesHandlers(langs);
                this.updateForm(promotion, contents, langs);
            });
    }

    cancel(): void {
        this.loadPromotionAndContents();
    }

    save$(): Observable<void[]> {
        if (this.form.valid) {
            const data = this.form.value;
            const promotion: ChannelPromotion = { name: data.name };
            const contents: CommunicationTextContent[] = this._communicationContent.getContents();
            return forkJoin([
                this.#channelPromotionsSrv.updatePromotion(this.#channelId, this.#promotionId, promotion),
                this.#channelPromotionsSrv.updatePromotionContents(this.#channelId, this.#promotionId, contents)
            ])
                .pipe(tap(() => {
                    this.#ephemeralMessageService.showSaveSuccess();
                    // refresh list when name changed
                    if (this.form.controls['name'].touched) {
                        this.#channelPromotionsSrv.loadPromotionsList(this.#channelId, {
                            limit: 999, offset: 0, sort: 'name:asc'
                        });
                    }
                    this.loadPromotionAndContents();
                }));
        } else {
            this.showValidationErrors();
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe();
    }

    private showValidationErrors(): void {
        this.form.markAllAsTouched();
        this._communicationContent.showValidationErrors();
        scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanels);
    }

    private initFormChangesHandlers(langs: string[]): void {
        combineLatest([
            this.form.get('useNameForCommunication').valueChanges,
            this.form.get('name').valueChanges
        ])
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(([useNameForCommunication, name]) => {
                if (useNameForCommunication) {
                    // patch Promotion name as Communication Name for every language
                    const contents = Object.assign({}, ...langs.map(lang => ({ [lang]: { name } })));
                    this.form.patchValue({ contents });
                    langs.forEach(lang => this.form.get(['contents', lang, 'name']).disable({ emitEvent: false }));
                    this.form.get('contents').markAsDirty();
                } else {
                    this.form.get('contents').enable();
                }
            });
    }

    private updateForm(
        promo: ChannelPromotion,
        contents: CommunicationTextContentFormData,
        langs: string[]
    ): void {
        this.form.reset();
        const languages = Object.keys(contents);
        const useNameForCommunication = languages.length === langs.length &&
            languages.every(lang => contents[lang]['name'] === promo.name);
        this.form.patchValue({
            name: promo.name,
            useNameForCommunication,
            contents
        });
        this.form.markAsPristine();
    }

    private loadPromotionAndContents(): void {
        this.#channelPromotionsSrv.clearPromotionContents();
        this.#channelPromotionsSrv.loadPromotion(this.#channelId, this.#promotionId);
        this.#channelPromotionsSrv.loadPromotionContents(this.#channelId, this.#promotionId);
    }
}
