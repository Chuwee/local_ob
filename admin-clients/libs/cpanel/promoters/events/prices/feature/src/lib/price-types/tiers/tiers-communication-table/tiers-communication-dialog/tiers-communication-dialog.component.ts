import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    AdvancedTiersTransTableOptionsDialogRestrictions, EventTiersService, EventTiersChannelContent, EventTierChannelContentType
} from '@admin-clients/cpanel-promoters-events-prices-data-access';
import { DialogSize, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import {
    FormControlHandler, htmlMaxLengthValidator
} from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subject, Observable, combineLatest } from 'rxjs';
import { map, filter, tap, takeUntil, first } from 'rxjs/operators';

@Component({
    selector: 'app-tiers-communication-dialog',
    templateUrl: './tiers-communication-dialog.component.html',
    styleUrls: ['./tiers-communication-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class TiersCommunicationDialogComponent implements OnInit, OnDestroy {

    private _onDestroy = new Subject<void>();
    private _tierId: string;
    private _eventId: string;

    form: UntypedFormGroup;
    priceZoneName: string;
    tierName: string;
    languageList$: Observable<string[]>;
    isLoadingOrSaving$: Observable<boolean>;
    restrictions = AdvancedTiersTransTableOptionsDialogRestrictions;

    constructor(
        private _dialogRef: MatDialogRef<TiersCommunicationDialogComponent>,
        private _eventTiersService: EventTiersService,
        private _eventsService: EventsService,
        private _ephemeralMessageService: EphemeralMessageService,
        private _fb: UntypedFormBuilder,
        @Inject(MAT_DIALOG_DATA) private _data: { tierId: string; eventId: string; priceTypeTitle: string; tierName: string }
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
        this._tierId = this._data.tierId;
        this._eventId = this._data.eventId;
        this.priceZoneName = this._data.priceTypeTitle;
        this.tierName = this._data.tierName;
    }

    ngOnInit(): void {
        this.isLoadingOrSaving$ = this._eventTiersService.isTierChannelContentInProgress$();

        this.initForms();

        this._eventTiersService.loadTierChannelContent(this._eventId, this._tierId);
        this.languageList$ = this._eventsService.event.get$()
            .pipe(
                first(event => !!event),
                map(event => event.settings.languages.selected),
                tap(languages => {
                    const languagesControls = this._fb.group({});
                    languages.forEach(language => {
                        languagesControls.addControl(language, this._fb.group({
                            name: [null, [Validators.maxLength(this.restrictions.nameLength)]],
                            description: [null, [htmlMaxLengthValidator(this.restrictions.descriptionLength)]]
                        }));
                    });

                    this.form.setControl('contents', languagesControls);
                    this.form.get('contents').markAsPristine();

                    this.refreshFormDataHandler();
                    this.formChangeHandler();
                })
            );
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    close(): void {
        this._dialogRef.close();
    }

    save(): void {
        if (this.form.valid) {
            const textsToSave: EventTiersChannelContent[] = [];
            this.addTextToSave(textsToSave, 'name', EventTierChannelContentType.name);
            this.addTextToSave(textsToSave, 'description', EventTierChannelContentType.description);
            this._eventTiersService.updateTierChannelContent(this._eventId, this._tierId, textsToSave)
                .subscribe(() => {
                    this._ephemeralMessageService.showSaveSuccess();
                    this._dialogRef.close();
                });
            this.form.markAsPristine();
        } else {
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
        }
    }

    private initForms(): void {
        // FormGroup creation
        this.form = this._fb.group({
            contents: this._fb.group({})
        });
    }

    private refreshFormDataHandler(): void {
        this._eventTiersService.getTierChannelContent$()
            .pipe(
                filter(texts => !!texts),
                takeUntil(this._onDestroy))
            .subscribe(texts => {
                texts.forEach(text => {
                    const field = this.form.get(['contents', text.language, text.type.toLowerCase()]);
                    field.patchValue(text.value);
                });
            });
    }

    private formChangeHandler(): void {
        combineLatest([
            this._eventTiersService.getTierChannelContent$(),
            this.form.valueChanges
        ])
            .pipe(
                filter(([texts]) => !!texts),
                takeUntil(this._onDestroy))
            .subscribe(([texts]) => {
                texts.forEach(text => {
                    const field = this.form.get(['contents', text.language, text.type.toLowerCase()]);
                    FormControlHandler.checkAndRefreshDirtyState(field, text.value);
                });
            });
    }

    private addTextToSave(
        textsToSave: EventTiersChannelContent[],
        fieldName: string,
        textType: EventTierChannelContentType): void {
        Object.entries(this.form.get('contents').value).forEach(([language, value]) => {
            if (value[fieldName]) {
                textsToSave.push({
                    language,
                    type: textType,
                    value: value[fieldName]
                });
            }
        });
    }

}
