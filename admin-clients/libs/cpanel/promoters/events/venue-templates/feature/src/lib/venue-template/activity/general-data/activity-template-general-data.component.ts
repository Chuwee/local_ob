import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { ArchivedEventMgrComponent } from '@admin-clients/cpanel/promoters/events/feature';
import { EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import {
    ActivityVenueTemplateMainDataComponent, ActivityVenueTemplatePriceTypesComponent,
    ActivityVenueTemplateQuotasComponent, ActivityVenueTemplateLimitsComponent
} from '@admin-clients/cpanel-common-venue-templates-feature';
import { PriceTypeTranslationsComponent } from '@admin-clients/cpanel-venues-venue-templates-feature';
import { EventType } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService, TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ACTIVITY_LIMITS_SERVICE, ActVenueTplService } from '@admin-clients/shared/venues/data-access/activity-venue-tpls';
import { VenueTemplatesService, VenueTemplateType } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import {
    AfterViewInit,
    ChangeDetectionStrategy, Component, computed, DestroyRef, ElementRef, inject, OnDestroy, OnInit, QueryList, ViewChild,
    ViewChildren
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatDivider } from '@angular/material/divider';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { forkJoin, Observable, of } from 'rxjs';
import { first, map, startWith, switchMap, take } from 'rxjs/operators';

@Component({
    selector: 'app-activity-template-general-data',
    templateUrl: './activity-template-general-data.component.html',
    styleUrls: ['./activity-template-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [{ provide: ACTIVITY_LIMITS_SERVICE, useExisting: ActVenueTplService }],
    imports: [
        FormContainerComponent, ReactiveFormsModule, AsyncPipe, MatExpansionPanel, MatExpansionPanelHeader,
        MatExpansionPanelTitle, TabsMenuComponent, TabDirective, TranslatePipe, MatProgressSpinner,
        ArchivedEventMgrComponent, ActivityVenueTemplateMainDataComponent, ActivityVenueTemplatePriceTypesComponent,
        ActivityVenueTemplateQuotasComponent, ActivityVenueTemplateLimitsComponent, PriceTypeTranslationsComponent,
        FlexLayoutModule, MatIcon, MatTooltip, MatButton, MatDivider
    ]
})
export class ActivityTemplateGeneralDataComponent implements OnInit, OnDestroy, AfterViewInit, WritingComponent {
    readonly #onDestroy = inject(DestroyRef);
    readonly #elemRef = inject(ElementRef);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #actVenueTplSrv = inject(ActVenueTplService);
    readonly #eventsService = inject(EventsService);
    readonly #sessionsService = inject(EventSessionsService);

    @ViewChild(ActivityVenueTemplateMainDataComponent) protected mainDataComponent: ActivityVenueTemplateMainDataComponent;
    @ViewChild(ActivityVenueTemplatePriceTypesComponent) protected priceTypesComponent: ActivityVenueTemplatePriceTypesComponent;
    @ViewChildren(PriceTypeTranslationsComponent) protected translationsComponent: QueryList<PriceTypeTranslationsComponent>;
    @ViewChild(ActivityVenueTemplateQuotasComponent) protected quotasComponent: ActivityVenueTemplateQuotasComponent;
    @ViewChild(ActivityVenueTemplateLimitsComponent) protected limitsComponent: ActivityVenueTemplateLimitsComponent;

    readonly mainForm = this.#fb.group({});
    readonly limitsForm = this.#fb.group({});
    readonly translationsForm = this.#fb.group({});
    readonly form = this.#fb.group({
        forms: this.#fb.array([this.mainForm, this.limitsForm, this.translationsForm])
    });

    readonly $event = toSignal(this.#eventsService.event.get$());
    readonly $venueTemplate = toSignal(this.#venueTemplatesSrv.venueTpl.get$());
    readonly $isSga = computed(() => this.$event()?.additional_config.inventory_provider === 'SGA');

    readonly languages$ = this.#eventsService.event.get$()
        .pipe(first(Boolean), map(event => event.settings?.languages?.selected));

    readonly $isSmartBooking = computed(() =>
        this.$event()?.type === EventType.avet && this.$venueTemplate()?.type === VenueTemplateType.activity);

    reqInProgress$: Observable<boolean>;

    ngOnInit(): void {
        this.form.valueChanges.pipe(
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(() => {
            if (this.priceTypesComponent) {
                if (this.form.dirty) {
                    this.quotasComponent.disable();
                } else {
                    this.quotasComponent.enable();
                }
            }
        });
    }

    ngAfterViewInit(): void {
        this.reqInProgress$ = booleanOrMerge([
            this.mainDataComponent?.requestInProgress$,
            this.priceTypesComponent?.requestsInProgress$,
            this.translationsComponent.changes.pipe(
                startWith(null),
                switchMap(() => this.translationsComponent.first?.requestsInProgress$ || of(false))
            ),
            this.quotasComponent?.requestsInProgress$,
            this.limitsComponent?.requestsInProgress$
        ]);
    }

    ngOnDestroy(): void {
        this.#actVenueTplSrv.clearActivityVenueTemplateData();
    }

    cancel(): void {
        this.mainDataComponent.reset();
        this.limitsComponent.reset();
        this.translationsComponent.first?.reset();
    }

    save(): void {
        if (this.form.valid) {
            const updates = [
                this.mainForm.dirty && this.mainDataComponent.saveData(),
                this.limitsForm.dirty && this.limitsComponent.save(),
                this.translationsForm.dirty && this.translationsComponent.first.save()
            ].filter(Boolean);
            forkJoin(updates)
                .pipe(
                    switchMap(() => this.#venueTemplatesSrv.venueTpl.get$()),
                    take(1)
                ).subscribe(tpl => {
                    this.#ephemeralMessageService.showSaveSuccess();
                    this.#venueTemplatesSrv.venueTpl.load(tpl.id);
                });
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(this.#elemRef.nativeElement);
        }
    }

    updateSgaMembershipInventory(): void {
        this.#sessionsService.refreshExternalMembershipInventory(this.$event()?.id)
            .subscribe(() => {
                this.#ephemeralMessageService.showSuccess({
                    msgKey: 'TITLES.SESSION.EXTERNAL_AVAILABILITY_UPDATE_IN_PROGRESS'
                });
            });
    }
}
