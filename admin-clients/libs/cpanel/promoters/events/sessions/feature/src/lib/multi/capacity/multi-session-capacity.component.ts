import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService, LinkedSession, SessionWrapper } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { StdVenueTplMgrComponent, VenueTemplateEditorType } from '@admin-clients/cpanel/venues/feature/standard-venue-tpl-manager';
import { MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { Topic, WebsocketsService, WsEventMsgType, WsSessionMsg } from '@admin-clients/shared/core/data-access';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { sessionCapacityProviders } from '@admin-clients/shared/venues/data-access/session-standard-venue-tpls';
import { VENUE_MAP_SERVICE } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplate, VenueTemplatesService, VenueTemplatesState } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, Component, OnInit, ViewChild } from '@angular/core';
import { combineLatest, debounceTime, Observable, of, startWith } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, take } from 'rxjs/operators';
import { MultiSessionCapacityService } from './multi-session-capacity.service';
import { MultiSessionCapacityState } from './state/multi-session-capacity.state';

@Component({
    selector: 'app-multi-session-capacity',
    templateUrl: './multi-session-capacity.component.html',
    styleUrls: ['./multi-session-capacity.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        VenueTemplatesService,
        VenueTemplatesState,
        MultiSessionCapacityState,
        ...sessionCapacityProviders,
        { provide: VENUE_MAP_SERVICE, useClass: MultiSessionCapacityService }
    ],
    standalone: false
})
export class MultiSessionCapacityComponent implements OnInit, WritingComponent {
    private _standardVenueTemplateComponent: StdVenueTplMgrComponent;
    private _selectedSessionsTemplates$: Observable<VenueTemplate[]>;
    readonly editorType = VenueTemplateEditorType.multiSessionTemplate;
    templates$: Observable<VenueTemplate[]>;
    template$: Observable<VenueTemplate>;
    isDirty$: Observable<boolean>;
    isSessionCapacityUpdateInProgress$: Observable<boolean>;
    linkedSessions$: Observable<LinkedSession[]>;

    @ViewChild(StdVenueTplMgrComponent)
    set standardVenueTemplateComponent(standardVenueTemplateComponent: StdVenueTplMgrComponent) {
        this._standardVenueTemplateComponent = standardVenueTemplateComponent;
        this.isDirty$ = standardVenueTemplateComponent?.isDirty$;
    }

    constructor(
        private _msgDialogSrv: MessageDialogService,
        private _eventsSrv: EventsService,
        private _eventsSessionsSrv: EventSessionsService,
        private _websocketsService: WebsocketsService
    ) { }

    ngOnInit(): void {
        this._selectedSessionsTemplates$ = this._eventsSessionsSrv.getSelectedSessions$()
            .pipe(
                map(sessionWrappers => sessionWrappers.map(sessionWrapper => sessionWrapper.session.venue_template)),
                shareReplay(1)
            );
        this.templates$ = this._selectedSessionsTemplates$
            .pipe(
                map(sessionsTemplates => {
                    if (sessionsTemplates?.length > 1) {
                        const templates = new Map<number, { venueTemplate: VenueTemplate; count: number }>();
                        sessionsTemplates?.forEach(sessionTemplate => {
                            if (!templates.has(sessionTemplate.id)) {
                                templates.set(sessionTemplate.id, { venueTemplate: sessionTemplate, count: 0 });
                            }
                            templates.get(sessionTemplate.id).count++;
                        });
                        return Array.from(templates.values())
                            .filter(templateWrapper => templateWrapper.count > 1)
                            .map(templateWrapper => templateWrapper.venueTemplate);
                    } else {
                        return null;
                    }
                })
            );
        this.template$ = this._selectedSessionsTemplates$
            .pipe(
                map(sessionTemplates => {
                    if (sessionTemplates?.length > 1) {
                        const candidateTemplate = sessionTemplates[0];
                        if (!sessionTemplates.find(sessionTemplate => sessionTemplate.id !== candidateTemplate.id)) {
                            return candidateTemplate;
                        }
                    }
                    return null;
                })
            );
        this.isSessionCapacityUpdateInProgress$ = combineLatest([
            this._eventsSessionsSrv.getSelectedSessions$()
                .pipe(map(sessionWrappers => sessionWrappers.map(s => s.session))),
            this._eventsSrv.event.get$().pipe(
                first(event => !!event),
                switchMap(event => this._websocketsService.getMessages$<WsSessionMsg>(Topic.event, event.id)),
                filter(wsMsg => !wsMsg || wsMsg?.type === WsEventMsgType.session),
                startWith(null)
            )
        ])
            .pipe(
                map(([sessions]) => sessions.some(session => session.updating_capacity)),
                debounceTime(500)
            );
        this.linkedSessions$ = this._eventsSessionsSrv.getLinkedSessions$();
    }

    selectTemplate(template: VenueTemplate): void {
        this._eventsSessionsSrv.getSelectedSessions$()
            .pipe(
                take(1),
                map(sessionWrappers => {
                    const result: SessionWrapper[] = [];
                    sessionWrappers
                        .forEach(sessionWrapper => {
                            if (sessionWrapper.session.venue_template.id === template.id) {
                                result.push(sessionWrapper);
                            } else {
                                sessionWrapper.selected = false;
                            }
                        });
                    return result;
                })
            )
            .subscribe(sessionWrappers => this._eventsSessionsSrv.setSelectedSessions(sessionWrappers));
    }

    canDeactivate(): Observable<boolean> {
        if (this.isDirty$) {
            return this.isDirty$
                .pipe(
                    switchMap(isDirty => isDirty ? this._msgDialogSrv.defaultUnsavedChangesWarn() : of(true))
                );
        } else {
            return of(true);
        }
    }

    cancelChanges(): void {
        this._standardVenueTemplateComponent?.cancelChanges();
    }

    saveChanges(): void {
        this._standardVenueTemplateComponent?.save();
    }
}
