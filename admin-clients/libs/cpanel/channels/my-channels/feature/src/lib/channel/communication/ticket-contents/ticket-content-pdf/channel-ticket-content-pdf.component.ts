import {
    ChannelTicketContentImage, ChannelTicketContentImageField, channelTicketImageRestrictions, PutChannelTicketContentImage,
    ChannelTicketContentImageType
} from '@admin-clients/cpanel/channels/communication/data-access';
import { ChannelsService, ChannelsExtendedService } from '@admin-clients/cpanel/channels/data-access';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { AfterViewInit, ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, map, take, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-channel-ticket-content-pdf',
    templateUrl: './channel-ticket-content-pdf.component.html',
    styleUrls: ['./channel-ticket-content-pdf.component.scss', '../channel-ticket-contents.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelTicketContentPdfComponent implements OnInit, AfterViewInit, OnDestroy {
    private _onDestroy = new Subject<void>();

    private _channelId: number;
    private _images$: Observable<ChannelTicketContentImage[]>;
    private _imageFields: ChannelTicketContentImageField[];
    private _selectedLanguage: string;

    isPdfContentExpanded: boolean;
    imageRestrictions = channelTicketImageRestrictions;
    pdfContentForm: UntypedFormGroup;

    @Input() form: UntypedFormGroup;
    @Input() language$: Observable<string>;

    constructor(
        private _fb: UntypedFormBuilder,
        private _channelsService: ChannelsService,
        private _channelsExtSrv: ChannelsExtendedService
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
    }

    cancel(): void {
        this.pdfContentForm.markAsPristine();
        this._channelsExtSrv.loadTicketPdfContentImages(this._channelId); // filter language si o no?
    }

    save(getImageFields: (contentForm: UntypedFormGroup, imageFields: ChannelTicketContentImageField[], language: string) =>
        { [key: string]: PutChannelTicketContentImage[] }
    ): Observable<void>[] {
        const obsToSave$: Observable<void>[] = [];
        const { imagesToSave, imagesToDelete } = getImageFields(this.pdfContentForm, this._imageFields, this._selectedLanguage);
        if (imagesToSave.length > 0) {
            obsToSave$.push(
                this._channelsExtSrv.saveTicketPdfContentImages(this._channelId, imagesToSave)
            );
        }

        if (imagesToDelete.length > 0) {
            obsToSave$.push(
                this._channelsExtSrv.deleteTicketPdfContentImages(this._channelId, imagesToDelete)
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
        this._imageFields = [{
            formField: 'header',
            type: ChannelTicketContentImageType.header,
            maxSize: channelTicketImageRestrictions['pdfHeader'].size
        }, {
            formField: 'banner',
            type: ChannelTicketContentImageType.banner,
            maxSize: channelTicketImageRestrictions['pdfBanner'].size
        }];
    }

    private initForms(): void {
        const pdfFields: Record<string, any> = {};

        this._imageFields.forEach(imageField => {
            pdfFields[imageField.formField] = null;
        });

        this.pdfContentForm = this._fb.group(pdfFields);
        this.form.addControl('pdfContent', this.pdfContentForm);
    }

    private loadContents(): void {
        // load texts and images
        this._channelsService.getChannel$()
            .pipe(
                take(1),
                tap(channel => {
                    this._channelId = channel.id;
                    this._channelsExtSrv.loadTicketPdfContentImages(this._channelId); // filter language si o no?
                })
            ).subscribe();

        this._images$ = combineLatest([
            this._channelsExtSrv.getTicketPdfContentImages$(),
            this.language$
        ]).pipe(
            filter(([pdfImages, language]) => !!pdfImages && !!language),
            map(([pdfImages, language]) => pdfImages.filter(img => img.language === language)),
            tap(pdfImages => {
                this._imageFields.forEach(imageField => {
                    const field = this.pdfContentForm.get(imageField.formField);
                    field.reset();
                    for (const image of pdfImages) {
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
            this._images$,
            this.form.valueChanges
        ]).pipe(
            filter(([language, images]) => !!language && !!images),
            takeUntil(this._onDestroy)
        ).subscribe(([_, images]) => {
            this._imageFields.forEach(imageField => {
                const field = this.pdfContentForm.get(imageField.formField);
                const originalValue = images.filter(img => img.type === imageField.type)[0]?.image_url || null;
                FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
            });
        });
    }
}
