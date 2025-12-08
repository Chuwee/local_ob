import { Metadata } from '@OneboxTM/utils-state';
import { ElementsInfoFilterRequest, PostVenueTemplateElementInfoRequest, PutVenueTemplateElementInfoRequest, VenueTemplateElementInfo, VenueTemplateElementInfoDetail, VenueTemplateElementInfoImage, VenueTemplateElementInfoType } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';

export interface ElementsInfoService {
    venueTplsElementInfo: {
        load(id: number, filters: Partial<ElementsInfoFilterRequest>): void;
        loadAll(id: number, filters: Partial<ElementsInfoFilterRequest>): void;
        getData$(): Observable<VenueTemplateElementInfo[]>;
        getMetadata$(): Observable<Metadata>;
        inProgress$(): Observable<boolean>;
        clear(): void;
    };
    venueTplElementInfo: {
        load(id: number, elementInfoId: number, type: VenueTemplateElementInfoType): void;
        create(id: number, elementInfo: PostVenueTemplateElementInfoRequest): Observable<void>;
        update(id: number, elementInfoId: number, type: VenueTemplateElementInfoType, elementInfo: PutVenueTemplateElementInfoRequest): Observable<void>;
        changeStatus(id: number, elementInfoId: number, type: VenueTemplateElementInfoType, status: 'ENABLED' | 'DISABLED', filter?: ElementsInfoFilterRequest): Observable<void>;
        updateMultiple(id: number, elements: VenueTemplateElementInfo[], elementInfo: PutVenueTemplateElementInfoRequest, all?: boolean, filters?: ElementsInfoFilterRequest): Observable<void>;
        delete(id: number, elements: string[], all: boolean, filter?: ElementsInfoFilterRequest): Observable<void>;
        recoverInheritance(id: number, elementInfoId: number, type: VenueTemplateElementInfoType): Observable<void>;
        inProgress$(): Observable<boolean>;
        get$(): Observable<VenueTemplateElementInfoDetail>;
        clear(): void;
    };
    venueTplElementInfoImages: {
        delete(id: number, elementInfoId: number, type: VenueTemplateElementInfoType, imagesToDelete: VenueTemplateElementInfoImage[]): Observable<void>;
        inProgress$(): Observable<boolean>;
        clear(): void;
    };

}

export const ELEMENTS_INFO_SERVICE = new InjectionToken<ElementsInfoService>('ELEMENTS_INFO_SERVICE_TOKEN');
