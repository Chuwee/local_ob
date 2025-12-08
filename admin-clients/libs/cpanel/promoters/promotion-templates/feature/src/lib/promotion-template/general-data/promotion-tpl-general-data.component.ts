import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { PromotionFieldRestrictions } from '@admin-clients/cpanel/promoters/data-access';
import { PromotionTpl, PromotionTplsService } from '@admin-clients/cpanel/promoters/promotion-templates/data-access';
import {
    CommunicationContentTextType,
    CommunicationTextContent,
    CommunicationTextContentFormData
} from '@admin-clients/cpanel/shared/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService, HelpButtonComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { CommunicationTextContentComponent, convertContentsIntoFormData } from '@admin-clients/shared-common-ui-communication-texts';
import { DefaultIconComponent } from '@admin-clients/shared-common-ui-default-icon';
import { AsyncPipe } from '@angular/common';
import {
    AfterViewInit, ChangeDetectionStrategy, Component, OnInit, viewChildren, viewChild, inject, DestroyRef
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, forkJoin, Observable, throwError } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, tap } from 'rxjs/operators';
import { favoritePromotionLiterals } from '../../models/favorite-promotion-literals';

@Component({
    selector: 'app-promotion-tpl-general-data',
    templateUrl: './promotion-tpl-general-data.component.html',
    styleUrls: ['./promotion-tpl-general-data.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, AsyncPipe, DefaultIconComponent, TranslatePipe,
        HelpButtonComponent, MaterialModule, ReactiveFormsModule, FormControlErrorsComponent,
        CommunicationTextContentComponent
    ]
})
export class PromotionTplGeneralDataComponent implements OnInit, AfterViewInit {
    readonly #onDestroy = inject(DestroyRef);
    readonly #promotionTplsSrv = inject(PromotionTplsService);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ephemeralMsg = inject(EphemeralMessageService);

    private readonly _matExpansionPanels = viewChildren(MatExpansionPanel);
    private readonly _communicationContent = viewChild(CommunicationTextContentComponent);

    private _promotionId: number;
    private _promotion$: Observable<PromotionTpl>;
    private _contents$: Observable<CommunicationTextContentFormData>;

    readonly fieldRestrictions = PromotionFieldRestrictions;
    languages$: Observable<string[]>;
    reqInProgress$: Observable<boolean>;
    error$: Observable<boolean>;
    form: UntypedFormGroup;
    promotionTemplate: PromotionTpl;
    updateFavorite: (promotionTplId: number, isFavorite: boolean) => Observable<boolean>;
    readonly favoritePromotionLiterals = favoritePromotionLiterals;

    ngOnInit(): void {
        this.form = this.#fb.group({
            name: [null, [
                Validators.required,
                Validators.minLength(this.fieldRestrictions.minNameLength),
                Validators.maxLength(this.fieldRestrictions.maxNameLength)
            ]],
            useNameForCommunication: null,
            contents: this.#fb.group({})
        });

        this.error$ = combineLatest([
            this.#promotionTplsSrv.getPromotionTemplateError$(),
            this.#promotionTplsSrv.getPromotionTplChannelContentsError$()
        ]).pipe(map(errors => errors.some(error => !!error)));

        this.reqInProgress$ = booleanOrMerge([
            this.#promotionTplsSrv.isPromotionTemplateLoading$(),
            this.#promotionTplsSrv.isPromotionTemplateSaving$(),
            this.#promotionTplsSrv.isPromotionTplChannelContentsLoading$(),
            this.#promotionTplsSrv.isPromotionTplChannelContentsSaving$()
        ]);

        this._promotion$ = this.#promotionTplsSrv.getPromotionTemplate$()
            .pipe(
                filter(promotion => !!promotion),
                tap(promotion => {
                    this._promotionId = promotion.id;
                    this.promotionTemplate = promotion;
                }),
                takeUntilDestroyed(this.#onDestroy),
                shareReplay(1)
            );

        this._contents$ = this.#promotionTplsSrv.getPromotionTplChannelContents$()
            .pipe(
                filter(contents => !!contents),
                map(convertContentsIntoFormData),
                takeUntilDestroyed(this.#onDestroy),
                shareReplay(1)
            );

        this.languages$ = this.#promotionTplsSrv.getPromotionTemplate$()
            .pipe(
                first(promoTpl => !!promoTpl),
                switchMap(promoTpl => {
                    this.#entitiesService.loadEntity(promoTpl.entity.id);
                    return this.#entitiesService.getEntity$();
                }),
                filter(entity => !!entity),
                map(entity => entity.settings?.languages.available),
                takeUntilDestroyed(this.#onDestroy),
                shareReplay(1)
            );

        this.updateFavorite = (promotionTplId, isFavorite) =>
            this.#promotionTplsSrv.updatePromotionTemplateFavorite(promotionTplId, isFavorite)
                .pipe(tap(() => this.loadPromotionAndContents()));
    }

    ngAfterViewInit(): void {
        combineLatest([
            this._promotion$,
            this._contents$,
            this.languages$
        ])
            .pipe(takeUntilDestroyed(this.#onDestroy))
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
            const promotion: PromotionTpl = { name: data.name };
            const contents: CommunicationTextContent[] = this._communicationContent().getContents();

            return forkJoin([
                this.#promotionTplsSrv.savePromotionTemplate(this._promotionId, promotion),
                this.#promotionTplsSrv.savePromotionTplChannelContents(this._promotionId, contents)
            ]).pipe(
                tap(() => {
                    this.#ephemeralMsg.showSaveSuccess();
                    this.loadPromotionAndContents();
                })
            );

        } else {
            this.form.markAllAsTouched();
            this._communicationContent().showValidationErrors();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanels());
            return throwError(() => 'invalid fields');
        }
    }

    save(): void {
        this.save$().subscribe();
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
                    langs.forEach(lang => this.form.get(['contents', lang, 'name'])?.disable({ emitEvent: false }));
                    this.form.get('contents').markAsDirty();
                } else {
                    this.form.get('contents').enable();
                }
            });

        combineLatest([
            this._promotion$,
            this._contents$,
            this.form.valueChanges // only used as a trigger
        ])
            .pipe(
                takeUntilDestroyed(this.#onDestroy)
            )
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
        promo: PromotionTpl,
        contents: CommunicationTextContentFormData,
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
        this.#promotionTplsSrv.clearPromotionTplChannelContents();
        this.#promotionTplsSrv.loadPromotionTemplate(this._promotionId);
        this.#promotionTplsSrv.loadPromotionTplChannelContents(this._promotionId);
    }

}
