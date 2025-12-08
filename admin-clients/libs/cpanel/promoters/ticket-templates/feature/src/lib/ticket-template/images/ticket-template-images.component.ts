import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    TicketTemplateFormat, TicketTemplate, ticketTemplatePrinterImageRestrictions,
    ticketTemplatePdfImageRestrictions, TicketTemplatesService, PostTicketTemplateImage,
    TicketTemplateImageType, TicketTemplatePrinterImageType, TicketTemplatePdfImageType
} from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import {
    EphemeralMessageService, ImageUploaderComponent,
    LanguageBarComponent, MessageDialogService,
    UnsavedChangesDialogResult
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { ObFile } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { NgIf, AsyncPipe } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexModule } from '@angular/flex-layout/flex';
import { UntypedFormBuilder, UntypedFormGroup, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, Observable, of, throwError } from 'rxjs';
import { catchError, filter, first, map, switchMap, take, tap } from 'rxjs/operators';

const writePermissionRoles = [UserRoles.OPR_MGR, UserRoles.EVN_MGR];

@Component({
    selector: 'app-ticket-template-images',
    templateUrl: './ticket-template-images.component.html',
    styleUrls: ['./ticket-template-images.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, LanguageBarComponent, FlexModule, ReactiveFormsModule,
        NgIf, ImageUploaderComponent, MaterialModule, AsyncPipe, TranslatePipe
    ]
})
export class TicketTemplateImagesComponent implements OnInit, AfterViewInit {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ticketsTemplateService = inject(TicketTemplatesService);
    readonly #auth = inject(AuthenticationService);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #destroyRef = inject(DestroyRef);
    #language = new BehaviorSubject<string>(null);

    format = TicketTemplateFormat;
    ticketTemplate: TicketTemplate;
    form: UntypedFormGroup;
    printerImageRestrictions = ticketTemplatePrinterImageRestrictions;
    pdfImageRestrictions = ticketTemplatePdfImageRestrictions;
    readonly isInProgress$ = booleanOrMerge([
        this.#ticketsTemplateService.isTicketTemplateInProgress$(),
        this.#ticketsTemplateService.isTicketTemplateImagesInProgress$()
    ]);

    readonly languageList$ = this.#ticketsTemplateService.getTicketTemplate$()
        .pipe(
            first(Boolean),
            tap(ticketTemplate => {
                this.initForms(ticketTemplate);
                const { languages } = ticketTemplate;
                this.loadTicketTemplateImages(ticketTemplate, languages.default);
                this.#language.next(languages.default);
                this.ticketTemplate = ticketTemplate;
            }),
            map(({ languages }) => languages.selected)
        );

    language$ = this.#language.asObservable();

    canChangeLanguage: (() => Observable<boolean>) = () => this.validateIfCanChangeLanguage();

    ngOnInit(): void {
        this.form = this.#fb.group({});
    }

    ngAfterViewInit(): void {
        this.refreshFormDataHandler();
    }

    save(): void {
        this.save$().subscribe(() => {
            this.loadTicketTemplateImages(this.ticketTemplate, this.#language.getValue());
            this.#ephemeralMessageService.showSaveSuccess();
            this.form.markAsPristine();
        });
    }

    save$(): Observable<void[]> {
        if (this.form.valid) {
            const images: PostTicketTemplateImage[] = Object.keys(this.getDirtyValues(this.form))
                .map(type => ({
                    image: (this.form.value[type] as ObFile)?.data,
                    language: this.#language.getValue(),
                    type: type as TicketTemplateImageType
                }));

            return this.#ticketsTemplateService.saveTicketTemplateImages(this.ticketTemplate, images)
                .pipe(take(1));
        } else {
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this.#ticketsTemplateService.clearTicketTemplateImages();
        this.loadTicketTemplateImages(this.ticketTemplate, this.#language.getValue());
        this.form.markAsPristine();
    }

    changeLanguage(newLanguage: string): void {
        this.loadTicketTemplateImages(this.ticketTemplate, newLanguage);
        this.#language.next(newLanguage);
    }

    openTicketPreview(): void {
        this.#ticketsTemplateService.downloadTicketPdfPreview$(this.ticketTemplate.id, this.#language.getValue())
            .subscribe(res => window.open(res?.url, '_blank'));
    }

    private loadTicketTemplateImages(ticketTemplate: TicketTemplate, language: string): void {
        this.#ticketsTemplateService.clearTicketTemplateImages();
        this.#ticketsTemplateService.loadTicketTemplateImages(ticketTemplate, language);
    }

    private initForms(ticketTemplate: TicketTemplate): void {
        this.form = this.#fb.group({});

        let types: TicketTemplateImageType[] = [];

        if (ticketTemplate.design.format === this.format.printer) {
            types = Object.values(TicketTemplatePrinterImageType);
        } else if (ticketTemplate.design.format === this.format.pdf) {
            types = Object.values(TicketTemplatePdfImageType);
        }

        types.forEach(type =>
            this.form.addControl(type, this.#fb.control(null))
        );
    }

    private validateIfCanChangeLanguage(): Observable<boolean> {
        if (this.form.dirty) {
            return this.#messageDialogService.openRichUnsavedChangesWarn().pipe(
                switchMap(res => {
                    if (res === UnsavedChangesDialogResult.cancel) {
                        return of(false);
                    } else if (res === UnsavedChangesDialogResult.continue) {
                        return of(true);
                    } else {
                        return this.save$().pipe(
                            switchMap(() => of(true)),
                            catchError(() => of(false))
                        );
                    }
                }));
        }
        return of(true);
    }

    private refreshFormDataHandler(): void {
        combineLatest([
            this.#ticketsTemplateService.getTicketTemplateImages$(),
            this.#auth.hasLoggedUserSomeRoles$(writePermissionRoles)
        ]).pipe(
            filter(data => data.every(elem => elem !== null)),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(([images, canWrite]) => {
            this.form.reset();

            images?.forEach(({ type, image_url: url }) =>
                this.form.get(type).setValue(url));

            if (!canWrite) {
                this.form.disable();
            }
        });
    }

    private getDirtyValues(form: UntypedFormGroup): { [key in TicketTemplateImageType]?: ObFile } {
        const dirtyValues = {};
        Object.keys(form.controls).forEach(control => {
            const currentControl = form.get(control);
            if (currentControl.dirty) {
                dirtyValues[control] = currentControl.value;
            }
        });
        return dirtyValues;
    }

}
