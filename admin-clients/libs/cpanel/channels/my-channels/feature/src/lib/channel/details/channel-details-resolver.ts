import {
    channelOperativeTypes, channelTypesPaymentMethods,
    channelWebTypes, Channel, ChannelsService
} from '@admin-clients/cpanel/channels/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { Router, ActivatedRouteSnapshot, ResolveFn } from '@angular/router';
import { EMPTY, combineLatest, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

export const channelDetailsResolver: ResolveFn<Channel> = (route: ActivatedRouteSnapshot) => {
    const channelsSrv = inject(ChannelsService);
    const breadcrumbsSrv = inject(BreadcrumbsService);
    const router = inject(Router);
    const id = route.paramMap.get('channelId');

    channelsSrv.loadChannel(id);

    return combineLatest([
        channelsSrv.getChannel$(),
        channelsSrv.getChannelError$()
    ])
        .pipe(
            first(([channel, error]) => channel !== null || error !== null),
            mergeMap(([channel, error]) => {
                if (error) {
                    router.navigate(['/channels']);
                    return EMPTY;
                }
                if (!resolveGuard(route, channel)) {
                    navigateBack(route, router);
                }
                if (route.data?.['breadcrumb']) {
                    breadcrumbsSrv.addDynamicSegment(route.data['breadcrumb'], channel.name);
                }
                return of(channel);
            })
        );
};

// guard to control if the route is available for the current channel type
function resolveGuard(route: ActivatedRouteSnapshot, channel: Channel): boolean {
    const child = route.firstChild;
    if (child) {
        const subpath = child.firstChild?.routeConfig.path;
        switch (child.routeConfig.path) {
            case 'operative':
                if (!channelOperativeTypes.includes(channel.type)) {
                    return false;
                }
                // these routes doesn't exist yet, but i leave them here as a placeholder as how to guard them
                if ((subpath === 'cross-selling' || subpath === 'blacklists') &&
                    !channelWebTypes.includes(channel.type)) {
                    return false;
                }
                break;
            case 'configuration':
                if (subpath === 'payment-methods' &&
                    !channelTypesPaymentMethods.includes(channel.type)) {
                    return false;
                }
                break;
            default:
                return true;
        }
    }
    return true;
}

// navigates to channel datail default route
function navigateBack(route: ActivatedRouteSnapshot, router: Router): void {
    let url = '';
    let parent = route.parent;

    while (parent) {
        if (parent.url[0]) {
            url = `${parent.url[0].path}/${url}`;
        }
        parent = parent.parent || null;
    }
    router.navigate([url]);
}

