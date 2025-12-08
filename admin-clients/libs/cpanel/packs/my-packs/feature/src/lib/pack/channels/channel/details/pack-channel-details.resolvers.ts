import { Channel, ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { PacksService } from '@admin-clients/cpanel/packs/my-packs/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

export const packChannelDetailsResolver: ResolveFn<Channel> = (route: ActivatedRouteSnapshot) => {
    const channelSrv = inject(ChannelsService);
    const packsSrv = inject(PacksService);
    const router = inject(Router);
    const breadcrumbsSrv = inject(BreadcrumbsService);
    const allRouteParams = Object.assign({}, ...route.pathFromRoot.map(path => path.params));
    const packId = allRouteParams.packId;
    const channelId = allRouteParams.channelId;

    channelSrv.clearChannel();
    channelSrv.loadChannel(channelId);
    packsSrv.pack.channel.clear();
    packsSrv.pack.channel.load(packId, channelId);

    return combineLatest([
        channelSrv.getChannel$(),
        channelSrv.getChannelError$()
    ]).pipe(
        first(([channel, channelError]) => !!(channel || channelError)),
        mergeMap(([channel, channelError]) => {
            if (channelError) {
                router.navigate(['/packs', packId, 'channels']);
                return EMPTY;
            } else {
                breadcrumbsSrv.addDynamicSegment(route.data?.['breadcrumb'], channel.name);
                return of(channel);
            }
        })
    );
};
