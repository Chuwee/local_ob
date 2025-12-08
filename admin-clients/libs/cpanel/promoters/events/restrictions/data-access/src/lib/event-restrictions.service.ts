import { StateManager } from '@OneboxTM/utils-state';
import { HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { firstValueFrom, throwError } from 'rxjs';
import { catchError, filter, map, tap } from 'rxjs/operators';
import { EventRestrictionsApi } from './api/event-restrictions.api';
import { EventRestrictionListElem, EventRestrictionType } from './models/event-restrictions.model';
import { EventRestrictionsState } from './state/event-restrictions.state';

const compareBySid = (a: EventRestrictionListElem, b: EventRestrictionListElem): boolean => a.sid === b.sid;

@Injectable()
export class EventRestrictionsService {
    private readonly _api = inject(EventRestrictionsApi);
    private readonly _state = inject(EventRestrictionsState);

    readonly restrictions = Object.freeze({
        structure: Object.freeze({
            load: (eventId: number) => StateManager.loadIfNull(
                this._state.restrictionsStructure,
                this._api.getRestrictionsStructure(eventId)
            ),
            get$: () => this._state.restrictionsStructure.getValue$(),
            fields$: (type: EventRestrictionType) => this._state.restrictionsStructure.getValue$().pipe(
                filter(Boolean),
                map(structures => structures?.find(struct => struct.restriction_type === type)),
                map(structure => structure?.fields)
            ),
            error$: () => this._state.restrictionsStructure.getError$(),
            loading$: () => this._state.restrictionsStructure.isInProgress$()
        }),
        list: Object.freeze({
            load: (eventId: number) => StateManager.loadIfNull(
                this._state.restrictions,
                this._api.getRestrictions(eventId)
            ),
            get$: () => this._state.restrictions.getValue$(),
            error$: () => this._state.restrictions.getError$(),
            loading$: () => this._state.restrictions.isInProgress$()
        }),
        clear: () => {
            this._state.restrictions.setValue(null);
            this._state.restrictionsLoading.setValue({});
        },
        create: (eventId: number, restriction: EventRestrictionListElem) => StateManager.inProgress(
            this._state.restrictions,
            this._api.postRestriction(eventId, restriction).pipe(
                tap(response => StateManager.addElement(
                    this._state.restrictions, { ...restriction, sid: response.code, new: true }
                ))
            )
        ),
        update: (eventId: number, restriction: EventRestrictionListElem) => StateManager.inProgress(
            this._state.restrictions,
            this._api.putRestriction(eventId, restriction).pipe(
                tap(() => StateManager.updateElement(this._state.restrictions, restriction, compareBySid))
            )
        ),
        delete: (eventId: number, restriction: EventRestrictionListElem) => StateManager.inProgress(
            this._state.restrictions,
            this._api.deleteRestriction(eventId, restriction).pipe(
                tap(() => StateManager.deleteElement(this._state.restrictions, restriction, compareBySid))
            )
        ),
        load: (eventId: number, restriction: EventRestrictionListElem) => StateManager.inProgress(
            async loading => {
                const state = this._state.restrictionsLoading;
                const value = await firstValueFrom(state.getValue$());
                state.setValue({ ...value, [restriction.sid]: loading });
            },
            this._api.getRestriction(eventId, restriction.sid).pipe(
                tap(restriction => this.restrictions.set({ ...restriction, loaded: true })),
                catchError((err: HttpErrorResponse) => {
                    if (err?.status === 404) {
                        StateManager.deleteElement(this._state.restrictions, restriction, compareBySid);
                    }
                    return throwError(() => err);
                })
            )
        ),
        set: (update: EventRestrictionListElem) => {
            StateManager.updateElement(this._state.restrictions, update, compareBySid);
        },
        loading$: () => this._state.restrictionsLoading.getValue$()
    });
}
