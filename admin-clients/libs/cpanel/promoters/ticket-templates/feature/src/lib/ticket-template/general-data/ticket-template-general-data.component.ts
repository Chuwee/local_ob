import { scrollIntoFirstInvalidFieldOrErrorMsg, FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    TicketTemplate, defaultTicketTemplateLiterals,
    TicketTemplatesService, PutTicketTemplate, TicketTemplateFieldRestriction
} from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService, LanguageSelectorComponent,
    LanguageSelector, HelpButtonComponent, SelectSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { DefaultIconComponent } from '@admin-clients/shared-common-ui-default-icon';
import { NgFor, NgIf, AsyncPipe } from '@angular/common';
import {
    AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, OnInit, QueryList, ViewChild,
    inject
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UntypedFormBuilder, UntypedFormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable, throwError } from 'rxjs';
import { filter, first, map, shareReplay, tap } from 'rxjs/operators';
import { compareObjectsById } from '../../ticket-templates.utils';

const writePermissionRoles = [UserRoles.OPR_MGR, UserRoles.EVN_MGR];

@Component({
    selector: 'app-ticket-template-general-data',
    templateUrl: './ticket-template-general-data.component.html',
    styleUrls: ['./ticket-template-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, DefaultIconComponent, HelpButtonComponent,
        ReactiveFormsModule, FormControlErrorsComponent,
        SelectSearchComponent, NgFor, EllipsifyDirective,
        NgIf, LanguageSelectorComponent, MaterialModule,
        AsyncPipe, TranslatePipe
    ]
})
export class TicketTemplateGeneralDataComponent implements OnInit, AfterViewInit, WritingComponent {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ticketTemplatesSrv = inject(TicketTemplatesService);
    readonly #auth = inject(AuthenticationService);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #entityService = inject(EntitiesBaseService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);

    readonly #destroyRef = inject(DestroyRef);
    #matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    @ViewChild('languageSelector') languageSelector: LanguageSelectorComponent;

    form: UntypedFormGroup;
    languageSelectorData: LanguageSelector;
    ticketTemplate: TicketTemplate;
    readonly isInProgress$ = booleanOrMerge([
        this.#ticketTemplatesSrv.isTicketTemplateInProgress$(),
        this.#entityService.isEntityLoading$()
    ]);

    readonly designs$ = combineLatest([
        this.#ticketTemplatesSrv.getDesignsList$(),
        this.#ticketTemplatesSrv.getTicketTemplate$()
    ]).pipe(
        filter(data => data.every(elem => !!elem)),
        map(([designs, ticketTemplate]) =>
            designs.filter(design => design.format === ticketTemplate.design.format || !design)
        ),
        shareReplay(1)
    );

    compareObjectsById = compareObjectsById;

    updateDefault: (ticketTemplateId: number, isDefault: boolean) => Observable<boolean>;
    readonly defaultTicketTemplateLiterals = defaultTicketTemplateLiterals;

    ngOnInit(): void {
        this.#entityService.clearEntity();
        this.#ticketTemplatesSrv.loadDesigns();

        this.initForms();

        this.#ticketTemplatesSrv.getTicketTemplate$().pipe(
            first(Boolean)
        ).subscribe(ticketTemplate => this.#entityService.loadEntity(ticketTemplate.entity.id));

        this.updateDefault = (ticketTemplateId, isDefault) =>
            this.#ticketTemplatesSrv.updateTicketTemplateDefault(ticketTemplateId, isDefault).pipe(tap(() =>
                this.#ticketTemplatesSrv.loadTicketTemplate(this.ticketTemplate.id.toString())));
    }

    ngAfterViewInit(): void {
        this.refreshFormDataHandler();
    }

    cancel(): void {
        this.#ticketTemplatesSrv.loadTicketTemplate(this.ticketTemplate.id.toString());
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const ticketTemplate: PutTicketTemplate = {
                id: this.ticketTemplate.id,
                name: this.form.value.name,
                design_id: this.form.value.design.id,
                languages: {
                    selected: this.languageSelector.getSelectedLanguages(),
                    default: this.languageSelector.getDefaultLanguage()
                }
            };

            return this.#ticketTemplatesSrv.updateTicketTemplate(ticketTemplate)
                .pipe(
                    map(() => {
                        this.#ticketTemplatesSrv.loadTicketTemplate(this.ticketTemplate.id.toString());
                        this.#ephemeralMessageService.showSaveSuccess();

                    }));

        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this.#matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    private initForms(): void {
        this.form = this.#fb.group({
            name: [null, [
                Validators.required,
                Validators.maxLength(TicketTemplateFieldRestriction.nameMaxLength)
            ]],
            design: [null, Validators.required]
        });
    }

    private refreshFormDataHandler(): void {
        combineLatest([
            this.#ticketTemplatesSrv.getTicketTemplate$(),
            this.#entityService.getEntityAvailableLenguages$(),
            this.#auth.hasLoggedUserSomeRoles$(writePermissionRoles)
        ]).pipe(
            filter(data => data.every(elem => elem != null)),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(([ticketTemplate, entityAvailableLanguages, canWrite]) => {
            this.ticketTemplate = ticketTemplate;

            this.form.patchValue({
                name: ticketTemplate.name,
                design: ticketTemplate.design
            });

            this.languageSelectorData = {
                default: ticketTemplate.languages?.default,
                selected: ticketTemplate.languages?.selected,
                languages: entityAvailableLanguages || []
            };

            this.form.addControl('languages', this.languageSelector.form);
            this.#ref.detectChanges();
            this.form.markAsPristine();

            if (!canWrite) {
                this.form.disable();
            }
        });
    }
}
