import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { PacksService } from '@admin-clients/cpanel/channels/packs/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';
import { Observable, of } from 'rxjs';
import { filter, first, withLatestFrom } from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class PackDetailsResolverService {
    readonly #channelsSrv = inject(ChannelsService);
    readonly #packsSrv = inject(PacksService);
    readonly #breadcrumbsService = inject(BreadcrumbsService);

    resolve(route: ActivatedRouteSnapshot): Observable<number> | Observable<never> {
        const packId = Number(route.paramMap.get('packId'));
        this.#breadcrumbsService.addDynamicSegment(route.data['breadcrumb'], 'LOADING');
        this.#packsSrv.pack.clear();
        this.#channelsSrv.getChannel$().pipe(
            first(Boolean),
            withLatestFrom(this.#packsSrv.pack.get$()),
            filter(([, pack]) => !pack || pack.id !== packId)
        ).subscribe(([channel]) => {
            this.#packsSrv.pack.load(channel.id, packId);
            this.#packsSrv.packItems.load(channel.id, packId);
        });

        this.#packsSrv.pack.get$()
            .pipe(first(pack => pack?.id === packId))
            .subscribe(pack =>
                this.#breadcrumbsService.addDynamicSegment(route.data['breadcrumb'], pack.name)
            );

        return of(packId);
    }
}
