import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelFieldsRestrictions, AdditionalCondition } from '@admin-clients/cpanel/channels/data-access';
import { SalesRequestsService } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { DialogSize, EphemeralMessageService, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { FormControlHandler, htmlContentMaxLengthValidator } from '@admin-clients/shared/utility/utils';
import { Component, OnInit, ChangeDetectionStrategy, Inject, OnDestroy, ViewChild } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { iif, Observable, Subject } from 'rxjs';
import { takeUntil, first, switchMap, map, tap, shareReplay } from 'rxjs/operators';

@Component({
    selector: 'app-sale-request-additional-conditions-dialog',
    templateUrl: './sale-request-additional-conditions-dialog.component.html',
    styleUrls: ['./sale-request-additional-conditions-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SaleRequestAdditionalConditionsDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    @ViewChild('channelTextsTabs') private _channelTextsTabs: TabsMenuComponent;

    isEditCondition: boolean;
    form: UntypedFormGroup;
    languageList$: Observable<string[]>;
    isSaving$: Observable<boolean>;
    restrictions = ChannelFieldsRestrictions;

    constructor(
        private _dialogRef: MatDialogRef<SaleRequestAdditionalConditionsDialogComponent>,
        private _salesRequestsService: SalesRequestsService,
        private _ephemeralMessageService: EphemeralMessageService,
        private _fb: UntypedFormBuilder,
        @Inject(MAT_DIALOG_DATA) private _data?: AdditionalCondition
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.isEditCondition = !!this._data;
        // init form
        this.form = this._fb.group({
            name: [this._data?.name, [
                Validators.required,
                Validators.maxLength(this.restrictions.additionalConditionNameLength)
            ]],
            texts: this._fb.group({})
        });

        this.isSaving$ = this._salesRequestsService.isAdditionalConditionSaving$();

        this.languageList$ = this._salesRequestsService.getSaleRequest$()
            .pipe(
                first(saleReq => !!saleReq),
                map(saleReq => saleReq.languages.selected),
                tap(languages => {
                    const languagesControls = this._fb.group({});
                    languages.forEach(language => {
                        languagesControls.addControl(language, this._fb.control(this._data?.texts[language], [
                            Validators.required,
                            htmlContentMaxLengthValidator(this.restrictions.additionalConditionChannelTextLength)
                        ]
                        ));
                    });

                    this.form.setControl('texts', languagesControls);
                    this.form.get('texts').markAsPristine();

                    this.formChangeHandler(languages);
                }),
                shareReplay(1)
            );
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    close(isSaved = false): void {
        this._dialogRef.close(isSaved);
    }

    save(): void {
        if (this.form.valid) {
            this._salesRequestsService.getSaleRequest$()
                .pipe(
                    first(salesReq => !!salesReq),
                    switchMap(salesReq => {
                        const formData = this.form.value;
                        const conditionData: AdditionalCondition = {
                            name: formData.name,
                            texts: formData.texts
                        };

                        return iif(() => !!this._data?.id,
                            this._salesRequestsService.updateAdditionalCondition(salesReq.id, this._data?.id, conditionData),
                            this._salesRequestsService.createAdditionalCondition(salesReq.id, conditionData)
                        );
                    })
                )
                .subscribe(() => {
                    this._ephemeralMessageService.showSaveSuccess();
                    this.close(true);
                });
        } else {
            this.form.markAllAsTouched();
            this._channelTextsTabs.goToInvalidCtrlTab();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
        }
    }

    private formChangeHandler(languages: string[]): void {
        this.form.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(() => {
                FormControlHandler.checkAndRefreshDirtyState(this.form.get('name'), this._data?.name);
                languages.forEach(lang => {
                    const field = this.form.get(['texts', lang]);
                    FormControlHandler.checkAndRefreshDirtyState(field, this._data?.texts[lang]);
                });
            });
    }

}
