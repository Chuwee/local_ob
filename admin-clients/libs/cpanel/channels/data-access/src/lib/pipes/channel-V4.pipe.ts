import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { inject, Pipe, PipeTransform } from '@angular/core';
import { BehaviorSubject, combineLatest, filter, map, Observable, shareReplay } from 'rxjs';
import { Channel, ChannelType } from '../models/_index';

@Pipe({
    name: 'isWebV4$',
    pure: true,
    standalone: true
})
export class IsWebV4$Pipe implements PipeTransform {
    readonly #entityService = inject(EntitiesBaseService);
    readonly entity$ = this.#entityService.getEntity$().pipe(
        filter(Boolean),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    readonly #channel$ = new BehaviorSubject<Channel | null>(null);

    readonly #transformValues$ = combineLatest([
        this.entity$,
        this.#channel$
    ]).pipe(
        filter(([entity, channel]) => Boolean(entity) && Boolean(channel)),
        map(([entity, channel]) =>
            channel.type === ChannelType.web &&
            (!!entity.settings.enable_v4_configs || !!channel.settings.v4_config_enabled)
        )
    );

    transform(channel: Channel): Observable<boolean> {
        this.#channel$.next(channel);
        return this.#transformValues$;
    }
}

@Pipe({
    name: 'isV3$',
    pure: true,
    standalone: true
})
export class IsV3$Pipe implements PipeTransform {
    readonly #channel$ = new BehaviorSubject<Channel>(null);
    readonly #transformValues$ = this.#channel$.pipe(
        filter(Boolean),
        map(channel => !channel.settings.v4_enabled)
    );

    transform(channel: Channel): Observable<boolean> {
        this.#channel$.next(channel);
        return this.#transformValues$;
    }
}

@Pipe({
    name: 'isV4$',
    pure: true,
    standalone: true
})
export class IsV4$Pipe implements PipeTransform {
    readonly #entityService = inject(EntitiesBaseService);
    readonly entity$ = this.#entityService.getEntity$().pipe(
        filter(Boolean),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    readonly #channel$ = new BehaviorSubject<Channel | null>(null);

    readonly #transformValues$ = combineLatest([
        this.entity$,
        this.#channel$
    ]).pipe(
        filter(([entity, channel]) => Boolean(entity) && Boolean(channel)),
        map(([entity, channel]) =>
            (!!entity.settings.enable_v4_configs || !!channel.settings.v4_config_enabled)
        )
    );

    transform(channel: Channel): Observable<boolean> {
        this.#channel$.next(channel);
        return this.#transformValues$;
    }
}
