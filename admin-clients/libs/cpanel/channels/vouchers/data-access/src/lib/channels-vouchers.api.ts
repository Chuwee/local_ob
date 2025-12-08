import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ChannelVouchers } from './models/channel-vouchers.model';

@Injectable()
export class ChannelsVouchersApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly CHANNELS_API = `${this.BASE_API}/mgmt-api/v1/channels`;
    private readonly VOUCHERS = '/vouchers';

    private readonly http = inject(HttpClient);

    getChannelVouchers(channelId: number): Observable<ChannelVouchers> {
        return this.http.get<ChannelVouchers>(`${this.CHANNELS_API}/${channelId}${this.VOUCHERS}`);
    }

    putChannelVouchers(channelId: number, conf: ChannelVouchers): Observable<void> {
        return this.http.put<void>(`${this.CHANNELS_API}/${channelId}${this.VOUCHERS}`, conf);
    }
}
