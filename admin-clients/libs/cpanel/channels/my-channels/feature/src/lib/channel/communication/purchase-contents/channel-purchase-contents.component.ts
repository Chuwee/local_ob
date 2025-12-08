import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    ChannelPurchaseContentTextField, ChannelPurchaseContentImageField, ChannelPurchaseContentImage,
    ChannelPurchaseContentText, channelPurchaseImageRestrictions, PutChannelPurchaseContentImage,
    ChannelPurchaseContentImageType, ChannelPurchaseContentTextType
} from '@admin-clients/cpanel/channels/communication/data-access';
import { Channel, ChannelsService, ChannelsExtendedService } from '@admin-clients/cpanel/channels/data-access';
import { MessageDialogService, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormControlHandler, urlValidator } from '@admin-clients/shared/utility/utils';
import { AfterViewInit, ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { BehaviorSubject, combineLatest, forkJoin, Observable, of, Subject, throwError } from 'rxjs';
import { filter, first, map, shareReplay, take, takeUntil, tap } from 'rxjs/operators';
import { ChannelCommunicationNotifierService } from '../container/channel-communication-notifier.service';

@Component({
    selector: 'app-channel-purchase-contents',
    templateUrl: './channel-purchase-contents.component.html',
    styleUrls: ['./channel-purchase-contents.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelPurchaseContentsComponent implements OnInit, AfterViewInit, OnDestroy, WritingComponent {
    private _onDestroy = new Subject<void>();
    private _selectedLanguage = new BehaviorSubject<string>(null);
    private _channelId: number;
    private _textFields: ChannelPurchaseContentTextField[];
    private _imageFields: ChannelPurchaseContentImageField[];
    private _images$: Observable<ChannelPurchaseContentImage[]>;
    private _texts$: Observable<ChannelPurchaseContentText[]>;

    form: UntypedFormGroup;
    isLoadingOrSaving$: Observable<boolean>;
    selectedLanguage$ = this._selectedLanguage.asObservable();
    channel$: Observable<Channel>;
    languages$: Observable<string[]>;
    imageRestrictions = channelPurchaseImageRestrictions;

    constructor(
        private _fb: UntypedFormBuilder,
        private _ephemeralMessageService: EphemeralMessageService,
        private _channelsService: ChannelsService,
        private _channelsExtSrv: ChannelsExtendedService,
        private _messageDialogService: MessageDialogService,
        private _communicationNotifierService: ChannelCommunicationNotifierService
    ) { }

    ngOnInit(): void {
        this.prepareFields();
        this.initForms();
        this.loadContents();

        this._communicationNotifierService.getRefreshDataSignal$()
            .pipe(takeUntil(this._onDestroy))
            .subscribe(() => this.cancel());
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    ngAfterViewInit(): void {
        this.refreshFormDataHandler();
    }

    canChangeLanguage: (() => Observable<boolean>) = () => this.validateIfCanChangeLanguage();

    cancel(): void {
        this._channelsExtSrv.loadPurchaseContentImages(this._channelId);
        this._channelsExtSrv.loadPurchaseContentTexts(this._channelId);
    }

    getImageFields(
        contentForm: UntypedFormGroup, imageFields: ChannelPurchaseContentImageField[], language: string
    ): { [key: string]: PutChannelPurchaseContentImage[] } {
        const imagesToSave: PutChannelPurchaseContentImage[] = [];
        const imagesToDelete: PutChannelPurchaseContentImage[] = [];
        imageFields.forEach(imageField => {
            const field = contentForm.get(imageField.formField);
            if (field.dirty) {
                const imageValue = field.value;
                if (imageValue?.data) {
                    imagesToSave.push({ type: imageField.type, image: imageValue?.data, language });
                } else {
                    imagesToDelete.push({ type: imageField.type, image: null, language });
                }
            }
        });
        return { imagesToSave, imagesToDelete };
    }

    getTextFields(
        contentForm: UntypedFormGroup, textFields: ChannelPurchaseContentTextField[], language: string
    ): ChannelPurchaseContentText[] {
        const textsToSave: ChannelPurchaseContentText[] = [];
        textFields.forEach(textField => {
            const field = contentForm.get(textField.formField);
            if (field.dirty) {
                textsToSave.push({ type: textField.type, redirect_url: field.value, language });
            }
        });
        return textsToSave;
    }

    save$(): Observable<void[]> {
        if (this.form.valid) {
            const obs$: Observable<void>[] = [];
            const { imagesToSave, imagesToDelete } = this.getImageFields(
                this.form,
                this._imageFields,
                this._selectedLanguage.value
            );
            if (imagesToSave.length > 0) {
                obs$.push(this._channelsExtSrv.savePurchaseContentImages(this._channelId, imagesToSave));
            }
            if (imagesToDelete.length > 0) {
                obs$.push(this._channelsExtSrv.deletePurchaseContentImages(this._channelId, imagesToDelete));
            }

            const textsToSave = this.getTextFields(this.form, this._textFields, this._selectedLanguage.value);
            if (textsToSave.length > 0) {
                obs$.push(
                    this._channelsExtSrv.savePurchaseContentTexts(this._channelId, textsToSave)
                );
            }

            if (obs$.length) {
                obs$[obs$.length - 1] = obs$[obs$.length - 1].pipe(
                    tap(() => this.cancel()) // reloads data from backend
                );
            }

            return forkJoin(obs$)
                .pipe(tap(() => {
                    this._ephemeralMessageService.showSaveSuccess();
                }));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe(() => {
            this._channelsExtSrv.loadPurchaseContentImages(this._channelId);
            this._channelsExtSrv.loadPurchaseContentTexts(this._channelId);
        });
    }

    changeLanguage(newLanguage: string): void {
        this._selectedLanguage.next(newLanguage);
    }

    private prepareFields(): void {
        this._imageFields = [{
            formField: 'headerBanner',
            type: ChannelPurchaseContentImageType.headerBanner,
            maxSize: channelPurchaseImageRestrictions['headerBanner'].size
        }, {
            formField: 'banner',
            type: ChannelPurchaseContentImageType.banner,
            maxSize: channelPurchaseImageRestrictions['banner'].size
        }];
        this._textFields = [{
            formField: 'headerBannerUrl',
            type: ChannelPurchaseContentTextType.headerBanner
        }, {
            formField: 'bannerUrl',
            type: ChannelPurchaseContentTextType.banner
        }];
    }

    private initForms(): void {
        const purchaseFields: Record<string, any> = {};
        this._imageFields.forEach(imageField => {
            purchaseFields[imageField.formField] = null;
        });
        this._textFields.forEach(textField => {
            purchaseFields[textField.formField] = [null, urlValidator()];
        });
        this.form = this._fb.group(purchaseFields);
    }

    private loadContents(): void {
        // load texts and images
        this._channelsService.getChannel$()
            .pipe(
                take(1),
                tap(channel => {
                    this._channelId = channel.id;
                    this._channelsExtSrv.loadPurchaseContentImages(this._channelId);
                    this._channelsExtSrv.loadPurchaseContentTexts(this._channelId);
                })
            ).subscribe();

        this._images$ = combineLatest([
            this._channelsExtSrv.getPurchaseContentImages$(),
            this.selectedLanguage$
        ]).pipe(
            filter(([purchaseImgs, language]) => !!purchaseImgs && !!language),
            map(([purchaseImgs, language]) => purchaseImgs.filter(img => img.language === language)),
            tap(purchaseImgs => {
                this._imageFields.forEach(imageField => {
                    const field = this.form.get(imageField.formField);
                    field.reset();
                    for (const image of purchaseImgs) {
                        if (image.type === imageField.type) {
                            field.setValue(image.image_url);
                            return;
                        }
                    }
                });
                this.form.markAsPristine();
            })
        );

        this._texts$ = combineLatest([
            this._channelsExtSrv.getPurchaseContentTexts$(),
            this.selectedLanguage$
        ]).pipe(
            filter(([purchaseTexts, language]) => !!purchaseTexts && !!language),
            map(([purchaseTexts, language]) => purchaseTexts.filter(text => text.language === language)),
            tap(purchaseTexts => {
                this._textFields.forEach(textField => {
                    const field = this.form.get(textField.formField);
                    field.reset();
                    for (const text of purchaseTexts) {
                        if (text.type === textField.type) {
                            field.setValue(text.redirect_url);
                            return;
                        }
                    }
                });
                this.form.markAsPristine();
            })
        );

        this.channel$ = this._channelsService.getChannel$()
            .pipe(
                first(channel => !!channel),
                shareReplay(1)
            );

        this.languages$ = this._channelsService.getChannel$()
            .pipe(
                first(channel => !!channel),
                map(channel => channel.languages.selected),
                filter(languages => !!languages),
                tap(languages => {
                    this._selectedLanguage.next(languages[0]);
                }),
                shareReplay(1)
            );

        this.isLoadingOrSaving$ = combineLatest([
            this._channelsExtSrv.isTicketPdfContentImagesLoading$(),
            this._channelsExtSrv.isTicketPdfContentImagesSaving$(),
            this._channelsExtSrv.isTicketPdfContentImagesRemoving$(),
            this._channelsExtSrv.isTicketPrinterContentImagesLoading$(),
            this._channelsExtSrv.isTicketPrinterContentImagesSaving$(),
            this._channelsExtSrv.isTicketPrinterContentImagesRemoving$(),
            this._channelsExtSrv.isTicketPassbookContentTextsLoading$(),
            this._channelsExtSrv.isTicketPassbookContentTextsSaving$(),
            this._channelsExtSrv.isPurchaseContentTextsLoading$()
        ]).pipe(map(loadings => loadings.some(isLoading => isLoading)));
    }

    private refreshFormDataHandler(): void {
        combineLatest([
            this.selectedLanguage$,
            this._images$,
            this._texts$,
            this.form.valueChanges
        ]).pipe(
            filter(data => data.every(item => !!item)),
            takeUntil(this._onDestroy)
        ).subscribe(([_, images, texts]) => {
            this._imageFields.forEach(imageField => {
                const field = this.form.get(imageField.formField);
                const originalValue = images.filter(img => img.type === imageField.type)[0]?.image_url || null;
                FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
            });
            this._textFields.forEach(textField => {
                const field = this.form.get(textField.formField);
                const originalValue = texts.filter(text => text.type === textField.type)[0]?.redirect_url || null;
                FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
            });
        });
    }

    private validateIfCanChangeLanguage(): Observable<boolean> {
        if (this.form.dirty) {
            return this._messageDialogService.defaultUnsavedChangesWarn();
        }
        return of(true);
    }
}
