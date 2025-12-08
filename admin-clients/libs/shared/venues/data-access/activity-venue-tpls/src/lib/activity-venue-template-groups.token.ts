import { ActivityGroupsConfig } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';
import { SessionActivityGroupsConfig } from './models/session-activity-groups-config.model';

export interface ActivityGroupsComponentService<T extends ActivityGroupsConfig | SessionActivityGroupsConfig =
        ActivityGroupsConfig | SessionActivityGroupsConfig> {

    loadActivityGroupsConfig(
        { venueTemplateId, eventId, sessionId }: {
            venueTemplateId?: number;
            eventId?: number;
            sessionId?: number;
        }): void;

    updateActivityGroupsConfig(
        { venueTemplateId, eventId, sessionId }: {
            venueTemplateId?: number;
            eventId?: number;
            sessionId?: number;
        },
        groupsConfigs: ActivityGroupsConfig | SessionActivityGroupsConfig
    ): Observable<void>;

    clearVenueTemplateGroupsConfig();

    isActivityGroupsConfigInProgress$(): Observable<boolean>;

    getActivityGroupsConfig$(): Observable<T>;
}

export const ACTIVITY_GROUPS_SERVICE = new InjectionToken<ActivityGroupsComponentService>('ACTIVITY_GROUPS_SERVICE');
