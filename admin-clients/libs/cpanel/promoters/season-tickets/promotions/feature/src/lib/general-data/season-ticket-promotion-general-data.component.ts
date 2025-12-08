import {
    FormControlErrorsComponent,
    scrollIntoFirstInvalidFieldOrErrorMsg
} from '@OneboxTM/feature-form-control-errors';
import { PromotionFieldRestrictions } from '@admin-clients/cpanel/promoters/data-access';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    SeasonTicketPromotion
    , SeasonTicketPromotionsService
} from '@admin-clients/cpanel/promoters/season-tickets/promotions/data-access';
import {
    CommunicationContentTextType,
    CommunicationTextContent
} from '@admin-clients/cpanel/shared/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { CommunicationTextContentComponent, convertContentsIntoFormData } from '@admin-clients/shared-common-ui-communication-texts';
import { CommonModule } from '@angular/common';
import {
    AfterViewInit,
    ChangeDetectionStrategy,
    Component, DestroyRef,
    inject,
    QueryList,
    ViewChild,
    ViewChildren
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, forkJoin, Observable, throwError } from 'rxjs';
import { filter, first, map, shareReplay, tap, withLatestFrom } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent,
        ReactiveFormsModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        FormControlErrorsComponent,
        CommunicationTextContentComponent,
        CommonModule
    ],
    selector: 'app-season-ticket-promotion-general-data',
    templateUrl: './season-ticket-promotion-general-data.component.html',
    styleUrls: ['./season-ticket-promotion-general-data.component.scss']
})
export class SeasonTicketPromotionGeneralDataComponent implements AfterViewInit {
    private readonly _seasonTicketsService = inject(SeasonTicketsService);
    private readonly _stPromotionsSrv = inject(SeasonTicketPromotionsService);
    private readonly _fb = inject(UntypedFormBuilder);
    private readonly _ephemeralMessageSrv = inject(EphemeralMessageService);
    private readonly _destroyRef = inject(DestroyRef);

    @ViewChildren(MatExpansionPanel)
    private readonly _matExpansionPanels: QueryList<MatExpansionPanel>;

    @ViewChild(CommunicationTextContentComponent)
    private readonly _communicationContent: CommunicationTextContentComponent;

    private _promotionId: number;
    private _seasonTicketId: number;
    private readonly _contents$ = this._stPromotionsSrv.promotionChannelTextContents.get$()
        .pipe(
            filter(Boolean),
            map(convertContentsIntoFormData),
            takeUntilDestroyed(this._destroyRef),
            shareReplay(1)
        );

    readonly fieldRestrictions = PromotionFieldRestrictions;
    readonly promotion$ = this._stPromotionsSrv.promotion.get$()
        .pipe(
            filter(promotion => !!promotion),
            withLatestFrom(this._seasonTicketsService.seasonTicket.get$()),
            tap(([promotion, seasonTicket]) => {
                this._seasonTicketId = seasonTicket.id;
                this._promotionId = promotion.id;
            }),
            map(([promotion]) => promotion),
            takeUntilDestroyed(this._destroyRef),
            shareReplay(1)
        );

    readonly languages$ = this._seasonTicketsService.seasonTicket.get$()
        .pipe(
            first(seasonTicket => !!seasonTicket),
            map(seasonTicket => seasonTicket.settings.languages.selected),
            takeUntilDestroyed(this._destroyRef),
            shareReplay(1)
        );

    readonly reqInProgress$ = booleanOrMerge([
        this._stPromotionsSrv.promotion.loading$(),
        this._stPromotionsSrv.promotionChannelTextContents.loading$()
    ]);

    readonly error$ = combineLatest([
        this._stPromotionsSrv.promotion.error$(),
        this._stPromotionsSrv.promotionChannelTextContents.error$()
    ]).pipe(map(errors => errors.some(error => !!error)));

    readonly form = this._fb.group({
        name: [null, [
            Validators.required,
            Validators.minLength(this.fieldRestrictions.minNameLength),
            Validators.maxLength(this.fieldRestrictions.maxNameLength)
        ]],
        useNameForCommunication: null,
        contents: this._fb.group({})
    });

    ngAfterViewInit(): void {
        combineLatest([
            this.promotion$,
            this._contents$,
            this.languages$
        ]).pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(([promotion, contents, langs]) => {
                this.initFormChangesHandlers(langs);
                this.updateForm(promotion, contents, langs);
            });
    }

    cancel(): void {
        this.loadPromotionAndContents();
    }

    save$(): Observable<void | void[]> {
        if (this.form.valid) {
            const data = this.form.value;
            const promotion: SeasonTicketPromotion = { name: data.name };
            const contents: CommunicationTextContent[] = this._communicationContent.getContents();

            return forkJoin([
                this._stPromotionsSrv.promotion.update(this._seasonTicketId, this._promotionId, promotion),
                this._stPromotionsSrv.promotionChannelTextContents.update(this._seasonTicketId, this._promotionId, contents)
            ]).pipe(tap(() => {
                this._ephemeralMessageSrv.showSaveSuccess();
                if (this.form.controls['name'].touched) {
                    this._stPromotionsSrv.promotionsList.load(this._seasonTicketId, {
                        limit: 999, offset: 0, sort: 'name:asc'
                    });
                }
                this.loadPromotionAndContents();
            }));
        } else {
            this.showValidationErrors();
            return throwError(() => 'invalid fields');
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
            .pipe(takeUntilDestroyed(this._destroyRef))
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

        combineLatest([
            this.promotion$,
            this._contents$,
            this.form.valueChanges // only used as a trigger
        ])
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(([promo, contents]) => {
                FormControlHandler.checkAndRefreshDirtyState(this.form.get('name'), promo.name);
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('useNameForCommunication'),
                    this.form.get('useNameForCommunication').value
                );
                const comElemsLangs = Object.keys(contents);
                comElemsLangs.forEach(lang => {
                    const comElemsTypes = Object.values(CommunicationContentTextType);
                    comElemsTypes.forEach(textType => {
                        const type = textType.toLowerCase();
                        FormControlHandler.checkAndRefreshDirtyState(
                            this.form.get(['contents', lang, type]),
                            contents[lang][type] || ''
                        );
                    });
                });
            });
    }

    private updateForm(
        promo: SeasonTicketPromotion,
        contents: { [lang: string]: { [item: string]: string } },
        langs: string[]
    ): void {
        this.form.reset();
        const comElemsLangs = Object.keys(contents);
        const useNameForCommunication = comElemsLangs.length === langs.length &&
            comElemsLangs.every(lang => contents[lang]['name'] === promo.name);
        this.form.patchValue({
            name: promo.name,
            useNameForCommunication,
            contents
        });
        this.form.markAsPristine();
    }

    private loadPromotionAndContents(): void {
        this._stPromotionsSrv.promotionChannelTextContents.clear();
        this._stPromotionsSrv.promotion.load(this._seasonTicketId, this._promotionId);
        this._stPromotionsSrv.promotionChannelTextContents.load(this._seasonTicketId, this._promotionId);
    }

}
