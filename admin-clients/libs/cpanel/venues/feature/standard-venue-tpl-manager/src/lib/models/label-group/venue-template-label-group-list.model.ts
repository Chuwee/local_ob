import {
    Accessibility, SeatLinkable, SeatLinked, SeatStatus, Visibility
} from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplateType } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { SafeHtml } from '@angular/platform-browser';
import { VenueTemplateEditorType } from '../venue-template-editor-type.model';
import { VenueTemplateIcons } from '../venue-template-icons';
import { VenueTemplateLabelGroupType } from './venue-template-label-group-type.enum';

export interface VenueTemplateLabelGroup {
    id: VenueTemplateLabelGroupType;
    customGroupId?: number;
    code?: string; // revisar
    literalKey: string;
    customName?: string;
    labels?: VenueTemplateLabel[];
    sorted?: boolean;
    visible?: boolean;
    disabled?: boolean;
    addable?: boolean;
    sortable?: boolean;
    editable?: boolean;
    editableGroup?: boolean;
    deletable?: boolean;
    deletableGroup?: boolean;
    nnzPartialApply?: boolean;
    isLinkableGroup?: boolean;
    selectedByDefault: boolean;
    custom?: boolean;
}

export interface VenueTemplateLabel {
    id: string;
    labelGroupId: VenueTemplateLabelGroupType;
    literal?: string;
    literalKey?: string;
    tooltip?: string;
    color?: string;
    icon?: string;
    safeIcon?: SafeHtml;
    code?: string;
    visible?: boolean;
    disabled?: boolean;
    editableStatus?: boolean;
    editable?: boolean;
    deletable?: boolean;
    default?: boolean;
    // This propety defines if a seat could edit this properties (groups) of the seats in this state, "editable" property override this one
    notModifiableGroups?: VenueTemplateLabelGroupType[];
    count?: number;
    noFilter?: boolean;
    filtering?: boolean;
}

export function generateCustomVenueTemplateLabelGroup(
    id: VenueTemplateLabelGroupType, code: string, customGroupId: number, customName: string
): VenueTemplateLabelGroup {
    return {
        id, code, customGroupId, customName,
        literalKey: undefined,
        selectedByDefault: false,
        addable: true,
        deletable: true,
        deletableGroup: true,
        disabled: false,
        visible: true,
        editableGroup: true,
        editable: true,
        custom: true
    };
}

export class VenueTemplateLabelGroupList extends Array<VenueTemplateLabelGroup> {
    constructor(editorType: VenueTemplateEditorType, templateType: VenueTemplateType, unrestrictedPack: boolean, isSga: boolean) {
        const isAvetOrSga = templateType === VenueTemplateType.avet || isSga;
        const labelGroups: VenueTemplateLabelGroup[] = [
            {
                id: VenueTemplateLabelGroupType.seasonTicketLinkable,
                literalKey: 'VENUE_TPL_MGR.FORMS.LABELS.SEASON_TICKET',
                visible: [
                    VenueTemplateEditorType.seasonTicketTemplate
                ].includes(editorType),
                selectedByDefault: [
                    VenueTemplateEditorType.seasonTicketTemplate
                ].includes(editorType),
                isLinkableGroup: true,
                labels: [
                    {
                        id: SeatLinkable.linkable,
                        labelGroupId: VenueTemplateLabelGroupType.seasonTicketLinkable,
                        literalKey: 'VENUE_TPL_MGR.FORMS.LABELS.LINKED',
                        color: '#00CCFF',
                        count: 0
                    },
                    {
                        id: SeatLinkable.notLinkable,
                        labelGroupId: VenueTemplateLabelGroupType.seasonTicketLinkable,
                        literalKey: 'VENUE_TPL_MGR.FORMS.LABELS.UNLINKED',
                        color: '#FFFFFF',
                        count: 0
                    },
                    {
                        id: SeatLinkable.notAssignable,
                        labelGroupId: VenueTemplateLabelGroupType.seasonTicketLinkable,
                        literalKey: 'VENUE_TPL_MGR.FORMS.LABELS.NOT_ASSIGNABLE',
                        color: '#a4b8cd',
                        count: 0,
                        disabled: true
                    }
                ]
            },
            {
                id: VenueTemplateLabelGroupType.sessionPackLink,
                literalKey: 'VENUE_TPL_MGR.FORMS.LABELS.SESSION_PACK',
                visible: [
                    VenueTemplateEditorType.sessionPackTemplate
                ].includes(editorType),
                selectedByDefault: [
                    VenueTemplateEditorType.sessionPackTemplate
                ].includes(editorType),
                isLinkableGroup: true,
                labels: [
                    {
                        id: SeatLinked.linked,
                        labelGroupId: VenueTemplateLabelGroupType.sessionPackLink,
                        literalKey: 'VENUE_TPL_MGR.FORMS.LABELS.SEASON_TICKET',
                        tooltip: 'VENUE_TPL_MGR.ACTIONS.LINK',
                        color: '#00CCFF'
                    },
                    {
                        id: SeatLinked.unlinked,
                        labelGroupId: VenueTemplateLabelGroupType.sessionPackLink,
                        literalKey: 'VENUE_TPL_MGR.FORMS.LABELS.UNLINKED',
                        tooltip: 'VENUE_TPL_MGR.ACTIONS.UNLINK',
                        color: '#FFFFFF'
                    }
                ]
            },
            {
                id: VenueTemplateLabelGroupType.state,
                literalKey: 'VENUE_TPLS.STATUS',
                selectedByDefault: [
                    VenueTemplateEditorType.venueTemplate,
                    VenueTemplateEditorType.promoterTemplate,
                    VenueTemplateEditorType.eventTemplate,
                    VenueTemplateEditorType.sessionTemplate,
                    VenueTemplateEditorType.multiSessionTemplate,
                    VenueTemplateEditorType.seasonTicketTemplate
                ].includes(editorType),
                nnzPartialApply: [
                    VenueTemplateEditorType.sessionTemplate,
                    VenueTemplateEditorType.sessionPackTemplate,
                    VenueTemplateEditorType.seasonTicketTemplate
                ].includes(editorType),
                labels: [
                    {
                        id: SeatStatus.free,
                        labelGroupId: VenueTemplateLabelGroupType.state,
                        literalKey: 'ENUMS.SEAT_STATUS_PLURAL.FREE',
                        color: '#8bc34a',
                        icon: VenueTemplateIcons.free.icon,
                        disabled: editorType === VenueTemplateEditorType.seasonTicketTemplate || unrestrictedPack,
                        editableStatus: true,
                        count: 0
                    },
                    {
                        id: SeatStatus.kill,
                        labelGroupId: VenueTemplateLabelGroupType.state,
                        literalKey: 'ENUMS.SEAT_STATUS_PLURAL.KILL',
                        color: '#000000',
                        icon: VenueTemplateIcons.kill.icon,
                        visible: !isAvetOrSga,
                        disabled: editorType === VenueTemplateEditorType.seasonTicketTemplate || unrestrictedPack,
                        editableStatus: true,
                        count: 0
                    },
                    {
                        id: SeatStatus.promotorLocked,
                        labelGroupId: VenueTemplateLabelGroupType.state,
                        literalKey: 'ENUMS.SEAT_STATUS_PLURAL.LOCKED',
                        color: '#FFFFFF',
                        icon: VenueTemplateIcons.lock.icon,
                        disabled: true,
                        editableStatus: true,
                        visible:
                            [
                                VenueTemplateEditorType.promoterTemplate,
                                VenueTemplateEditorType.eventTemplate,
                                VenueTemplateEditorType.sessionTemplate,
                                VenueTemplateEditorType.sessionPackTemplate,
                                VenueTemplateEditorType.multiSessionTemplate,
                                VenueTemplateEditorType.seasonTicketTemplate
                            ].includes(editorType),
                        count: 0
                    },
                    {
                        id: SeatStatus.sold,
                        labelGroupId: VenueTemplateLabelGroupType.state,
                        literalKey: 'ENUMS.SEAT_STATUS_PLURAL.SOLD',
                        color: '#F99335',
                        icon: VenueTemplateIcons.sold.icon,
                        disabled: true,
                        editableStatus: false,
                        notModifiableGroups: [
                            VenueTemplateEditorType.sessionTemplate,
                            VenueTemplateEditorType.sessionPackTemplate,
                            VenueTemplateEditorType.seasonTicketTemplate
                        ].includes(editorType) ?
                            [
                                VenueTemplateLabelGroupType.state,
                                VenueTemplateLabelGroupType.blockingReason,
                                VenueTemplateLabelGroupType.accessibility,
                                VenueTemplateLabelGroupType.priceType,
                                VenueTemplateLabelGroupType.quota,
                                VenueTemplateLabelGroupType.visibility
                            ] : undefined,
                        visible: [
                            VenueTemplateEditorType.sessionTemplate,
                            VenueTemplateEditorType.sessionPackTemplate,
                            VenueTemplateEditorType.seasonTicketTemplate
                        ].includes(editorType),
                        count: 0
                    },
                    {
                        id: SeatStatus.emitted,
                        labelGroupId: VenueTemplateLabelGroupType.state,
                        literalKey: 'ENUMS.SEAT_STATUS_PLURAL.EMITTED',
                        color: '#6AD1E8',
                        icon: VenueTemplateIcons.sold.icon,
                        disabled: true,
                        editableStatus: false,
                        notModifiableGroups: [
                            VenueTemplateEditorType.sessionTemplate,
                            VenueTemplateEditorType.sessionPackTemplate,
                            VenueTemplateEditorType.seasonTicketTemplate
                        ].includes(editorType) ?
                            [
                                VenueTemplateLabelGroupType.state,
                                VenueTemplateLabelGroupType.blockingReason,
                                VenueTemplateLabelGroupType.accessibility,
                                VenueTemplateLabelGroupType.priceType,
                                VenueTemplateLabelGroupType.quota,
                                VenueTemplateLabelGroupType.visibility
                            ] : undefined,
                        visible: [
                            VenueTemplateEditorType.sessionTemplate,
                            VenueTemplateEditorType.sessionPackTemplate,
                            VenueTemplateEditorType.seasonTicketTemplate
                        ].includes(editorType),
                        count: 0
                    },
                    {
                        id: SeatStatus.gift,
                        labelGroupId: VenueTemplateLabelGroupType.state,
                        literalKey: 'ENUMS.SEAT_STATUS_PLURAL.GIFT',
                        color: '#F0F288',
                        icon: VenueTemplateIcons.sold.icon,
                        disabled: true,
                        editableStatus: false,
                        notModifiableGroups: [
                            VenueTemplateEditorType.sessionTemplate,
                            VenueTemplateEditorType.sessionPackTemplate,
                            VenueTemplateEditorType.seasonTicketTemplate
                        ].includes(editorType) ?
                            [
                                VenueTemplateLabelGroupType.state,
                                VenueTemplateLabelGroupType.blockingReason,
                                VenueTemplateLabelGroupType.accessibility,
                                VenueTemplateLabelGroupType.priceType,
                                VenueTemplateLabelGroupType.quota,
                                VenueTemplateLabelGroupType.visibility
                            ] : undefined,
                        visible: [
                            VenueTemplateEditorType.sessionTemplate,
                            VenueTemplateEditorType.sessionPackTemplate,
                            VenueTemplateEditorType.seasonTicketTemplate
                        ].includes(editorType),
                        count: 0
                    },
                    {
                        id: SeatStatus.systemLocked,
                        labelGroupId: VenueTemplateLabelGroupType.state,
                        literalKey: 'ENUMS.SEAT_STATUS_PLURAL.SYSTEM_LOCKED',
                        color: '#FFCB69',
                        icon: VenueTemplateIcons.systemLocked.icon,
                        disabled: true,
                        editableStatus: false,
                        visible: [
                            VenueTemplateEditorType.sessionTemplate,
                            VenueTemplateEditorType.sessionPackTemplate,
                            VenueTemplateEditorType.seasonTicketTemplate
                        ].includes(editorType),
                        count: 0
                    },
                    {
                        id: SeatStatus.presoldLocked,
                        labelGroupId: VenueTemplateLabelGroupType.state,
                        literalKey: 'ENUMS.SEAT_STATUS_PLURAL.PRESOLD_LOCKED',
                        color: '#FFCB69',
                        icon: VenueTemplateIcons.systemLocked.icon,
                        disabled: true,
                        editableStatus: false,
                        visible: [
                            VenueTemplateEditorType.sessionTemplate,
                            VenueTemplateEditorType.sessionPackTemplate,
                            VenueTemplateEditorType.seasonTicketTemplate
                        ].includes(editorType),
                        count: 0
                    },
                    {
                        id: SeatStatus.externalLocked,
                        labelGroupId: VenueTemplateLabelGroupType.state,
                        literalKey: 'ENUMS.SEAT_STATUS_PLURAL.EXTERNAL_LOCKED',
                        color: '#FF6600',
                        icon: VenueTemplateIcons.externalLocked.icon,
                        disabled: true,
                        notModifiableGroups: [
                            VenueTemplateLabelGroupType.state,
                            VenueTemplateLabelGroupType.blockingReason,
                            VenueTemplateLabelGroupType.priceType
                        ],
                        visible: templateType === VenueTemplateType.avet && editorType === VenueTemplateEditorType.sessionTemplate,
                        count: 0
                    },
                    {
                        id: SeatStatus.booked,
                        labelGroupId: VenueTemplateLabelGroupType.state,
                        literalKey: 'ENUMS.SEAT_STATUS_PLURAL.BOOKED',
                        color: '#B19AF1',
                        icon: VenueTemplateIcons.booked.icon,
                        disabled: true,
                        editableStatus: false,
                        notModifiableGroups: [
                            VenueTemplateEditorType.sessionTemplate,
                            VenueTemplateEditorType.sessionPackTemplate,
                            VenueTemplateEditorType.seasonTicketTemplate
                        ].includes(editorType) ?
                            [
                                VenueTemplateLabelGroupType.state,
                                VenueTemplateLabelGroupType.blockingReason,
                                VenueTemplateLabelGroupType.accessibility,
                                VenueTemplateLabelGroupType.priceType,
                                VenueTemplateLabelGroupType.quota,
                                VenueTemplateLabelGroupType.visibility
                            ] : undefined,
                        visible: !isAvetOrSga && [
                            VenueTemplateEditorType.sessionTemplate,
                            VenueTemplateEditorType.sessionPackTemplate,
                            VenueTemplateEditorType.seasonTicketTemplate
                        ].includes(editorType),
                        count: 0
                    },
                    {
                        id: SeatStatus.seasonLocked,
                        labelGroupId: VenueTemplateLabelGroupType.state,
                        literalKey: 'ENUMS.SEAT_STATUS_PLURAL.NOT_AVAILABLE',
                        disabled: true,
                        editableStatus: false,
                        color: '#F57EA2',
                        notModifiableGroups:
                            (editorType === VenueTemplateEditorType.seasonTicketTemplate
                                && [VenueTemplateLabelGroupType.state, VenueTemplateLabelGroupType.blockingReason])
                            || (editorType === VenueTemplateEditorType.sessionPackTemplate
                                && [
                                    VenueTemplateLabelGroupType.state,
                                    VenueTemplateLabelGroupType.blockingReason,
                                    VenueTemplateLabelGroupType.priceType,
                                    VenueTemplateLabelGroupType.quota,
                                    VenueTemplateLabelGroupType.accessibility,
                                    VenueTemplateLabelGroupType.visibility
                                ])
                            || undefined,
                        icon: VenueTemplateIcons.seasonLocked.icon,
                        visible: templateType !== VenueTemplateType.avet
                            && [
                                VenueTemplateEditorType.sessionPackTemplate,
                                VenueTemplateEditorType.seasonTicketTemplate
                            ].includes(editorType),
                        count: 0
                    },
                    {
                        id: SeatStatus.seasonLocked,
                        labelGroupId: VenueTemplateLabelGroupType.state,
                        literalKey: 'ENUMS.SEAT_STATUS_PLURAL.SEASON_LOCKED',
                        disabled: true,
                        editableStatus: false,
                        color: '#F57EA2',
                        icon: VenueTemplateIcons.seasonLocked.icon,
                        notModifiableGroups: [
                            VenueTemplateLabelGroupType.state,
                            VenueTemplateLabelGroupType.blockingReason,
                            VenueTemplateLabelGroupType.accessibility,
                            VenueTemplateLabelGroupType.priceType,
                            VenueTemplateLabelGroupType.quota,
                            VenueTemplateLabelGroupType.visibility
                        ],
                        visible: templateType !== VenueTemplateType.avet && editorType === VenueTemplateEditorType.sessionTemplate,
                        count: 0
                    }
                ]
            },
            {
                id: VenueTemplateLabelGroupType.blockingReason,
                literalKey: 'VENUE_TPLS.BLOCKING_REASON',
                selectedByDefault: false,
                addable: !isAvetOrSga,
                sortable: true,
                deletable: true,
                editable: true,
                nnzPartialApply: [
                    VenueTemplateEditorType.sessionTemplate,
                    VenueTemplateEditorType.sessionPackTemplate,
                    VenueTemplateEditorType.seasonTicketTemplate
                ].includes(editorType),
                visible: [
                    VenueTemplateEditorType.promoterTemplate,
                    VenueTemplateEditorType.eventTemplate,
                    VenueTemplateEditorType.sessionTemplate,
                    VenueTemplateEditorType.sessionPackTemplate,
                    VenueTemplateEditorType.multiSessionTemplate,
                    VenueTemplateEditorType.seasonTicketTemplate
                ].includes(editorType),
                disabled: unrestrictedPack || editorType === VenueTemplateEditorType.seasonTicketTemplate
            },
            {
                id: VenueTemplateLabelGroupType.priceType,
                literalKey: 'VENUE_TPLS.PRICE_TYPE',
                selectedByDefault: false,
                sortable: true,
                addable: !isAvetOrSga,
                editable: !isAvetOrSga,
                disabled: isAvetOrSga,
                deletable: [
                    VenueTemplateEditorType.promoterTemplate,
                    VenueTemplateEditorType.eventTemplate
                ].includes(editorType),
                visible: [
                    VenueTemplateEditorType.promoterTemplate,
                    VenueTemplateEditorType.eventTemplate,
                    VenueTemplateEditorType.sessionTemplate,
                    VenueTemplateEditorType.sessionPackTemplate,
                    VenueTemplateEditorType.multiSessionTemplate,
                    VenueTemplateEditorType.seasonTicketTemplate
                ].includes(editorType)
            },
            {
                id: VenueTemplateLabelGroupType.quota,
                literalKey: 'VENUE_TPLS.FORMS.LABELS.QUOTA',
                selectedByDefault: false,
                addable: true,
                sortable: true,
                editable: true,
                deletable: true,
                nnzPartialApply: [
                    VenueTemplateEditorType.promoterTemplate,
                    VenueTemplateEditorType.eventTemplate,
                    VenueTemplateEditorType.sessionTemplate,
                    VenueTemplateEditorType.sessionPackTemplate,
                    VenueTemplateEditorType.seasonTicketTemplate
                ].includes(editorType),
                visible: [
                    VenueTemplateEditorType.promoterTemplate,
                    VenueTemplateEditorType.eventTemplate,
                    VenueTemplateEditorType.sessionTemplate,
                    VenueTemplateEditorType.sessionPackTemplate,
                    VenueTemplateEditorType.multiSessionTemplate,
                    VenueTemplateEditorType.seasonTicketTemplate
                ].includes(editorType)
            },
            {
                id: VenueTemplateLabelGroupType.visibility,
                literalKey: 'VENUE_TPLS.VISIBILITY',
                selectedByDefault: false,
                visible: true,
                labels: [
                    {
                        id: Visibility.full,
                        labelGroupId: VenueTemplateLabelGroupType.visibility,
                        literalKey: 'VENUE_TPLS.SEAT_VISIBILITY.NORMAL',
                        color: '#FFFFFF',
                        visible: true,
                        count: 0
                    },
                    {
                        id: Visibility.side,
                        labelGroupId: VenueTemplateLabelGroupType.visibility,
                        literalKey: 'VENUE_TPLS.SEAT_VISIBILITY.SIDE',
                        color: '#ffc107',
                        visible: true,
                        count: 0
                    },
                    {
                        id: Visibility.partial,
                        labelGroupId: VenueTemplateLabelGroupType.visibility,
                        literalKey: 'VENUE_TPLS.SEAT_VISIBILITY.LOW',
                        color: '#944137',
                        visible: true,
                        count: 0
                    },
                    {
                        id: Visibility.none,
                        labelGroupId: VenueTemplateLabelGroupType.visibility,
                        literalKey: 'VENUE_TPLS.SEAT_VISIBILITY.NULL',
                        color: '#607d8b',
                        visible: true,
                        count: 0
                    }
                ]
            },
            {
                id: VenueTemplateLabelGroupType.accessibility,
                literalKey: 'VENUE_TPLS.ACCESSIBILITY',
                selectedByDefault: false,
                visible: true,
                labels: [
                    {
                        id: Accessibility.normal,
                        labelGroupId: VenueTemplateLabelGroupType.accessibility,
                        literalKey: 'VENUE_TPLS.SEAT_ACCESSIBILITY.NORMAL',
                        color: '#FFFFFF',
                        visible: true,
                        count: 0
                    },
                    {
                        id: Accessibility.reducedMobility,
                        labelGroupId: VenueTemplateLabelGroupType.accessibility,
                        literalKey: 'VENUE_TPLS.SEAT_ACCESSIBILITY.LOW_MOBILITY',
                        color: '#944137',
                        visible: true,
                        count: 0
                    }
                ]
            },
            {
                id: VenueTemplateLabelGroupType.sessionPacks,
                literalKey: 'EVENTS.SESSION_PACKS',
                selectedByDefault: false,
                disabled: true,
                deletable: false,
                visible: editorType === VenueTemplateEditorType.sessionTemplate
            },
            {
                id: VenueTemplateLabelGroupType.gate,
                literalKey: 'VENUE_TPLS.GATE',
                selectedByDefault: false,
                editable: true,
                addable: true,
                sortable: true,
                deletable: ![
                    VenueTemplateEditorType.sessionTemplate,
                    VenueTemplateEditorType.sessionPackTemplate,
                    VenueTemplateEditorType.multiSessionTemplate,
                    VenueTemplateEditorType.seasonTicketTemplate
                ].includes(editorType),
                visible: [
                    VenueTemplateEditorType.venueTemplate,
                    VenueTemplateEditorType.promoterTemplate,
                    VenueTemplateEditorType.eventTemplate,
                    VenueTemplateEditorType.sessionTemplate,
                    VenueTemplateEditorType.sessionPackTemplate,
                    VenueTemplateEditorType.multiSessionTemplate,
                    VenueTemplateEditorType.seasonTicketTemplate
                ].includes(editorType)
            }
        ];
        labelGroups.forEach(labelGroup =>
            labelGroup.labels = labelGroup.labels?.filter(label => label.visible === undefined || label.visible)
        );
        super(...labelGroups.filter(labelGroup => labelGroup.visible === undefined || labelGroup.visible));
    }
}
