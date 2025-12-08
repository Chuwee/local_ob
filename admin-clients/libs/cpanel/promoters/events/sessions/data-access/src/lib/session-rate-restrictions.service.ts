import { RateRestrictions, RateRestrictionsService } from '@admin-clients/cpanel/promoters/shared/data-access';
import { inject, Injectable } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { filter, first, map, Observable } from 'rxjs';
import { EventSessionsService } from './sessions.service';

@Injectable()
export class SessionRateRestrictionsService implements RateRestrictionsService {
    readonly #sessionSrv = inject(EventSessionsService);

    readonly $session = toSignal(this.#sessionSrv.session.get$().pipe(filter(Boolean)));

    ratesRestrictions = {
        load: (eventId: number): void => {
            this.#sessionSrv.session.get$().pipe(first(Boolean)).subscribe(session => {
                this.#sessionSrv.ratesRestrictions.load(eventId, session.id);
            });
        },
        get$: (): Observable<RateRestrictions[]> => this.#sessionSrv.ratesRestrictions.get$()
            .pipe(map(restrictions => (restrictions?.data))),
        clear: (): void => {
            this.#sessionSrv.ratesRestrictions.clear();
        },
        inProgress$: (): Observable<boolean> => this.#sessionSrv.ratesRestrictions.loading$(),
        update: (eventId: number, rateId: number, reqBody: Partial<RateRestrictions>): Observable<void> =>
            this.#sessionSrv.ratesRestrictions.create(eventId, this.$session()?.id, rateId, reqBody),
        delete: (eventId: number, rateId: number): Observable<void> =>
            this.#sessionSrv.ratesRestrictions.delete(eventId, this.$session()?.id, rateId)
    };
}

