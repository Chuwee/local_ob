import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { StdVenueTplMgrComponent, VenueTemplateEditorType } from '@admin-clients/cpanel/venues/feature/standard-venue-tpl-manager';
import { EditVenueTemplateDialogComponent } from '@admin-clients/cpanel-common-venue-templates-feature';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import {
    ContextNotificationComponent,
    EphemeralMessageService, MessageDialogService,
    ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import {
    Topic, WebsocketsService, WsMsg, WsMsgStatus, WsOperatorMsgType, WsOperatorVenueTplData
} from '@admin-clients/shared/core/data-access';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerFullComponent } from '@admin-clients/shared/feature/form-container';
import { RoutingState } from '@admin-clients/shared/utility/state';
import { VenueTemplate, VenueTemplatesService, VenueTemplateStatus } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe, NgClass, NgIf, UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { MatDrawer } from '@angular/material/sidenav';
import { Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable, of, shareReplay, Subject, withLatestFrom } from 'rxjs';
import { filter, map, switchMap, take, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-venue-template-capacity',
    templateUrl: './venue-template-capacity.component.html',
    styleUrls: ['./venue-template-capacity.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, FlexLayoutModule, MaterialModule, NgIf, AsyncPipe, NgClass, FormContainerFullComponent,
        UpperCasePipe, StdVenueTplMgrComponent, ContextNotificationComponent
    ]
})
export class VenueTemplateCapacityComponent implements OnInit, OnDestroy, WritingComponent {
    private readonly _onDestroy: Subject<void> = new Subject<void>();

    private readonly _routingState = inject(RoutingState);
    private readonly _router = inject(Router);
    private readonly _breakpointObserver = inject(BreakpointObserver);
    private readonly _messageDialogSrv = inject(MessageDialogService);
    private readonly _authSrv = inject(AuthenticationService);
    private readonly _venueTemplatesService = inject(VenueTemplatesService);
    private readonly _entityService = inject(EntitiesBaseService);
    private readonly _auth = inject(AuthenticationService);
    private readonly _matDialog = inject(MatDialog);
    private readonly _ref = inject(ChangeDetectorRef);
    private readonly _ephemeralMessageService = inject(EphemeralMessageService);
    private readonly _ws = inject(WebsocketsService);

    private _standardVenueTemplateComponent: StdVenueTplMgrComponent;

    readonly venueTemplateStatus = VenueTemplateStatus;
    readonly sidebarWidth$ = this._breakpointObserver
        .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium])
        .pipe(map(result => result.matches ? '240px' : '280px'));

    venueTemplate$: Observable<VenueTemplate>;
    venueTemplateEditorType = VenueTemplateEditorType;
    isDirty$: Observable<boolean>;
    venueTemplateStatus$: Observable<VenueTemplateStatus>;

    readonly showEditorBtn$ = this._authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.REC_EDI]);

    ngOnInit(): void {
        this.venueTemplateStatus$ = this._venueTemplatesService.venueTpl.get$()
            .pipe(
                filter(venueTemplate => !!venueTemplate),
                withLatestFrom(this._auth.getLoggedUser$()),
                switchMap(([venueTemplate, user]) => {
                    if (user && venueTemplate.status === VenueTemplateStatus.inProgress) {
                        return this._ws.getMessages$<WsMsg<WsOperatorMsgType, WsOperatorVenueTplData>>(Topic.operator, user.operator.id)
                            .pipe(
                                filter(wsMsg =>
                                    wsMsg?.type === WsOperatorMsgType.venueTemplate
                                    && Number(wsMsg.data.id) === venueTemplate.id
                                ),
                                map(wsMsg => {
                                    if (wsMsg.status === WsMsgStatus.done) {
                                        return VenueTemplateStatus.active;
                                    } else if (wsMsg.status === WsMsgStatus.error) {
                                        return VenueTemplateStatus.error;
                                    } else if (wsMsg.status === WsMsgStatus.inProgress) {
                                        return VenueTemplateStatus.inProgress;
                                    } else {
                                        return undefined;
                                    }
                                })
                            );
                    } else {
                        return of(venueTemplate.status);
                    }
                }),
                shareReplay(1)
            );
        this.venueTemplate$ = combineLatest([
            this.venueTemplateStatus$,
            this._venueTemplatesService.venueTpl.get$()
        ])
            .pipe(
                map(([status, venueTemplate]) => {
                    venueTemplate.status = status || venueTemplate.status;
                    return venueTemplate;
                })
            );

        this._venueTemplatesService.venueTpl.get$()
            .pipe(
                filter(venueTemplate => !!venueTemplate),
                takeUntil(this._onDestroy)
            ).subscribe(venueTemplate => this._entityService.loadEntity(venueTemplate.entity.id));
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._auth.getLoggedUser$().pipe(take(1)).subscribe(user => this._ws.unsubscribeMessages(Topic.operator, user.operator.id));

    }

    @ViewChild(StdVenueTplMgrComponent)
    set standardVenueTemplateComponent(standardVenueTemplateComponent: StdVenueTplMgrComponent) {
        this._standardVenueTemplateComponent = standardVenueTemplateComponent;
        if (standardVenueTemplateComponent) {
            this.isDirty$ = standardVenueTemplateComponent.isDirty$;
        }
    }

    get standardVenueTemplateComponent(): StdVenueTplMgrComponent {
        return this._standardVenueTemplateComponent;
    }

    cancelChanges(): void {
        if (this._standardVenueTemplateComponent) {
            this._standardVenueTemplateComponent.cancelChanges();
        }
    }

    saveChanges(): void {
        if (this._standardVenueTemplateComponent) {
            this._standardVenueTemplateComponent.save();
        }
    }

    canDeactivate(): Observable<boolean> {
        return this.isDirty$
            ?.pipe(
                switchMap(isDirty => {
                    if (!isDirty) {
                        return of(true);
                    } else {
                        return this._messageDialogSrv.defaultUnsavedChangesWarn();
                    }
                }),
                take(1)
            );
    }

    openEditTemplateDialog(venueTemplate: VenueTemplate): void {
        this._matDialog.open(EditVenueTemplateDialogComponent, new ObMatDialogConfig<VenueTemplate>(venueTemplate))
            .beforeClosed()
            .subscribe(editedVenueTemplate => {
                if (editedVenueTemplate) {
                    venueTemplate.name = editedVenueTemplate.name;
                    this._ephemeralMessageService.showSaveSuccess();
                    this._ref.markForCheck();
                }
            });
    }

    sidebarToggle(drawer: MatDrawer): void {
        drawer.toggle().then(() =>this._standardVenueTemplateComponent?.fitGraphicViewIfRequired());
    }

    gotoEditor(): void {
        this._venueTemplatesService.venueTpl.get$().pipe(take(1))
            .subscribe(venueTemplate => {
                this._routingState.removeLastUrlsWith('/venue-templates/');
                this._router.navigate(['venue-templates', venueTemplate.id, 'template-editor']);
            });
    }
}
