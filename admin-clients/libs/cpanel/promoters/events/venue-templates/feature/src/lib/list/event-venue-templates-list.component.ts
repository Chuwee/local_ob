import { Event, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EditVenueTemplateDialogComponent, NewVenueTemplateDialogData, NewVenueTemplateDialogMode,
    NewVenueTemplateDialogComponent
} from '@admin-clients/cpanel-common-venue-templates-feature';
import { IsAvetEventPipe, IsSgaEventPipe } from '@admin-clients/cpanel-promoters-events-utils';
import { EventType } from '@admin-clients/shared/common/data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogService, MessageType, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { Topic, WebsocketsService, WsEventMsgType, WsEventVenueTplData, WsMsg, WsMsgStatus } from '@admin-clients/shared/core/data-access';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import {
    VenueTemplate, VenueTemplatesService, VenueTemplateStatus, VenueTemplateType
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, ViewContainerRef } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTooltip } from '@angular/material/tooltip';
import { ActivatedRoute, ActivatedRouteSnapshot, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, switchMap } from 'rxjs';
import { filter, first, map, pairwise, shareReplay, startWith, take, tap } from 'rxjs/operators';
import { CloneEventVenueTemplateDialogComponent } from '../clone/clone-event-venue-template-dialog.component';
import { VmEventVenueTemplate } from '../models/vm-event-venue-template.model';
import { EventVenueTemplateInfoComponent } from './info/event-venue-template-info.component';

type VenueTemplateListElement = VenueTemplate & {
    isActiveFromInProgress?: boolean;
};

@Component({
    selector: 'app-event-venue-templates-list',
    templateUrl: './event-venue-templates-list.component.html',
    styleUrls: ['./event-venue-templates-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, AsyncPipe, IsAvetEventPipe, IsSgaEventPipe, EllipsifyDirective, EventVenueTemplateInfoComponent,
        MatAccordion, MatTooltip, MatIcon, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle,
        MatProgressSpinner, MatIconButton
    ]
})
export class EventVenueTemplatesListComponent implements OnInit, OnDestroy {
    readonly #route = inject(ActivatedRoute);
    readonly #router = inject(Router);
    readonly #matDialog = inject(MatDialog);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ws = inject(WebsocketsService);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #eventSrv = inject(EventsService);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #viewContainerRef = inject(ViewContainerRef);
    // This subject can override the venue template that determine which venue template is selected in the template list
    readonly #selectedVenueTplOverride = new BehaviorSubject<Partial<VenueTemplate>>(null);
    // ENUMS
    readonly venueTplStatus = VenueTemplateStatus;

    readonly event$ = this.#eventSrv.event.get$();
    readonly $standardTemplate = toSignal(this.#eventSrv.event.get$()
        .pipe(map(event => event.type === EventType.normal || event.type === EventType.avet)), { initialValue: false });

    readonly $loading = toSignal(this.#venueTemplatesSrv.isVenueTemplatesListLoading$());

    readonly venueTemplates$ = this.#venueTemplatesSrv.getVenueTemplatesList$()
        .pipe(
            map(listResp => listResp?.data as VenueTemplateListElement[]),
            filter(Boolean),
            startWith(null),
            pairwise(),
            map(([oldList, newList]) => {
                if (oldList?.length < newList.length) { // new template
                    const addedTpl = newList
                        .filter(newTpl => oldList.find(oldTpl => oldTpl.id === newTpl.id) === undefined)
                        .pop();
                    if (addedTpl) {
                        this.#selectedVenueTplOverride.next(addedTpl);
                        if (addedTpl.status === VenueTemplateStatus.active) {
                            addedTpl.isActiveFromInProgress = true;
                            this.tplGenerationSuccessFlow(addedTpl);
                        } else if (addedTpl.status === VenueTemplateStatus.error) {
                            this.tplGenerationErrorFlow(addedTpl);
                        }
                    }
                } else if (newList.length < oldList?.length) { // removed template
                    if (!newList.length) { // last template deleted
                        this.#venueTemplatesSrv.venueTpl.clear();
                        this.#selectedVenueTplOverride.next(null);
                    } else {
                        const removedTpl = oldList
                            .filter(oldTpl => newList.find(newTpl => newTpl.id === oldTpl.id) === undefined)
                            .pop();
                        if (removedTpl) {
                            this.#selectedVenueTplOverride.next(
                                newList[Math.max(Math.min(oldList.indexOf(removedTpl) - 1, newList.length - 1), 0)]
                            );
                        }
                    }
                } else if (newList.length) { // auto selection
                    this.#venueTemplatesSrv.venueTpl.inProgress$()
                        .pipe(
                            first(inProgress => !inProgress),
                            switchMap(() => this.#venueTemplatesSrv.venueTpl.get$()),
                            take(1)
                        )
                        .subscribe(tpl => {
                            if (tpl && newList.find(listTpl => listTpl.id === tpl.id)) {
                                this.#selectedVenueTplOverride.next(tpl);
                            } else {
                                this.#selectedVenueTplOverride.next(newList[0]);
                            }
                        });
                }
                return newList;
            }),
            switchMap(venueTpls => this.#eventSrv.event.get$()
                .pipe(
                    take(1),
                    switchMap(event => this.#ws.getMessages$<WsMsg<WsEventMsgType, WsEventVenueTplData>>(Topic.event, event.id)),
                    map(wsMsg => ({ venueTpls, wsMsg }))
                )
            ),
            map(({ venueTpls, wsMsg }) => {
                if (wsMsg?.type === WsEventMsgType.venueTemplate) {
                    const msgTpl = venueTpls.find(tpl => tpl.id === wsMsg?.data?.id);
                    if (msgTpl) {
                        if (wsMsg.status === WsMsgStatus.done && msgTpl.status !== VenueTemplateStatus.active) {
                            msgTpl.status = VenueTemplateStatus.active;
                            this.reloadVenueTemplateIfRequired(msgTpl);
                            this.tplGenerationSuccessFlow(msgTpl);
                        } else if (wsMsg.status === WsMsgStatus.error && msgTpl.status !== VenueTemplateStatus.error) {
                            msgTpl.status = VenueTemplateStatus.error;
                            this.reloadVenueTemplateIfRequired(msgTpl);
                            this.tplGenerationErrorFlow(msgTpl);
                        }
                    }
                }
                return venueTpls;
            }),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly selectedVenueTpl$ = combineLatest([
        this.#venueTemplatesSrv.venueTpl.get$(),
        this.#selectedVenueTplOverride.asObservable().pipe(startWith(null))
    ])
        .pipe(
            map(([tpl, tplOverride]) => tplOverride || tpl),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly $selectedVenueTplInfo = toSignal(this.#venueTemplatesSrv.venueTpl.inProgress$()
        .pipe(
            switchMap(loading =>
                combineLatest([this.#venueTemplatesSrv.venueTpl.get$(), this.#venueTemplatesSrv.getVenueTemplatePriceTypes$()])
                    .pipe(map(([venueTemplate, priceTypes]) => ({
                        priceTypes: loading ? null : priceTypes?.length,
                        external_data: loading ? null : venueTemplate?.external_data
                    })))
            )
        ));

    ngOnInit(): void {
        this.#venueTemplatesSrv.clearVenueTemplateList();
        this.loadVenueTemplates();
    }

    ngOnDestroy(): void {
        this.#venueTemplatesSrv.clearVenueTemplateList();
        this.#eventSrv.event.get$().pipe(take(1))
            .subscribe(event => this.#ws.unsubscribeMessages(Topic.event, event.id));
    }

    selectVenueTemplate(venueTemplate: VenueTemplate): void {
        this.#selectedVenueTplOverride.next(venueTemplate);
        combineLatest([
            this.#venueTemplatesSrv.venueTpl.get$(),
            this.#eventSrv.event.get$()
        ])
            .pipe(take(1))
            .subscribe(([currentTpl, event]) => {
                const innerPaths = this.getInnerPaths(this.#route.snapshot, [venueTemplate.id]);
                if (currentTpl?.id !== venueTemplate.id || !innerPaths.length) {
                    const path = this.templatePath(event, venueTemplate);
                    if (innerPaths.includes(String(venueTemplate.id)) && innerPaths.includes(path)) {
                        this.#router.navigate(['/events', event.id, 'venue-templates', ...innerPaths]);
                    } else {
                        this.#router.navigate(['/events', event.id, 'venue-templates', venueTemplate.id, path]);
                    }
                }
            });
    }

    undoLastNavigation(): void {
        // reverts the selected item, emitting the loaded venue template again.
        this.#selectedVenueTplOverride.next(null);
    }

    openEditTemplateDialog(): void {
        this.#venueTemplatesSrv.venueTpl.get$()
            .pipe(take(1))
            .subscribe(venueTemplate => {
                this.#matDialog.open<EditVenueTemplateDialogComponent, unknown, VenueTemplate>(
                    EditVenueTemplateDialogComponent, new ObMatDialogConfig<VenueTemplate>(venueTemplate)
                )
                    .beforeClosed()
                    .subscribe(editedVenueTemplate => {
                        if (editedVenueTemplate) {
                            this.#venueTemplatesSrv.getVenueTemplatesList$()
                                .pipe(take(1))
                                .subscribe(templates =>
                                    templates.data.find(tmp => tmp.id === editedVenueTemplate.id).name = editedVenueTemplate.name
                                );
                            this.#ephemeralMessageSrv.showSaveSuccess();
                        }
                    });
            });

    }

    openDeleteTemplateDialog(): void {
        combineLatest([
            this.#venueTemplatesSrv.venueTpl.get$(),
            this.#selectedVenueTplOverride.asObservable()
        ])
            .pipe(
                take(1),
                map(([venueTpl, tempTpl]) => tempTpl || venueTpl),
                switchMap(venueTemplate =>
                    this.#msgDialogSrv.showWarn({
                        size: DialogSize.SMALL,
                        title: 'TITLES.DELETE_VENUE_TEMPLATE',
                        message: 'EVENTS.DELETE_VENUE_TEMPLATE_WARNING',
                        messageParams: { venueTemplateName: venueTemplate.name },
                        actionLabel: 'FORMS.ACTIONS.DELETE',
                        showCancelButton: true
                    })
                ),
                filter(Boolean),
                switchMap(() => this.#venueTemplatesSrv.venueTpl.get$()),
                take(1)
            )
            .subscribe(venueTemplate => {
                this.#venueTemplatesSrv.deleteVenueTemplate(venueTemplate.id.toString())
                    .pipe(tap(() => this.loadVenueTemplates()))
                    .subscribe(() => this.#ephemeralMessageSrv.showDeleteSuccess());
            });
    }

    openNewVenueTemplateDialog(): void {
        this.#eventSrv.event.get$()
            .pipe(
                take(1),
                switchMap(event =>
                    this.#matDialog.open<NewVenueTemplateDialogComponent, NewVenueTemplateDialogData, number>(
                        NewVenueTemplateDialogComponent,
                        new ObMatDialogConfig({ mode: NewVenueTemplateDialogMode.eventTemplate, event }, this.#viewContainerRef)
                    ).beforeClosed()
                )
            )
            .subscribe(templateId => {
                if (templateId) {
                    this.loadVenueTemplates();
                }
            });
    }

    openCloneTemplateDialog(): void {
        this.#venueTemplatesSrv.venueTpl.get$()
            .pipe(
                take(1),
                switchMap(venueTemplate =>
                    this.#matDialog
                        .open(CloneEventVenueTemplateDialogComponent, new ObMatDialogConfig({ fromVenueTemplate: venueTemplate }))
                        .beforeClosed()
                )
            )
            .subscribe(newTplId => {
                if (newTplId) {
                    this.loadVenueTemplates();
                }
            });
    }

    private templatePath(event: Event, venueTemplate: VenueTemplate): string {
        return event.type === EventType.activity
            || event.type === EventType.themePark
            || venueTemplate.type === VenueTemplateType.activity ?
            'activity' : 'standard';
    }

    private loadVenueTemplates(): void {
        this.#eventSrv.event.get$()
            .pipe(take(1))
            .subscribe(event => {
                this.#venueTemplatesSrv.loadVenueTemplatesList({
                    limit: 999,
                    offset: 0,
                    sort: 'name:asc',
                    eventId: event.id
                });
            });
    }

    private tplGenerationSuccessFlow(venueTpl: VmEventVenueTemplate): void {
        this.#ephemeralMessageSrv.showSuccess({
            msgKey: 'VENUES.ADD_VENUE_TPL_SUCCESS',
            msgParams: { tplName: venueTpl.name }
        });
    }

    private tplGenerationErrorFlow(venueTpl: VmEventVenueTemplate): void {
        this.#ephemeralMessageSrv.show({
            type: MessageType.warn,
            msgKey: 'VENUES.ADD_VENUE_TPL_ERROR',
            msgParams: { tplName: venueTpl.name }
        });
    }

    private reloadVenueTemplateIfRequired(venueTemplate: VenueTemplate): void {
        this.#venueTemplatesSrv.venueTpl.get$()
            .pipe(take(1))
            .subscribe(modelVenueTemplate => {
                if (!modelVenueTemplate || modelVenueTemplate.id === venueTemplate.id) {
                    this.#venueTemplatesSrv.venueTpl.load(venueTemplate.id);
                }
            });
    }

    // route promotion id (could be undefined if not present)
    private getIdPath(): unknown {
        return this.#route.snapshot.children[0]?.params;
    }

    // gets the inner path (tab route) if found
    private getInnerPaths(activatedRouteSnapshot: ActivatedRouteSnapshot, dynamicParams: (string | number)[] = []): string[] {
        let currentPath = activatedRouteSnapshot.routeConfig.path;
        const result: string[] = [];
        if (currentPath?.length) {
            if (currentPath.startsWith(':') && dynamicParams.length) {
                currentPath = String(dynamicParams[0]);
                dynamicParams = dynamicParams.slice(1);
            }
            result.push(currentPath);
        }
        if (activatedRouteSnapshot.children.length) {
            result.push(...this.getInnerPaths(activatedRouteSnapshot.children[0], dynamicParams));
        }
        return result;
    }
}
