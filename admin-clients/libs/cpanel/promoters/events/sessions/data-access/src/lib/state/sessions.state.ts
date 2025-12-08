import { ListResponse, StateProperty } from '@OneboxTM/utils-state';
import { SessionPacksLoadCase, SessionRefundConditions } from '@admin-clients/cpanel/promoters/events/session-packs/data-access';
import { RateRestrictions } from '@admin-clients/cpanel/promoters/shared/data-access';
import { Presale } from '@admin-clients/cpanel/shared/data-access';
import { GetPriceTypeRestricion, RestrictedPriceZones } from '@admin-clients/cpanel/venues/venue-templates/data-access';
import { AttributeWithValues } from '@admin-clients/shared/common/data-access';
import { WsMsgStatus } from '@admin-clients/shared/core/data-access';
import { IdName } from '@admin-clients/shared/data-access/models';
import { ItemCache } from '@admin-clients/shared/utility/utils';
import {
    PriceTypeAvailability, SessionActivityGroupsConfig, SessionPriceType
} from '@admin-clients/shared/venues/data-access/activity-venue-tpls';
import {
    VenueTemplateElementInfo,
    VenueTemplateElementInfoDetail, VenueTemplateElementInfoImage
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { Injectable } from '@angular/core';
import { AutomaticSaleEntry } from '../models/automatic-sale-entry.model';
import { GetSessionsResponse } from '../models/get-sessions-response.model';
import { GetInternalBarcodesResponse } from '../models/internal-barcode.model';
import { LinkedSession } from '../models/linked-session.model';
import { SaleConstraints } from '../models/sale-constraints.model';
import { SessionAdditionalConfig } from '../models/session-additional-config.model';
import { GetExternalBarcodesResponse } from '../models/session-barcode-to-import.model';
import { GetSessionZoneDynamicPricesResponse, GetSessionDynamicPricesResponse } from '../models/session-dynamic-prices.model';
import { SessionExternalBarcodes } from '../models/session-external-barcodes.model';
import { SessionExternalSessionsConfig } from '../models/session-external-sessions-config.model';
import { SessionLoyaltyPoints } from '../models/session-loyalty-points.model';
import { SessionQuotaCapacity } from '../models/session-quota-capacity.model';
import { SessionRate } from '../models/session-rate.model';
import { SessionTiersAvailability } from '../models/session-tiers-availability.model';
import { Session } from '../models/session.model';
import { SessionsGroup } from '../models/sessions-group.model';

@Injectable()
export class EventSessionsState {
    readonly sessionList = new StateProperty<GetSessionsResponse>();
    readonly sessionsCache = new ItemCache<IdName>();
    readonly allSessions = new StateProperty<GetSessionsResponse>();
    readonly allSessionsReducedModel = new StateProperty<GetSessionsResponse>();
    readonly sessionsGroups = new StateProperty<SessionsGroup[]>();
    readonly listDetailsState = new StateProperty(SessionPacksLoadCase.none);
    readonly session = new StateProperty<Session>();
    readonly savingSession = new StateProperty<void>();
    readonly venueMapSaving = new StateProperty<void>();
    readonly pendingCapacityUpdates = new StateProperty(new Map<number, WsMsgStatus>());
    readonly updatingSessions = new StateProperty<void>();
    readonly deletingSessions = new StateProperty<void>();
    readonly linkedSessions = new StateProperty<LinkedSession[]>();
    readonly sessionAdditionalConfig = new StateProperty<SessionAdditionalConfig>();
    readonly creatingSessions = new StateProperty<void>();
    readonly automaticSales = new StateProperty<AutomaticSaleEntry[]>();
    readonly saleConstraints = new StateProperty<SaleConstraints>();
    readonly cartLimit = new StateProperty<void>();
    readonly priceTypeLimit = new StateProperty<void>();
    readonly presales = new StateProperty<Presale[]>();
    readonly sessionTiersAvailability = new StateProperty<SessionTiersAvailability[]>();
    readonly sessionAttributes = new StateProperty<AttributeWithValues[]>();
    readonly quotaCapacities = new StateProperty<SessionQuotaCapacity[]>();
    readonly refreshExternalAvailability = new StateProperty();
    readonly refreshExternalMembershipInventory = new StateProperty();
    readonly priceTypeAvailability = new StateProperty<PriceTypeAvailability[]>();
    readonly priceTypes = new StateProperty<SessionPriceType[]>();
    readonly savingPriceTypes = new StateProperty<void>();
    readonly activityGroupsConfig = new StateProperty<SessionActivityGroupsConfig>();
    readonly whiteList = new StateProperty<GetInternalBarcodesResponse>();
    readonly exportWhitelist = new StateProperty<void>();
    readonly exportExternalBarcodes = new StateProperty<void>();
    readonly refundConditions = new StateProperty<SessionRefundConditions>();
    readonly templateInUse = new StateProperty<boolean>();
    readonly restrictedPriceTypes = new StateProperty<RestrictedPriceZones>();
    readonly priceTypeRestriction = new StateProperty<GetPriceTypeRestricion>();
    readonly importBarcodesReference = new StateProperty<number>();
    readonly uploadedExternalBarcodes = new StateProperty<GetExternalBarcodesResponse>();
    readonly sessionExternalBarcodes = new StateProperty<SessionExternalBarcodes>();
    readonly sessionExternalBarcodesSaving = new StateProperty<void>();
    readonly sessionExternalSessionsConfig = new StateProperty<SessionExternalSessionsConfig>();
    readonly sessionExternalSessionsConfigSaving = new StateProperty<void>();
    readonly exportCapacity = new StateProperty<void>();
    readonly rates = new StateProperty<SessionRate[]>(); // AVET
    readonly ratesRestrictions = new StateProperty<ListResponse<RateRestrictions>>();
    readonly venueTplElementInfoList = new StateProperty<ListResponse<VenueTemplateElementInfo>>();
    readonly venueTplElementInfo = new StateProperty<VenueTemplateElementInfoDetail>();
    readonly venueTplElementInfoImages = new StateProperty<VenueTemplateElementInfoImage[]>();
    readonly loyaltyPoints = new StateProperty<SessionLoyaltyPoints>();
    readonly mapping = new StateProperty<void>();
    readonly dynamicPrices = new StateProperty<GetSessionDynamicPricesResponse>();
    readonly zoneDynamicPrices = new StateProperty<GetSessionZoneDynamicPricesResponse>();
}
