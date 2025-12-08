import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { PacksService } from '@admin-clients/cpanel/channels/packs/data-access';
import {
    eventsProviders, TicketContentImageFields,
    TicketContentImageRequest, TicketContentText, TicketContentTextFields
} from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EphemeralMessageService, IconManagerService, LanguageBarComponent, MessageDialogService, starIcon, ticketPassbookIcon,
    ticketPdfIcon, ticketPrinterIcon,
    UnsavedChangesDialogResult
} from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, QueryList, ViewChild, ViewChildren } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionPanel } from '@angular/material/expansion';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, forkJoin, Observable, of, throwError } from 'rxjs';
import { filter, map, shareReplay, tap } from 'rxjs/operators';
import { PackTicketContentPdfComponent } from './ticket-content-pdf/pack-ticket-content-pdf.component';
import { PackTicketContentPrinterComponent } from './ticket-content-printer/pack-ticket-content-printer.component';

@Component({
    selector: 'app-pack-ticket-content',
    templateUrl: './pack-ticket-content.component.html',
    styleUrls: ['./pack-ticket-content.component.scss'],
    imports: [
        AsyncPipe, TranslatePipe, FormContainerComponent, LanguageBarComponent, MatDividerModule, PackTicketContentPdfComponent,
        PackTicketContentPrinterComponent, MatProgressSpinnerModule, FlexLayoutModule
    ],
    providers: [eventsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PackTicketContentComponent {
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #fb = inject(FormBuilder);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #packsSrv = inject(PacksService);
    readonly #ephemeralMessage = inject(EphemeralMessageService);
    readonly #iconManagerSrv = inject(IconManagerService);
    readonly #selectedLanguage = new BehaviorSubject<string>(null);
    readonly #router = inject(Router);

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    @ViewChild('pdfContent')
    private _pdfContentComponent: PackTicketContentPdfComponent;

    @ViewChild('printerContent')
    private _printerContentComponent: PackTicketContentPrinterComponent;

    readonly languages$ = this.#channelsSrv.getChannel$().pipe(
        map(channel => channel.languages.selected),
        filter(Boolean),
        tap(languages => {
            this.#selectedLanguage.next(languages[0]);
        }),
        shareReplay(1)
    );

    readonly selectedLanguage$ = this.#selectedLanguage.asObservable();
    readonly isLoadingOrSaving$ = booleanOrMerge([
        this.#packsSrv.packTicketTexts.loading$(),
        this.#packsSrv.packTicketImages.loading$(),
        this.#packsSrv.packPrinterImages.loading$(),
        this.#packsSrv.packPrinterImages.loading$()
    ]);

    readonly form = this.#fb.group({});

    readonly $pack = toSignal(this.#packsSrv.pack.get$().pipe(
        filter(Boolean),
        tap(pack => {
            if (pack.type === 'MANUAL') {
                this.#router.navigate(['/channels', pack.channel_id, 'packs', pack.id, 'elements']);
            }
        })));

    constructor() {
        this.#iconManagerSrv.addIconDefinition(ticketPdfIcon, ticketPrinterIcon, ticketPassbookIcon, starIcon);
    }

    canChangeLanguage: () => Observable<boolean> = () =>
        this.validateIfCanChangeLanguage();

    cancel(): void {
        if (this._pdfContentComponent) {
            this._pdfContentComponent.cancel();
        }
        if (this._printerContentComponent) {
            this._printerContentComponent.cancel();
        }
    }

    getTextFields(contentForm: UntypedFormGroup, textFields: TicketContentTextFields[], language: string): TicketContentText[] {
        const textsToSave: TicketContentText[] = [];
        textFields.forEach(textField => {
            const field = contentForm.get(textField.type);
            if (field.dirty) {
                textsToSave.push({ type: textField.type, value: field.value, language });
            }
        });
        return textsToSave;
    }

    getImageFields(
        contentForm: UntypedFormGroup, imageFields: TicketContentImageFields[], language: string
    ): { [key: string]: TicketContentImageRequest[] } {
        const imagesToSave: TicketContentImageRequest[] = [];
        const imagesToDelete: TicketContentImageRequest[] = [];
        imageFields.forEach(imageField => {
            const field = contentForm.get(imageField.type);
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

    save(): void {
        this.save$()?.subscribe();
    }

    save$(): Observable<(void | void[])[]> {
        if (this.form.valid) {
            const obs$: Observable<void | void[]>[] = [];
            if (this._pdfContentComponent) {
                obs$.push(...this._pdfContentComponent.save(this.getTextFields.bind(this), this.getImageFields.bind(this)));
            }
            if (this._printerContentComponent) {
                obs$.push(...this._printerContentComponent.save(this.getTextFields.bind(this), this.getImageFields.bind(this)));
            }
            return forkJoin(obs$).pipe(tap(() => this.#ephemeralMessage.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => new Error('Invalid form'));
        }
    }

    changeLanguage(newLanguage: string): void {
        this.#selectedLanguage.next(newLanguage);
    }

    private validateIfCanChangeLanguage(): Observable<boolean> {
        if (this.form.dirty) {
            return this.#messageDialogService.openRichUnsavedChangesWarn().pipe(
                map(res => {
                    if (res === UnsavedChangesDialogResult.cancel) {
                        return false;
                    } else if (res === UnsavedChangesDialogResult.continue) {
                        return true;
                    } else {
                        this.save();
                        return true;
                    }
                }));
        }
        return of(true);
    }
}
