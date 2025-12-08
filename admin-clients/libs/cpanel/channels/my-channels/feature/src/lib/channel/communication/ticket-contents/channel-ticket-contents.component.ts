import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    ChannelTicketContentImageField, PutChannelTicketContentImage,
    ChannelTicketContentTextField, ChannelTicketContentText
} from '@admin-clients/cpanel/channels/communication/data-access';
import {
    ChannelType, channelWebTypes, Channel,
    ChannelsExtendedService, ChannelsService
} from '@admin-clients/cpanel/channels/data-access';
import { MessageDialogService, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, QueryList, ViewChild, ViewChildren } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { BehaviorSubject, combineLatest, forkJoin, Observable, of, Subject, throwError } from 'rxjs';
import { filter, first, map, shareReplay, takeUntil, tap } from 'rxjs/operators';
import { ChannelOperativeService } from '../../operative/channel-operative.service';
import { ChannelCommunicationNotifierService } from '../container/channel-communication-notifier.service';
import { ChannelTicketContentPassbookComponent } from './ticket-content-passbook/channel-ticket-content-passbook.component';
import { ChannelTicketContentPdfComponent } from './ticket-content-pdf/channel-ticket-content-pdf.component';
import { ChannelTicketContentPrinterComponent } from './ticket-content-printer/channel-ticket-content-printer.component';

@Component({
    selector: 'app-channel-ticket-contents',
    templateUrl: './channel-ticket-contents.component.html',
    styleUrls: ['./channel-ticket-contents.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelTicketContentsComponent implements OnInit, OnDestroy, WritingComponent {
    private _onDestroy = new Subject<void>();
    private _selectedLanguage = new BehaviorSubject<string>(null);
    private _isPassbookEnabled: boolean;

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    @ViewChild('pdfContent')
    private _pdfContentComponent: ChannelTicketContentPdfComponent;

    @ViewChild('passbookContent')
    private _passbookContentComponent: ChannelTicketContentPassbookComponent;

    @ViewChild('printerContent')
    private _printerContentComponent: ChannelTicketContentPrinterComponent;

    form: UntypedFormGroup;
    isLoadingOrSaving$: Observable<boolean>;
    selectedLanguage$ = this._selectedLanguage.asObservable();
    channel$: Observable<Channel>;
    languages$: Observable<string[]>;
    passbook$: Observable<{
        visible: boolean;
        enabled: boolean;
    }>;

    constructor(
        private _fb: UntypedFormBuilder,
        private _ephemeralMessageService: EphemeralMessageService,
        private _channelsService: ChannelsService,
        private _channelsExtSrv: ChannelsExtendedService,
        private _channelOperativeService: ChannelOperativeService,
        private _messageDialogService: MessageDialogService,
        private _communicationNotifierService: ChannelCommunicationNotifierService
    ) { }

    ngOnInit(): void {
        // base form
        this.form = this._fb.group({});

        this.passbook$ = combineLatest([
            this._channelsService.getChannel$()
                .pipe(
                    first(channel => !!channel),
                    tap(channel => this._channelOperativeService.loadChannelDeliveryMethods(String(channel.id)))
                ),
            this._channelOperativeService.getChannelDeliveryMethods$()
                .pipe(first(dm => !!dm))
        ])
            .pipe(
                map(([channel, dm]) => ({
                    visible: channelWebTypes.includes(channel.type) || channel.type === ChannelType.members,
                    enabled: dm.receipt_ticket_display?.passbook
                })),
                tap(passbook => this._isPassbookEnabled = passbook.enabled),
                shareReplay(1)
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

        this.isLoadingOrSaving$ = booleanOrMerge([
            this._channelsExtSrv.isTicketPdfContentImagesLoading$(),
            this._channelsExtSrv.isTicketPdfContentImagesSaving$(),
            this._channelsExtSrv.isTicketPdfContentImagesRemoving$(),
            this._channelsExtSrv.isTicketPrinterContentImagesLoading$(),
            this._channelsExtSrv.isTicketPrinterContentImagesSaving$(),
            this._channelsExtSrv.isTicketPrinterContentImagesRemoving$(),
            this._channelsExtSrv.isTicketPassbookContentTextsLoading$(),
            this._channelsExtSrv.isTicketPassbookContentTextsSaving$()
        ]);

        this._communicationNotifierService.getRefreshDataSignal$()
            .pipe(takeUntil(this._onDestroy))
            .subscribe(() => this.cancel());
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._channelOperativeService.clearChannelDeliveryMethods();
    }

    canChangeLanguage: (() => Observable<boolean>) = () => this.validateIfCanChangeLanguage();

    cancel(): void {
        this._pdfContentComponent.cancel();
        if (this._passbookContentComponent && this._isPassbookEnabled) {
            this._passbookContentComponent.cancel();
        }
        if (this._printerContentComponent) {
            this._printerContentComponent.cancel();
        }
    }

    getImageFields(
        contentForm: UntypedFormGroup, imageFields: ChannelTicketContentImageField[], language: string
    ): { [key: string]: PutChannelTicketContentImage[] } {
        const imagesToSave: PutChannelTicketContentImage[] = [];
        const imagesToDelete: PutChannelTicketContentImage[] = [];
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

    getTextFields(contentForm: UntypedFormGroup, textFields: ChannelTicketContentTextField[], language: string): ChannelTicketContentText[] {
        const textsToSave: ChannelTicketContentText[] = [];
        textFields.forEach(textField => {
            const field = contentForm.get(textField.formField);
            if (field.dirty) {
                textsToSave.push({ type: textField.type, value: field.value, language });
            }
        });
        return textsToSave;
    }

    save$(): Observable<void[]> {
        if (this.form.valid) {
            const obs$ = [];
            if (this._pdfContentComponent) {
                obs$.push(...this._pdfContentComponent.save(this.getImageFields.bind(this)));
            }
            if (this._passbookContentComponent && this._isPassbookEnabled) {
                obs$.push(...this._passbookContentComponent.save(this.getTextFields.bind(this), this.getImageFields.bind(this)));
            }
            if (this._printerContentComponent) {
                obs$.push(...this._printerContentComponent.save(this.getImageFields.bind(this)));
            }
            return forkJoin(obs$)
                .pipe(tap(() => {
                    this._ephemeralMessageService.showSaveSuccess();
                }));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe();
    }

    changeLanguage(newLanguage: string): void {
        this._selectedLanguage.next(newLanguage);
    }

    private validateIfCanChangeLanguage(): Observable<boolean> {
        if (this.form.dirty) {
            return this._messageDialogService.defaultUnsavedChangesWarn();
        }
        return of(true);
    }
}
