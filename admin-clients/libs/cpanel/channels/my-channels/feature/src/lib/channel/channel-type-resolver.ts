import { Channel, ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { EMPTY, of } from 'rxjs';
import { first, switchMap } from 'rxjs/operators';

export const channelTypesResolver: ResolveFn<Channel> = (route: ActivatedRouteSnapshot) => {
    const channelsSrv = inject(ChannelsService);
    const router = inject(Router);
    const allowedChannelTypes = route.data['allowedChannelTypes'] as string[];

    return channelsSrv.getChannel$()
        .pipe(
            first(value => !!value),
            switchMap(channel => {
                if (!allowedChannelTypes.includes(channel.type)) {
                    router.navigate(['/channels', channel.id]);
                    return EMPTY;
                }
                return of(channel);
            })
        );
};
