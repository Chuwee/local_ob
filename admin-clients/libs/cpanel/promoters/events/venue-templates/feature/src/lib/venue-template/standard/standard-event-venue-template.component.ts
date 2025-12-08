import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EventsService, EventStatus } from '@admin-clients/cpanel/promoters/events/data-access';
import { ArchivedEventMgrComponent } from '@admin-clients/cpanel/promoters/events/feature';
import { EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { StdVenueTplMgrComponent, VenueTemplateEditorType } from '@admin-clients/cpanel/venues/feature/standard-venue-tpl-manager';
import { ContextNotificationComponent, MessageDialogService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerFullComponent } from '@admin-clients/shared/feature/form-container';
import { RoutingState } from '@admin-clients/shared/utility/state';
import {
    VenueTemplate, VenueTemplatesService, VenueTemplateStatus, VenueTemplateType
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, ViewChild } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatProgressBar } from '@angular/material/progress-bar';
import { Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, map, Observable, of } from 'rxjs';
import { filter, startWith, switchMap, take } from 'rxjs/operators';
import { ClonePromoterVenueTemplateDialogComponent } from '../../clone-promoter/clone-promoter-venue-template-dialog.component';

@Component({
    selector: 'app-standard-event-venue-template',
    templateUrl: './standard-event-venue-template.component.html',
    styleUrls: ['./standard-event-venue-template.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe, FlexLayoutModule, FormContainerFullComponent, ContextNotificationComponent, StdVenueTplMgrComponent,
        ArchivedEventMgrComponent, MatProgressBar, MatIcon, MatButton, TranslatePipe
    ]
})
export class StandardEventVenueTemplateComponent implements OnInit, WritingComponent {
    readonly #destroyRef = inject(DestroyRef);
    readonly #routingState = inject(RoutingState);
    readonly #router = inject(Router);
    readonly #authSrv = inject(AuthenticationService);
    readonly #venueTemplateService = inject(VenueTemplatesService);
    readonly #eventSrv = inject(EventsService);
    readonly #eventSessionsSrv = inject(EventSessionsService);
    readonly #matDialog = inject(MatDialog);
    readonly #msgDialogService = inject(MessageDialogService);

    #standardVenueTemplateComponent: StdVenueTplMgrComponent;

    readonly eventVenueTemplateEditorType = VenueTemplateEditorType.eventTemplate;

    venueTemplate$: Observable<VenueTemplate>;
    venueTplStatus = VenueTemplateStatus;
    venueTemplateType = VenueTemplateType;
    isDirty$: Observable<boolean>;
    isInCapacityIncrease$: Observable<boolean>;
    isInUse$: Observable<boolean>;
    increaseDisabled$: Observable<boolean>;

    readonly showEditorBtn$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.REC_EDI]);

    @ViewChild(StdVenueTplMgrComponent)
    set standardVenueTemplateComponent(standardVenueTemplateComponent: StdVenueTplMgrComponent) {
        this.#standardVenueTemplateComponent = standardVenueTemplateComponent;
        if (standardVenueTemplateComponent) {
            this.isDirty$ = standardVenueTemplateComponent.isDirty$;
            this.isInCapacityIncrease$ = standardVenueTemplateComponent.isInCapIncr$;
        }
    }

    constructor() { }

    ngOnInit(): void {
        this.venueTemplate$ = this.#venueTemplateService.venueTpl.get$();
        this.isInUse$ = this.#eventSessionsSrv.isTemplateInUse$().pipe(startWith(true));
        combineLatest([this.#venueTemplateService.venueTpl.get$(), this.#eventSrv.event.get$()])
            .pipe(
                filter(sources => sources.every(source => !!source)),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(([template, event]) => this.#eventSessionsSrv.loadTemplateIsInUse(event.id, template.id));
        this.increaseDisabled$ = this.#eventSrv.event.get$().pipe(
            map(event => event.status === EventStatus.ready)
        );
    }

    cancelChanges(): void {
        if (this.#standardVenueTemplateComponent) {
            this.#standardVenueTemplateComponent.cancelChanges();
        }
    }

    saveChanges(): void {
        if (this.#standardVenueTemplateComponent) {
            this.#standardVenueTemplateComponent.save();
        }
    }

    openClonePromoterTemplateDialog(): void {
        this.#venueTemplateService.venueTpl.get$()
            .pipe(take(1))
            .subscribe(template =>
                this.#matDialog.open(
                    ClonePromoterVenueTemplateDialogComponent,
                    new ObMatDialogConfig({ fromVenueTemplate: template })
                )
            );
    }

    canDeactivate(): Observable<boolean> {
        if (this.isDirty$) {
            return this.isDirty$
                .pipe(
                    switchMap(isDirty => {
                        if (isDirty) {
                            return this.#msgDialogService.defaultUnsavedChangesWarn();
                        } else {
                            return of(true);
                        }
                    }),
                    take(1)
                );
        } else {
            return of(true);
        }
    }

    gotoEditor(): void {
        combineLatest([
            this.#eventSrv.event.get$(),
            this.#venueTemplateService.venueTpl.get$()
        ])
            .pipe(take(1))
            .subscribe(([event, venueTemplate]) => {
                this.#routingState.removeLastUrlsWith('/events/');
                this.#router.navigate(['events', event.id, 'template-editor', venueTemplate.id]);
            });
    }
}
