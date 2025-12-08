import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService, ChannelFieldsRestrictions, AdditionalCondition } from '@admin-clients/cpanel/channels/data-access';
import {
    DialogSize,
    EphemeralMessageService,
    TabsMenuComponent,
    RichTextAreaComponent,
    TabDirective
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { PrefixPipe } from '@admin-clients/shared/utility/pipes';
import {
    FormControlHandler, htmlMaxLengthValidator
} from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { Component, OnInit, ChangeDetectionStrategy, Inject, OnDestroy, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject, iif } from 'rxjs';
import { map, takeUntil, first, tap, shareReplay, switchMap } from 'rxjs/operators';
import { ChannelOperativeService } from '../../channel-operative.service';

@Component({
    selector: 'app-additional-conditions-dialog',
    templateUrl: './additional-conditions-dialog.component.html',
    styleUrls: ['./additional-conditions-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, ReactiveFormsModule, TranslatePipe, RichTextAreaComponent,
        CommonModule, FlexLayoutModule, PrefixPipe, FormControlErrorsComponent, TabsMenuComponent, TabDirective
    ]
})
export class AdditionalConditionsDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    @ViewChild('channelTextsTabs') private _channelTextsTabs: TabsMenuComponent;

    isEditCondition: boolean;
    form: UntypedFormGroup;
    languageList$: Observable<string[]>;
    isSaving$: Observable<boolean>;
    restrictions = ChannelFieldsRestrictions;

    constructor(
        private _dialogRef: MatDialogRef<AdditionalConditionsDialogComponent>,
        private _channelsService: ChannelsService,
        private _channelOperativeService: ChannelOperativeService,
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

        this.isSaving$ = this._channelOperativeService.isAdditionalConditionSaving$();

        this.languageList$ = this._channelsService.getChannel$()
            .pipe(
                first(channel => !!channel),
                map(channel => channel.languages.selected),
                tap(languages => {
                    const languagesControls = this._fb.group({});
                    languages.forEach(language => {
                        languagesControls.addControl(language, this._fb.control(this._data?.texts[language], [
                            Validators.required,
                            htmlMaxLengthValidator(this.restrictions.additionalConditionChannelTextLength)
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
            this._channelsService.getChannel$()
                .pipe(
                    first(channel => !!channel),
                    switchMap(channel => {
                        const formData = this.form.value;
                        const conditionData: AdditionalCondition = {
                            name: formData.name,
                            texts: formData.texts
                        };

                        return iif(() => !!this._data?.id,
                            this._channelOperativeService.updateAdditionalCondition(channel.id, this._data?.id, conditionData),
                            this._channelOperativeService.createAdditionalCondition(channel.id, conditionData)
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
