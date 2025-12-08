import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit, QueryList, ViewChild }
    from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { combineLatest, Observable, of, Subject, throwError } from 'rxjs';
import { filter, map, takeUntil, tap, take } from 'rxjs/operators';
import { CommunicationTextContent, CommunicationContentTextType as TextType } from '@admin-clients/cpanel/shared/data-access';
import { VouchersService } from '@admin-clients/cpanel-vouchers-data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService,
    CurrencyInputComponent,
    TabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';

@Component({
    selector: 'app-gift-card-group-email',
    templateUrl: './gift-card-group-email.component.html',
    styleUrls: ['./gift-card-group-email.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class GiftCardGroupEmailComponent implements OnInit, OnDestroy, AfterViewInit {
    private _onDestroy = new Subject<void>();
    private _matExpansionPanels: QueryList<MatExpansionPanel>;
    private _giftCardGroupId: number;

    @ViewChild('languageTabs') private _languageTabs: TabsMenuComponent;
    @ViewChild(CurrencyInputComponent) private _input: CurrencyInputComponent;

    form: UntypedFormGroup;
    languages$: Observable<string[]>;
    reqInProgress$: Observable<boolean>;

    constructor(
        private _fb: UntypedFormBuilder,
        private _voucherSrv: VouchersService,
        private _entitiesService: EntitiesBaseService,
        private _ephemeralSrv: EphemeralMessageService,
        private _ref: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.form = this._fb.group({});

        this.reqInProgress$ = this._voucherSrv.isGiftCardTextContentsSaving$();

        this._voucherSrv.getVoucherGroup$()
            .pipe(
                filter(giftCardGroup => !!giftCardGroup),
                takeUntil(this._onDestroy)
            ).subscribe(giftCardGroup => {
                this._giftCardGroupId = giftCardGroup.id;
            });

        this.languages$ = this._entitiesService.getEntity$()
            .pipe(
                filter(entity => !!entity),
                map(entity => entity.settings?.languages.available),
                tap(langs => {
                    langs.forEach(lang => {
                        this.form.addControl(lang, this._fb.group({
                            subject: [null, [Validators.required]],
                            body: [null, [Validators.required]],
                            copyright: null
                        }));
                    });
                })
            );
    }

    ngAfterViewInit(): void {
        this.refreshFormDataHandler();
        this.formChangeHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    cancel(): void {
        this._voucherSrv.loadVoucherGroup(this._giftCardGroupId);
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const textsToSave: CommunicationTextContent[] = [];
            this.addTextToSave(textsToSave, 'subject', TextType.emailSubject);
            this.addTextToSave(textsToSave, 'body', TextType.emailBody);
            this.addTextToSave(textsToSave, 'copyright', TextType.emailCopyright);
            if (textsToSave.length > 0) {
                return this._voucherSrv.saveGiftCardTextContents(this._giftCardGroupId, textsToSave)
                    .pipe(
                        tap(() => {
                            this._ephemeralSrv.showSaveSuccess();
                            this._voucherSrv.loadGiftCardTextContents(this._giftCardGroupId);
                        })
                    );
            } else {
                return of();
            }
        } else {
            this.form.markAllAsTouched();
            this._languageTabs.goToInvalidCtrlTab();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanels);
            return throwError(() => 'invalid form');
        }
    }

    private refreshFormDataHandler(): void {
        combineLatest([
            this.languages$,
            this._voucherSrv.getGiftCardTextContents$()
        ])
            .pipe(
                takeUntil(this._onDestroy),
                filter(([languages, texts]) => !!languages && !!texts))
            .subscribe(([languages, texts]) => {
                languages.forEach(lang => {
                    this.applyOnTextFields(lang, texts,
                        (field, value) => {
                            if (value) field.setValue(value);
                        });
                });
            });
    }

    private formChangeHandler(): void {
        combineLatest([
            this.languages$,
            this._voucherSrv.getGiftCardTextContents$(),
            this.form.valueChanges
        ])
            .pipe(
                takeUntil(this._onDestroy),
                filter(([languages, texts]) => !!languages && !!texts))
            .subscribe(([languages, texts]) => {
                languages.forEach(lang => {
                    this.applyOnTextFields(lang, texts, (field, value) => FormControlHandler.checkAndRefreshDirtyState(field, value));
                });
            });
    }

    private applyOnTextFields(
        language: string,
        texts: CommunicationTextContent[],
        doOnField: (field: AbstractControl, value: string) => void
    ): void {
        this.applyOnTextField(`${language}.subject`, TextType.emailSubject, language, texts, doOnField);
        this.applyOnTextField(`${language}.body`, TextType.emailBody, language, texts, doOnField);
        this.applyOnTextField(`${language}.copyright`, TextType.emailCopyright, language, texts, doOnField);
    }

    private applyOnTextField(
        fieldName: string, type: TextType, language: string, texts: CommunicationTextContent[],
        doOnField: (field: AbstractControl, value: string) => void
    ): void {
        const field = this.form.get(fieldName);
        for (const text of texts) {
            if (text.language === language && text.type === type) {
                doOnField(field, text.value);
                return;
            }
        }
        doOnField(field, null);
    }

    private addTextToSave(textsToSave: CommunicationTextContent[], fieldName: string, textType: TextType): void {
        this.languages$.pipe(take(1)).subscribe(languages => {
            languages.forEach(language => {
                const field = this.form.get([language, fieldName]);
                if (field.dirty) {
                    textsToSave.push({
                        language,
                        type: textType,
                        value: field.value
                    });
                }
            });
        });
    }

}
