import {
    ChannelTicketContentImage, ChannelTicketContentText, ChannelTicketContentTextField, ChannelTicketContentImageField,
    ChannelContentFieldsRestrictions, channelTicketImageRestrictions, PutChannelTicketContentImage, ChannelTicketContentTextType,
    ChannelTicketContentImageType
} from '@admin-clients/cpanel/channels/communication/data-access';
import { ChannelsService, ChannelsExtendedService } from '@admin-clients/cpanel/channels/data-access';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, map, take, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-channel-ticket-content-passbook',
    templateUrl: './channel-ticket-content-passbook.component.html',
    styleUrls: ['./channel-ticket-content-passbook.component.scss', '../channel-ticket-contents.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelTicketContentPassbookComponent implements OnInit, OnDestroy, AfterViewInit {
    private _onDestroy = new Subject<void>();

    private _channelId: number;
    private _images$: Observable<ChannelTicketContentImage[]>;
    private _texts$: Observable<ChannelTicketContentText[]>;
    private _textFields: ChannelTicketContentTextField[];
    private _imageFields: ChannelTicketContentImageField[];
    private _selectedLanguage: string;

    isPassbookContentExpanded: boolean;
    textRestrictions = ChannelContentFieldsRestrictions;
    imageRestrictions = channelTicketImageRestrictions;
    passbookContentForm: UntypedFormGroup;

    @Input() form: UntypedFormGroup;
    @Input() language$: Observable<string>;
    @Input() isEnabled: boolean;

    constructor(
        private _fb: UntypedFormBuilder,
        private _channelsService: ChannelsService,
        private _channelsExtSrv: ChannelsExtendedService,
        private _ref: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.prepareFields();
        this.initForms();
        this.loadContents();
        this.language$.pipe(takeUntil(this._onDestroy)).subscribe(lang => this._selectedLanguage = lang);
    }

    ngAfterViewInit(): void {
        this.refreshFormDataHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._channelsExtSrv.clearTicketPassbookContentTexts();
        this._channelsExtSrv.clearTicketPassbookContentImages();
    }

    cancel(): void {
        this.passbookContentForm.markAsPristine();
        this._channelsExtSrv.loadTicketPassbookContentTexts(this._channelId);
        this._channelsExtSrv.loadTicketPassbookContentImages(this._channelId);
    }

    save(getTextFields: (contentForm: UntypedFormGroup, textFields: ChannelTicketContentTextField[], language: string) =>
        ChannelTicketContentText[],
        getImageFields: (contentForm: UntypedFormGroup, imageFields: ChannelTicketContentImageField[], language: string) =>
            { [key: string]: PutChannelTicketContentImage[] }
    ): Observable<void | void[]>[] {
        const obsToSave$: Observable<void>[] = [];

        const textsToSave = getTextFields(this.passbookContentForm, this._textFields, this._selectedLanguage);
        if (textsToSave.length > 0) {
            obsToSave$.push(
                this._channelsExtSrv.saveTicketPassbookContentTexts(this._channelId, textsToSave)
            );
        }

        const { imagesToSave, imagesToDelete } = getImageFields(this.passbookContentForm, this._imageFields, this._selectedLanguage);
        if (imagesToSave.length > 0) {
            obsToSave$.push(
                this._channelsExtSrv.saveTicketPassbookContentImages(this._channelId, imagesToSave)
            );
        }

        if (imagesToDelete.length > 0) {
            obsToSave$.push(
                this._channelsExtSrv.deleteTicketPassbookContentImages(this._channelId, imagesToDelete)
            );
        }

        if (obsToSave$.length) {
            obsToSave$[obsToSave$.length - 1] = obsToSave$[obsToSave$.length - 1].pipe(
                tap(() => this.cancel()) // reloads data from backend
            );
        }
        return obsToSave$;
    }

    private prepareFields(): void {
        this._textFields = [{
            formField: 'textColor',
            type: ChannelTicketContentTextType.textColor,
            maxLength: ChannelContentFieldsRestrictions.colorPassbookLength
        }, {
            formField: 'labelColor',
            type: ChannelTicketContentTextType.labelColor,
            maxLength: ChannelContentFieldsRestrictions.colorPassbookLength
        }, {
            formField: 'backgroundColor',
            type: ChannelTicketContentTextType.backgroundColor,
            maxLength: ChannelContentFieldsRestrictions.colorPassbookLength
        }];
        this._imageFields = [{
            formField: 'channelImage',
            type: ChannelTicketContentImageType.channelImage,
            maxSize: channelTicketImageRestrictions['passbookChannelImage'].size
        }];
    }

    private initForms(): void {
        const passbookFields: Record<string, any> = {};

        this._textFields.forEach(textField => {
            passbookFields[textField.formField] = [{ value: null, disabled: !this.isEnabled }, [Validators.maxLength(textField.maxLength)]];
        });
        this._imageFields.forEach(imageField => {
            passbookFields[imageField.formField] = [{ value: null, disabled: !this.isEnabled }];
        });

        this.passbookContentForm = this._fb.group(passbookFields);
        this.form.addControl('passbookContent', this.passbookContentForm);
    }

    private loadContents(): void {
        // Load texts and images only if passbook is enabled
        if (this.isEnabled) {
            this._channelsService.getChannel$()
                .pipe(take(1))
                .subscribe(channel => {
                    this._channelId = channel.id;
                    this._channelsExtSrv.loadTicketPassbookContentTexts(this._channelId);
                    this._channelsExtSrv.loadTicketPassbookContentImages(this._channelId);
                });
        }

        this._texts$ = combineLatest([
            this._channelsExtSrv.getTicketPassbookContentTexts$(),
            this.language$
        ]).pipe(
            filter(([passbookTexts, language]) => !!passbookTexts && !!language),
            map(([passbookTexts, language]) => passbookTexts.filter(text => text.language === language)),
            tap(passbookTexts => {
                this._textFields.forEach(textField => {
                    const field = this.passbookContentForm.get(textField.formField);
                    field.reset();
                    for (const text of passbookTexts) {
                        if (text.value && text.type === textField.type) {
                            field.setValue('#' + text.value);
                            return;
                        }
                    }
                });
                this._ref.detectChanges();
            })
        );

        this._images$ = combineLatest([
            this._channelsExtSrv.getTicketPassbookContentImages$(),
            this.language$
        ]).pipe(
            filter(([passbookImages, language]) => !!passbookImages && !!language),
            map(([passbookImages, language]) => passbookImages.filter(img => img.language === language)),
            tap(passbookImages => {
                this._imageFields.forEach(imageField => {
                    const field = this.passbookContentForm.get(imageField.formField);
                    field.reset();
                    for (const image of passbookImages) {
                        if (image.type === imageField.type) {
                            field.setValue(image.image_url);
                            return;
                        }
                    }
                });
            })
        );
    }

    private refreshFormDataHandler(): void {
        combineLatest([
            this.language$,
            this._texts$,
            this._images$,
            this.form.valueChanges
        ]).pipe(
            filter(data => data.every(item => !!item)),
            takeUntil(this._onDestroy)
        ).subscribe(([, texts, images]) => {
            this._textFields.forEach(textField => {
                const field = this.passbookContentForm.get(textField.formField);
                const originalValue = texts.filter(text => text.type === textField.type)[0]?.value || null;
                FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
            });
            this._imageFields.forEach(imageField => {
                const field = this.passbookContentForm.get(imageField.formField);
                const originalValue = images.filter(img => img.type === imageField.type)[0]?.image_url || null;
                FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
            });
        });
    }
}
