import {
    FormControlErrorsComponent,
    scrollIntoFirstInvalidFieldOrErrorMsg
} from '@OneboxTM/feature-form-control-errors';
import { PromotionFieldRestrictions } from '@admin-clients/cpanel/promoters/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { ArchivedEventMgrComponent } from '@admin-clients/cpanel/promoters/events/feature';
import { EventPromotion, EventPromotionsService } from '@admin-clients/cpanel/promoters/events/promotions/data-access';
import {
    CommunicationContentTextType,
    CommunicationTextContent
} from '@admin-clients/cpanel/shared/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
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
        TranslatePipe,
        FlexLayoutModule,
        FormControlErrorsComponent,
        CommunicationTextContentComponent,
        ArchivedEventMgrComponent,
        CommonModule
    ],
    selector: 'app-event-promotion-general-data',
    templateUrl: './event-promotion-general-data.component.html',
    styleUrls: ['./event-promotion-general-data.component.scss']
})
export class EventPromotionGeneralDataComponent implements AfterViewInit, WritingComponent {
    private readonly _eventsService = inject(EventsService);
    private readonly _eventPromotionsService = inject(EventPromotionsService);
    private readonly _fb = inject(UntypedFormBuilder);
    private readonly _ephemeralMsg = inject(EphemeralMessageService);
    private readonly _destroyRef = inject(DestroyRef);

    @ViewChildren(MatExpansionPanel)
    private readonly _matExpansionPanels: QueryList<MatExpansionPanel>;

    @ViewChild(CommunicationTextContentComponent)
    private readonly _communicationContent: CommunicationTextContentComponent;

    private _promotionId: number;
    private _eventId: number;
    private _contents$ = this._eventPromotionsService.promotionChannelTextContents.get$()
        .pipe(
            filter(contents => !!contents),
            map(convertContentsIntoFormData),
            takeUntilDestroyed(),
            shareReplay(1)
        );

    readonly fieldRestrictions = PromotionFieldRestrictions;
    readonly promotion$ = this._eventPromotionsService.promotion.get$()
        .pipe(
            filter(promotion => !!promotion),
            withLatestFrom(this._eventsService.event.get$()),
            tap(([promotion, event]) => {
                this._eventId = event.id;
                this._promotionId = promotion.id;
            }),
            map(([promotion]) => promotion),
            takeUntilDestroyed(),
            shareReplay(1)
        );

    readonly languages$ = this._eventsService.event.get$()
        .pipe(
            first(event => !!event),
            map(event => event.settings.languages.selected),
            takeUntilDestroyed(),
            shareReplay(1)
        );

    readonly reqInProgress$ = booleanOrMerge([
        this._eventPromotionsService.promotion.loading$(),
        this._eventPromotionsService.promotionChannelTextContents.loading$()
    ]);

    readonly error$ = combineLatest([
        this._eventPromotionsService.promotion.error$(),
        this._eventPromotionsService.promotion.error$()
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
        ])
            .pipe(takeUntilDestroyed(this._destroyRef))
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
            const promotion: EventPromotion = { name: data.name };
            const contents: CommunicationTextContent[] = this._communicationContent.getContents();

            return forkJoin([
                this._eventPromotionsService.promotion.update(this._eventId, this._promotionId, promotion),
                this._eventPromotionsService.promotionChannelTextContents.update(this._eventId, this._promotionId, contents)
            ]).pipe(tap(() => {
                this._ephemeralMsg.showSaveSuccess();
                if (this.form.controls['name'].touched) {
                    this._eventPromotionsService.promotionsList.load(this._eventId, {
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
        promo: EventPromotion,
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
        this._eventPromotionsService.promotionChannelTextContents.clear();
        this._eventPromotionsService.promotion.load(this._eventId, this._promotionId);
        this._eventPromotionsService.promotionChannelTextContents.load(this._eventId, this._promotionId);
    }

}
