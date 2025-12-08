import { ExternalProviderPresales } from '@admin-clients/cpanel/promoters/data-access';
import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';
import { Presale, PresalePost, PresalePut, PresalesRedirectionPolicy, SettingsLanguages } from './models/presale.model';

export interface PresalesService {
    get$(): Observable<Presale[]>;
    clear(): void;
    isLoading$(): Observable<boolean>;
    load(): void;
    update(presaleId: string, reqBody: PresalePut): Observable<void>;
    create(reqBody: PresalePost): Observable<Presale>;
    delete(presaleId: string): Observable<void>;

    getLanguages$(): Observable<SettingsLanguages>;
    getRedirectionPolicy$(): Observable<PresalesRedirectionPolicy>;
    loadRedirectionPolicy(): void;
    updateRedirectionPolicy(reqBody: PresalesRedirectionPolicy): Observable<void>;

    getExternalPresales$(): Observable<ExternalProviderPresales[]>;
    externalPresalesLoading$(): Observable<boolean>;
    loadExternalPresales(): void;
    clearExternalPresales(): void;
}

export const PRESALES_SERVICE = new InjectionToken<PresalesService>('PRESALES_SERVICE_TOKEN');
