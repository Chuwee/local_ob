import { inject, Injectable } from '@angular/core';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChannelLoyaltyPoints } from './models/channel-loyalty-points.model';

@Injectable({ providedIn: 'root' })
export class ChannelsLoyaltyPointsApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly CHANNELS_API = `${this.BASE_API}/mgmt-api/v1/channels`;
    private readonly LOYALTY_POINTS = '/loyalty-points';

    private readonly http = inject(HttpClient);

    getChannelLoyaltyPoints(channelId: number): Observable<ChannelLoyaltyPoints> {
        return this.http.get<ChannelLoyaltyPoints>(`${this.CHANNELS_API}/${channelId}${this.LOYALTY_POINTS}`);
    }

    putChannelLoyaltyPoints(channelId: number, conf: ChannelLoyaltyPoints): Observable<void> {
        return this.http.put<void>(`${this.CHANNELS_API}/${channelId}${this.LOYALTY_POINTS}`, conf);
    }
}
