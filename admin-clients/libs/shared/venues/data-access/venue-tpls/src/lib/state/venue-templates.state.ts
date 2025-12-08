import { ListResponse, StateProperty } from '@OneboxTM/utils-state';
import { IdName } from '@admin-clients/shared/data-access/models';
import { Injectable } from '@angular/core';
import { VenueTemplateElementInfoImage } from '../models/element-info/venue-template-element-info-image.model';
import { VenueTemplateElementInfo, VenueTemplateElementInfoDetail } from '../models/element-info/venue-template-element-info.model';
import { VenueTemplateInteractive } from '../models/interactive/venue-template-interactive.model';
import { VenueTemplateBlockingReason } from '../models/venue-template-blocking-reason.model';
import { VenueTplDynamicTagGroup, VenueTplDynamicTagGroupLabel } from '../models/venue-template-dynamic-tag-groups.model';
import { VenueTemplateGate } from '../models/venue-template-gate.model';
import { VenueTemplatePriceTypeGrouped } from '../models/venue-template-price-type-grouped.model';
import { VenueTemplatePriceType } from '../models/venue-template-price-type.model';
import { VenueTemplateQuota } from '../models/venue-template-quota.model';
import { VenueTemplate } from '../models/venue-template.model';

@Injectable({ providedIn: 'root' })
export class VenueTemplatesState {

    readonly venueTplList = new StateProperty<ListResponse<VenueTemplate>>();
    readonly venueTpl = new StateProperty<VenueTemplate>();
    readonly venueTplUpdate = new StateProperty<boolean>();
    readonly filterVenueEntitiesList = new StateProperty<IdName[]>();

    readonly venueTplElementInfoList = new StateProperty<ListResponse<VenueTemplateElementInfo>>();
    readonly venueTplElementInfo = new StateProperty<VenueTemplateElementInfoDetail>();
    readonly venueTplElementInfoImages = new StateProperty<VenueTemplateElementInfoImage[]>();
    readonly venueTplElementInfoUpdate = new StateProperty<boolean>();

    readonly priceTypes = new StateProperty<VenueTemplatePriceType[]>();
    readonly priceTypeUpdate = new StateProperty();
    readonly priceTypesGrouped = new StateProperty<VenueTemplatePriceTypeGrouped[]>();

    readonly quotas = new StateProperty<VenueTemplateQuota[]>();
    readonly quotaUpdate = new StateProperty();

    readonly blockingReasons = new StateProperty<VenueTemplateBlockingReason[]>();
    readonly blockingReasonUpdate = new StateProperty();

    readonly gates = new StateProperty<VenueTemplateGate[]>();
    readonly gateUpdate = new StateProperty();

    readonly interactiveOptions = new StateProperty<VenueTemplateInteractive>();
    readonly interactiveOptionsUpdate = new StateProperty();
    readonly interactiveFileExport = new StateProperty();

    readonly venueTplCustomTagGroups = new StateProperty<VenueTplDynamicTagGroup[]>();
    readonly venueTplCustomFirstTagGroupLabels = new StateProperty<VenueTplDynamicTagGroupLabel[]>();
    readonly venueTplCustomSecondTagGroupLabels = new StateProperty<VenueTplDynamicTagGroupLabel[]>();
}
