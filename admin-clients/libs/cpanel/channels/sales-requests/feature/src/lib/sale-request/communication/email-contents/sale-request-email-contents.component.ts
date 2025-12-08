import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelType } from '@admin-clients/cpanel/channels/data-access';
import {
    EmailContentTextsType, SaleRequestEmailContentTexts,
    SaleRequest, SalesRequestsService
} from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { MessageDialogService, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { BehaviorSubject, combineLatest, filter, map, Observable, of, Subject, take, takeUntil, tap, throwError } from 'rxjs';

@Component({
    selector: 'app-sale-request-email-contents',
    templateUrl: './sale-request-email-contents.component.html',
    styleUrls: ['./sale-request-email-contents.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SaleRequestEmailContentsComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _language = new BehaviorSubject<string>(null);
    private _salesRequestId: number;

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    form: UntypedFormGroup;
    isLoadingOrSaving$: Observable<boolean>;
    saleRequest$: Observable<SaleRequest>;
    languageList$: Observable<string[]>;
    selectedLanguage$ = this._language.asObservable();
    channelTypes = ChannelType;

    constructor(
        private _fb: UntypedFormBuilder,
        private _salesRequestsService: SalesRequestsService,
        private _messageDialogService: MessageDialogService,
        private _ephemeralMessageService: EphemeralMessageService
    ) { }

    ngOnInit(): void {
        this.isLoadingOrSaving$ = this._salesRequestsService.isEmailContentTextLoading$();

        this.form = this._fb.group({
            disclaimer: [null]
        });

        this._salesRequestsService.getSaleRequest$()
            .pipe(
                take(1),
                filter(saleRequest => !!saleRequest),
                tap(saleRequest => {
                    this._salesRequestId = saleRequest.id;
                    this._salesRequestsService.loadEmailContentText(this._salesRequestId);
                })
            ).subscribe();

        this.languageList$ = this._salesRequestsService.getSaleRequest$()
            .pipe(
                map(saleRequest => saleRequest.languages),
                tap(languages => {
                    if (!languages?.default && languages?.selected.length) {
                        languages.default = languages.selected[0];
                    }
                    this._language.next(languages?.default);
                }),
                map(languages => languages?.selected)
            );

        this.refreshFormDataHandler();
        this.formChangeHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    canChangeLanguage: (() => Observable<boolean>) = () => this.validateIfCanChangeLanguage();

    cancel(): void {
        this.loadContents();
    }

    save(): void {
        this.save$().subscribe(() => {
            this.form.markAsPristine();
        });
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const textsToSave: SaleRequestEmailContentTexts[] = [];
            const field = this.form.get('disclaimer');
            if (field.dirty) {
                textsToSave.push({
                    language: this._language.getValue(),
                    type: EmailContentTextsType.disclaimer,
                    value: field.value
                });
            }
            if (textsToSave.length > 0) {
                return this._salesRequestsService.saveEmailContentText(this._salesRequestId, textsToSave).pipe(
                    tap(() => {
                        this.loadContents();
                        this._ephemeralMessageService.showSaveSuccess();
                    }));
            }
            return throwError(() => 'nothing to save');
        } else {
            FormControlHandler.markAllControlsAsTouched(this.form);
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    changeLanguage(newLanguage: string): void {
        this._language.next(newLanguage);
    }

    private refreshFormDataHandler(): void {
        combineLatest([
            this.selectedLanguage$,
            this._salesRequestsService.getEmailContentText$()
        ])
            .pipe(
                takeUntil(this._onDestroy),
                filter(([language, texts]) => !!language && !!texts))
            .subscribe(([language, texts]) => {
                const field = this.form.get('disclaimer');
                for (const text of texts) {
                    if (text.language === language) {
                        field.setValue(text.value);
                        return;
                    }
                }
                field.setValue(null);
            });
    }

    private formChangeHandler(): void {
        combineLatest([
            this.selectedLanguage$,
            this._salesRequestsService.getEmailContentText$(),
            this.form.valueChanges
        ])
            .pipe(
                takeUntil(this._onDestroy),
                filter(([language, texts]) => !!language && !!texts))
            .subscribe(([language, texts]) => {
                const field = this.form.get('disclaimer');
                for (const text of texts) {
                    if (text.language === language) {
                        FormControlHandler.checkAndRefreshDirtyState(field, text.value);
                        return;
                    }
                }
                FormControlHandler.checkAndRefreshDirtyState(field, null);
            });
    }

    private validateIfCanChangeLanguage(): Observable<boolean> {
        if (this.form.dirty) {
            return this._messageDialogService.defaultUnsavedChangesWarn();
        }
        return of(true);
    }

    private loadContents(): void {
        this._salesRequestsService.loadEmailContentText(this._salesRequestId);
        this.form.markAsPristine();
    }
}
