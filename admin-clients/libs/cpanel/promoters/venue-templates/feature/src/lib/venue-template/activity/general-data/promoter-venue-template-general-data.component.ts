import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    ActivityVenueTemplateLimitsComponent, ActivityVenueTemplateMainDataComponent,
    ActivityVenueTemplatePriceTypesComponent, ActivityVenueTemplateQuotasComponent
} from '@admin-clients/cpanel-common-venue-templates-feature';
import { PriceTypeTranslationsComponent } from '@admin-clients/cpanel-venues-venue-templates-feature';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService, TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ACTIVITY_LIMITS_SERVICE, ActVenueTplService } from '@admin-clients/shared/venues/data-access/activity-venue-tpls';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import {
    AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef, ElementRef, inject, OnDestroy, OnInit, QueryList, ViewChild, ViewChildren
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, forkJoin, Observable, of } from 'rxjs';
import { first, map, switchMap } from 'rxjs/operators';

@Component({
    selector: 'app-promoter-venue-template-general-data',
    templateUrl: './promoter-venue-template-general-data.component.html',
    styleUrls: ['./promoter-venue-template-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [{ provide: ACTIVITY_LIMITS_SERVICE, useExisting: ActVenueTplService }],
    imports: [
        FormContainerComponent, ReactiveFormsModule, AsyncPipe, MatExpansionPanel, MatExpansionPanelHeader,
        TabsMenuComponent, TabDirective, TranslatePipe, MatProgressSpinner, MatExpansionPanelTitle,
        ActivityVenueTemplateMainDataComponent, ActivityVenueTemplatePriceTypesComponent,
        ActivityVenueTemplateQuotasComponent, ActivityVenueTemplateLimitsComponent, PriceTypeTranslationsComponent,
        FlexLayoutModule
    ]
})
export class PromoterVenueTemplateGeneralDataComponent implements OnInit, AfterViewInit, OnDestroy, WritingComponent {
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #onDestroy = inject(DestroyRef);
    readonly #elemRef = inject(ElementRef);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #actVenueTplSrv = inject(ActVenueTplService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #authSrv = inject(AuthenticationService);

    #templateId: number;
    @ViewChild(ActivityVenueTemplateMainDataComponent) private _mainDataComponent: ActivityVenueTemplateMainDataComponent;
    @ViewChild(ActivityVenueTemplatePriceTypesComponent) private _priceTypesComponent: ActivityVenueTemplatePriceTypesComponent;
    @ViewChildren(PriceTypeTranslationsComponent) private _translationsComponent: QueryList<PriceTypeTranslationsComponent>;
    @ViewChild(ActivityVenueTemplateQuotasComponent) private _quotasComponent: ActivityVenueTemplateQuotasComponent;
    @ViewChild(ActivityVenueTemplateLimitsComponent) private _limitsComponent: ActivityVenueTemplateLimitsComponent;
    form: UntypedFormGroup;
    mainForm: UntypedFormGroup;
    limitsForm: UntypedFormGroup;
    translationsForm: UntypedFormGroup;
    reqInProgress$: Observable<boolean>;
    languages$: Observable<string[]>;

    readonly venueTemplate$ = this.#venueTemplatesSrv.venueTpl.get$();

    ngOnInit(): void {
        this.#actVenueTplSrv.clearActivityVenueTemplateData();
        this.#venueTemplatesSrv.venueTpl.get$()
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(venueTemplate => this.#templateId = venueTemplate?.id);
        this.languages$ = combineLatest([
            this.#authSrv.getLoggedUser$(),
            this.#venueTemplatesSrv.venueTpl.get$()
        ]).pipe(
            switchMap(([user, venueTemplate]) => {
                if (AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR])) {
                    this.#entitiesSrv.loadEntity(venueTemplate.entity.id);
                    return this.#entitiesSrv.getEntity$();
                } else {
                    return of(user?.entity);
                }
            }),
            first(entity => !!entity),
            map(entity => entity.settings?.languages.available)
        );
        this.defineMainForm();
    }

    ngAfterViewInit(): void {
        this.reqInProgress$ = booleanOrMerge([
            this._mainDataComponent?.requestInProgress$,
            this._priceTypesComponent?.requestsInProgress$,
            this._quotasComponent?.requestsInProgress$,
            this._limitsComponent?.requestsInProgress$,
            this._translationsComponent.changes
                .pipe(switchMap(() => this._translationsComponent.first?.requestsInProgress$ || of(false)))
        ]);
    }

    ngOnDestroy(): void {
        this.#actVenueTplSrv.clearActivityVenueTemplateData();
    }

    cancel(): void {
        this._mainDataComponent.reset();
        this._limitsComponent.reset();
        this._translationsComponent?.first?.reset();
    }

    save(): void {
        const updates: Observable<void>[] = [];
        if (this.mainForm.dirty && this.mainForm.valid) {
            updates.push(this._mainDataComponent.saveData());
        }
        if (this.limitsForm.dirty && this.limitsForm.valid) {
            updates.push(this._limitsComponent.save());
        }
        if (this.translationsForm.dirty && this.translationsForm.valid) {
            updates.push(this._translationsComponent.first.save());
        }
        if (updates.length) {
            forkJoin(updates)
                .subscribe(() => {
                    this.#ephemeralMessageService.showSaveSuccess();
                    this.#venueTemplatesSrv.venueTpl.load(this.#templateId);
                });
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(this.#elemRef.nativeElement);
        }
    }

    private defineMainForm(): void {
        this.mainForm = this.#fb.group({});
        this.limitsForm = this.#fb.group({});
        this.translationsForm = this.#fb.group({});
        this.form = this.#fb.group({
            mainForm: this.mainForm,
            limitsForm: this.limitsForm,
            translationsForm: this.translationsForm
        });
        this.form.valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(() => {
                if (this.form.dirty) {
                    this._quotasComponent?.disable();
                } else {
                    this._quotasComponent?.enable();
                }
            });
    }
}
