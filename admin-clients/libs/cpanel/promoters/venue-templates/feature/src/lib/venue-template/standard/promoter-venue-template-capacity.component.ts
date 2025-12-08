import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { StdVenueTplMgrComponent, VenueTemplateEditorType } from '@admin-clients/cpanel/venues/feature/standard-venue-tpl-manager';
import { EditVenueTemplateDialogComponent } from '@admin-clients/cpanel-common-venue-templates-feature';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService, MessageDialogService, ObMatDialogConfig, UnsavedChangesDialogResult
} from '@admin-clients/shared/common/ui/components';
import {
    Topic, WebsocketsService, WsMsg, WsMsgStatus, WsOperatorMsgType, WsOperatorVenueTplData
} from '@admin-clients/shared/core/data-access';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerFullComponent } from '@admin-clients/shared/feature/form-container';
import {
    VenueTemplate, VenueTemplateInteractive, VenueTemplatesService, VenueTemplateStatus
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe, KeyValuePipe, UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatIcon } from '@angular/material/icon';
import { MatProgressBar } from '@angular/material/progress-bar';
import { MatDrawer, MatDrawerContainer, MatDrawerContent } from '@angular/material/sidenav';
import { MatTooltip } from '@angular/material/tooltip';
import { Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable, of, shareReplay, withLatestFrom } from 'rxjs';
import { catchError, filter, map, switchMap, take } from 'rxjs/operators';
import { InteractiveVenueTemplateDialogComponent } from '../interactive/interactive-venue-template-dialog.component';

@Component({
    selector: 'app-promoter-venue-template-capacity',
    templateUrl: './promoter-venue-template-capacity.component.html',
    styleUrls: ['./promoter-venue-template-capacity.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerFullComponent, AsyncPipe, UpperCasePipe, MatDrawerContainer, MatDrawer, MatIcon,
        MatIconButton, MatDivider, MatTooltip, FlexLayoutModule, TranslatePipe, StdVenueTplMgrComponent,
        MatProgressBar, KeyValuePipe, MatDrawerContent
    ]
})
export class PromoterVenueTemplateCapacityComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #destroyRef = inject(DestroyRef);
    readonly #router = inject(Router);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #messageDialogSrv = inject(MessageDialogService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #venueTemplatesService = inject(VenueTemplatesService);
    readonly #ws = inject(WebsocketsService);
    readonly #entityService = inject(EntitiesBaseService);
    readonly #matDialog = inject(MatDialog);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);

    #standardVenueTemplateComponent: StdVenueTplMgrComponent;

    readonly venueTemplateStatus = VenueTemplateStatus;
    readonly venueTemplateStatus$ = this.#venueTemplatesService.venueTpl.get$()
        .pipe(
            filter(venueTemplate => !!venueTemplate),
            withLatestFrom(this.#authSrv.getLoggedUser$()),
            switchMap(([venueTemplate, user]) => {
                if (user && venueTemplate.status === VenueTemplateStatus.inProgress) {
                    return this.#ws.getMessages$<WsMsg<WsOperatorMsgType, WsOperatorVenueTplData>>(Topic.operator, user.operator.id)
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

    readonly venueTemplate$ = combineLatest([
        this.venueTemplateStatus$,
        this.#venueTemplatesService.venueTpl.get$()
    ]).pipe(
        map(([status, venueTemplate]) => {
            venueTemplate.status = status || venueTemplate.status;
            return venueTemplate;
        })
    );

    readonly isOperatorUser$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);
    readonly showEditorBtn$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.REC_EDI]);

    venueTemplateInteractive$ = this.#venueTemplatesService.getVenueTemplateInteractive$();
    venueTemplateEditorType = VenueTemplateEditorType;
    isDirty$: Observable<boolean>;
    sidebarWidth$: Observable<string> = this.#breakpointObserver
        .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium])
        .pipe(map(result => result.matches ? '240px' : '280px'));

    ngOnInit(): void {
        this.#venueTemplatesService.venueTpl.get$()
            .pipe(
                filter(venueTemplate => !!venueTemplate),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(venueTemplate => this.#entityService.loadEntity(venueTemplate.entity.id));

        combineLatest([
            this.venueTemplate$,
            this.#entityService.getEntity$()
        ]).pipe(
            filter(([venueTemplate, entity]) => !!venueTemplate && !!entity),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(([venueTemplate, entity]) => {
            if (entity.settings?.interactive_venue.enabled) {
                this.#venueTemplatesService.loadVenueTemplateInteractive(venueTemplate.id);
            }
        });
    }

    ngOnDestroy(): void {
        this.#authSrv.getLoggedUser$().pipe(take(1)).subscribe(user => this.#ws.unsubscribeMessages(Topic.operator, user.operator.id));
        this.#venueTemplatesService.clearVenueTemplateInteractive();
    }

    @ViewChild(StdVenueTplMgrComponent)
    set standardVenueTemplateComponent(standardVenueTemplateComponent: StdVenueTplMgrComponent) {
        this.#standardVenueTemplateComponent = standardVenueTemplateComponent;
        if (standardVenueTemplateComponent) {
            this.isDirty$ = standardVenueTemplateComponent.isDirty$;
        }
    }

    get standardVenueTemplateComponent(): StdVenueTplMgrComponent {
        return this.#standardVenueTemplateComponent;
    }

    cancelChanges(): void {
        if (this.#standardVenueTemplateComponent) {
            this.#standardVenueTemplateComponent.cancelChanges();
        }
    }

    saveChanges(): void {
        this.save$().subscribe();
    }

    save$(): Observable<unknown> {
        return this.#standardVenueTemplateComponent?.save$() ?? of(true);
    }

    canDeactivate(): Observable<boolean> {
        return this.isDirty$
            ?.pipe(
                switchMap(isDirty => {
                    if (!isDirty) {
                        return of(true);
                    } else {
                        return this.#messageDialogSrv.openRichUnsavedChangesWarn().pipe(
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
                }),
                take(1)
            );
    }

    openEditTemplateDialog(venueTemplate: VenueTemplate): void {
        this.#matDialog.open(EditVenueTemplateDialogComponent, new ObMatDialogConfig<VenueTemplate>(venueTemplate))
            .beforeClosed()
            .subscribe(editedVenueTemplate => {
                if (editedVenueTemplate) {
                    venueTemplate.name = editedVenueTemplate.name;
                    this.#ephemeralMessageService.showSaveSuccess();
                    this.#ref.markForCheck();
                }
            });
    }

    openInteractiveVenuesTemplateDialog(venueTemplateId: number, venueTemplateInteractive: VenueTemplateInteractive): void {
        this.#matDialog.open(InteractiveVenueTemplateDialogComponent,
            new ObMatDialogConfig<{ venueTemplateId: number; venueTemplateInteractive: VenueTemplateInteractive }>(
                { venueTemplateId, venueTemplateInteractive }))
            .beforeClosed()
            .subscribe(edited => {
                if (edited) {
                    this.#ephemeralMessageService.showSaveSuccess();
                    this.#ref.markForCheck();
                }
            });
    }

    sidebarToggle(drawer: MatDrawer): void {
        drawer.toggle().then(() => this.#standardVenueTemplateComponent?.fitGraphicViewIfRequired());
    }

    gotoEditor(): void {
        this.#venueTemplatesService.venueTpl.get$().pipe(take(1))
            .subscribe(venueTemplate => this.#router.navigate(['promoter-venue-templates', venueTemplate.id, 'template-editor']));
    }
}
