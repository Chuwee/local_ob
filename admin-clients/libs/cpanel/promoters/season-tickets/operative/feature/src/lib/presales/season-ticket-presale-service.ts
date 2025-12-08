import { ExternalProviderPresales, PromotersExternalProviderService } from '@admin-clients/cpanel/promoters/data-access';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    Presale, PresalePost, PresalePut, PresalesRedirectionPolicy,
    PresalesService, SettingsLanguages, ValidatorTypes
} from '@admin-clients/cpanel/shared/data-access';
import { inject, Injectable } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { map, Observable } from 'rxjs';

@Injectable()
export class SeasonTicketPresalesService implements PresalesService {

    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #externalProviderSrv = inject(PromotersExternalProviderService);

    readonly $seasonTicket = toSignal(this.#seasonTicketSrv.seasonTicket.get$());

    getExternalPresales$(): Observable<ExternalProviderPresales[]> {
        return this.#externalProviderSrv.providerSeasonTicketsPresales.get$();
    }

    loadExternalPresales(): void {
        this.#externalProviderSrv.providerSeasonTicketsPresales.load(this.$seasonTicket()?.id, true);
    }

    clearExternalPresales(): void {
        this.#externalProviderSrv.providerSeasonTicketsPresales.clear();
    }

    externalPresalesLoading$(): Observable<boolean> {
        return this.#externalProviderSrv.providerSeasonTicketsPresales.loading$();
    }

    isLoading$(): Observable<boolean> {
        return this.#seasonTicketSrv.seasonTicketPresales.loading$();
    }

    get$(): Observable<Presale[]> {
        return this.#seasonTicketSrv.seasonTicketPresales.get$().pipe(
            map(presales => presales?.map(pr => ({ ...pr, validator_type: ValidatorTypes.customers })))
        );
    }

    clear(): void {
        this.#seasonTicketSrv.seasonTicketPresales.clear();
    }

    load(): void {
        this.#seasonTicketSrv.seasonTicketPresales.load(this.$seasonTicket()?.id);
    }

    update(presaleId: string, reqBody?: PresalePut): Observable<void> {
        return this.#seasonTicketSrv.seasonTicketPresales.update(this.$seasonTicket()?.id, presaleId, reqBody);
    }

    create(reqBody: PresalePost): Observable<Presale> {
        return this.#seasonTicketSrv.seasonTicketPresales.create(this.$seasonTicket()?.id, reqBody);
    }

    delete(presaleId: string): Observable<void> {
        return this.#seasonTicketSrv.seasonTicketPresales.delete(this.$seasonTicket()?.id, presaleId);
    }

    getLanguages$(): Observable<SettingsLanguages> {
        return this.#seasonTicketSrv.seasonTicket.get$().pipe(map(seasonTicket => seasonTicket?.settings?.languages));
    }

    getRedirectionPolicy$(): Observable<PresalesRedirectionPolicy> {
        return this.#seasonTicketSrv.seasonTicket.get$().pipe(map(seasonTicket => seasonTicket?.settings?.presales_redirection_policy));
    }

    loadRedirectionPolicy(): void {
        return this.#seasonTicketSrv.seasonTicket.load(this.$seasonTicket()?.id.toString());
    }

    updateRedirectionPolicy(reqBody: PresalesRedirectionPolicy): Observable<void> {
        return this.#seasonTicketSrv.seasonTicket.save(this.$seasonTicket()?.id.toString(), {
            settings: {
                presales_redirection_policy: reqBody
            }
        });
    }
}
