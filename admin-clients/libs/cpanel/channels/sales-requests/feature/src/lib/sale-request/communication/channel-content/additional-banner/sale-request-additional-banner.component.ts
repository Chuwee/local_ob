import {
    SaleRequestAdditionalBannerTexts,
    SalesRequestsService,
    BannerContentTextsType
} from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { FormControlHandler, htmlContentMaxLengthValidator } from '@admin-clients/shared/utility/utils';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, first, map, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-sale-request-additional-banner',
    templateUrl: './sale-request-additional-banner.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SaleRequestAdditionalBannerComponent implements OnInit, OnDestroy, AfterViewInit {
    private _onDestroy = new Subject<void>();
    private _saleRequestId: number;
    private _selectedLanguage: string;
    private _texts$: Observable<SaleRequestAdditionalBannerTexts[]>;

    @Input() language$: Observable<string>;
    @Input() form: UntypedFormGroup;

    additionalBannerForm: UntypedFormGroup;

    constructor(
        private _fb: UntypedFormBuilder,
        private _saleRequestsService: SalesRequestsService,
        private _ref: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.additionalBannerForm = this._fb.group({
            additionalBanner: [null, htmlContentMaxLengthValidator(600)]
        });
        this.form.addControl('additionalBannerForm', this.additionalBannerForm);

        this.loadContents();
        this.language$.pipe(takeUntil(this._onDestroy)).subscribe(lang => this._selectedLanguage = lang);
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._saleRequestsService.clearSaleRequestAdditionalBannerTexts();
    }

    ngAfterViewInit(): void {
        this.refreshFormDataHandler();
    }

    cancel(): void {
        this.additionalBannerForm.markAsPristine();
        this._saleRequestsService.loadSaleRequestAdditionalBannerTexts(this._saleRequestId, this._selectedLanguage);
    }

    save(): Observable<void>[] {
        const obs$: Observable<void>[] = [];

        if (this.additionalBannerForm.valid) {
            const textsToSave: SaleRequestAdditionalBannerTexts[] = [];
            const field = this.additionalBannerForm.get('additionalBanner');
            if (field.dirty) {
                textsToSave.push({
                    language: this._selectedLanguage,
                    type: BannerContentTextsType.seatSelectionDisclaimer,
                    value: field.value
                });
            }
            if (textsToSave.length > 0) {
                obs$.push(this._saleRequestsService.saveSaleRequestAdditionalBannerTexts(this._saleRequestId, textsToSave).pipe(
                    tap(() => {
                        this._saleRequestsService.loadSaleRequestAdditionalBannerTexts(this._saleRequestId, this._selectedLanguage);
                    })));
            }
        }
        return obs$;
    }

    private loadContents(): void {
        this._saleRequestsService.getSaleRequest$()
            .pipe(
                first(value => !!value)
            ).subscribe(saleRequest => {
                this._saleRequestId = saleRequest.id;
                this._saleRequestsService.loadSaleRequestAdditionalBannerTexts(this._saleRequestId, this._selectedLanguage);
            });

        this._texts$ = combineLatest([
            this._saleRequestsService.getSaleRequestAdditionalBannerTexts$(),
            this.language$
        ]).pipe(
            filter(data => data.every(item => !!item)),
            map(([additionalBannerTexts, language]) => additionalBannerTexts.filter(text => text.language === language)),
            tap(additionalBannerTexts => {
                const field = this.additionalBannerForm.get('additionalBanner');
                field.reset();
                for (const text of additionalBannerTexts) {
                    field.setValue(text.value);
                    return;
                }
                this._ref.detectChanges();
                this.additionalBannerForm.markAsPristine();
            })
        );
    }

    private refreshFormDataHandler(): void {
        combineLatest([
            this.language$,
            this._texts$
        ])
            .pipe(
                takeUntil(this._onDestroy),
                filter(([language, texts]) => !!language && !!texts))
            .subscribe(([language, texts]) => {
                const field = this.additionalBannerForm.get('additionalBanner');
                for (const text of texts) {
                    if (text.language === language) {
                        FormControlHandler.checkAndRefreshDirtyState(field, text.value);
                        return;
                    }
                }
                FormControlHandler.checkAndRefreshDirtyState(field, null);
            });
    }
}
