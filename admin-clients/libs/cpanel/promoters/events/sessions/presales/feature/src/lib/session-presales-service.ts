import { ExternalProviderPresales, PromotersExternalProviderService } from '@admin-clients/cpanel/promoters/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import {
    Presale, PresalePost, PresalePut, PresalesRedirectionPolicy,
    PresalesService, SettingsLanguages
} from '@admin-clients/cpanel/shared/data-access';
import { inject, Injectable } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { map, Observable } from 'rxjs';

@Injectable()
export class SessionPresalesService implements PresalesService {

    readonly #sessionSrv = inject(EventSessionsService);
    readonly #eventsSrv = inject(EventsService);
    readonly #externalProviderSrv = inject(PromotersExternalProviderService);

    readonly $session = toSignal(this.#sessionSrv.session.get$());
    readonly $event = toSignal(this.#eventsSrv.event.get$());

    getExternalPresales$(): Observable<ExternalProviderPresales[]> {
        return this.#externalProviderSrv.providerSessionsPresales.get$();
    }

    loadExternalPresales(): void {
        this.#externalProviderSrv.providerSessionsPresales.load({
            event_id: this.$session()?.event.id,
            session_id: this.$session()?.id,
            skip_used: true
        });
    }

    clearExternalPresales(): void {
        this.#externalProviderSrv.providerSessionsPresales.clear();
    }

    externalPresalesLoading$(): Observable<boolean> {
        return this.#externalProviderSrv.providerSessionsPresales.loading$();
    }

    isLoading$(): Observable<boolean> {
        return this.#sessionSrv.presales.loading$();
    }

    get$(): Observable<Presale[]> {
        return this.#sessionSrv.presales.get$();
    }

    clear(): void {
        this.#sessionSrv.presales.clear();
    }

    load(): void {
        this.#sessionSrv.presales.load(this.$session()?.event.id, this.$session()?.id);
    }

    update(presaleId: string, reqBody?: PresalePut): Observable<void> {
        return this.#sessionSrv.presales.update(this.$session()?.event.id, this.$session()?.id, presaleId, reqBody);
    }

    create(reqBody: PresalePost): Observable<Presale> {
        return this.#sessionSrv.presales.create(this.$session()?.event.id, this.$session()?.id, reqBody);
    }

    delete(presaleId: string): Observable<void> {
        return this.#sessionSrv.presales.delete(this.$session()?.event.id, this.$session()?.id, presaleId);
    }

    getLanguages$(): Observable<SettingsLanguages> {
        return this.#eventsSrv.event.get$().pipe(map(event => event?.settings?.languages));
    }

    getRedirectionPolicy$(): Observable<PresalesRedirectionPolicy> {
        return this.#sessionSrv.session.get$().pipe(map(session => session?.settings?.presales_redirection_policy));
    }

    loadRedirectionPolicy(): void {
        return this.#sessionSrv.session.load(this.$session()?.event.id, this.$session()?.id);
    }

    updateRedirectionPolicy(reqBody: PresalesRedirectionPolicy): Observable<void> {
        return this.#sessionSrv.updateSession(this.$session()?.event.id, this.$session()?.id, {
            settings: {
                presales_redirection_policy: reqBody
            }
        });
    }

}
