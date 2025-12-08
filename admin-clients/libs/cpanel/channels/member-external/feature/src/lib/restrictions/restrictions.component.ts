import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { ChannelMemberExternalService, Restriction, RestrictionListElem } from '@admin-clients/cpanel-channels-member-external-data-access';
import { MessageDialogService, EphemeralMessageService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { StdVenueTplService, StdVenueTplsState } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { filter, first, map, switchMap, takeUntil, tap } from 'rxjs/operators';
import { MemberExternalRestrictionDialogComponent as RestrictionDialog } from './dialog/restriction-dialog.component';

@Component({
    selector: 'app-channel-member-restrictions',
    templateUrl: './restrictions.component.html',
    styleUrls: ['./restrictions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [StdVenueTplsState, StdVenueTplService],
    standalone: false
})
export class MemberExternalRestrictionsComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #channelsService = inject(ChannelsService);
    readonly #memberExtSrv = inject(ChannelMemberExternalService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #venueTplService = inject(StdVenueTplService);
    readonly #dialog = inject(MatDialog);
    readonly #onDestroy = new Subject<void>();
    #channelId: number;

    form = new FormGroup({});

    readonly loadingList$ = this.#memberExtSrv.restrictions.list.loading$();

    readonly restrictionLoading$ = this.#memberExtSrv.restrictions.loading$();

    readonly sectors$ = this.#venueTplService.getSectors$();

    readonly mainCapacity$ = this.#memberExtSrv.channelCapacities.get$().pipe(
        first(value => !!value),
        map(capacities => capacities?.find(elem => elem.main))
    );

    readonly restrictions$ = this.#memberExtSrv.restrictions.list.get$().pipe(
        tap(restrictions => {
            restrictions?.forEach(r => this.form.addControl(r.sid, new FormGroup({})));
        })
    );

    ngOnInit(): void {
        this.#memberExtSrv.restrictions.structure.load();

        this.#channelsService.getChannel$().pipe(
            first(channel => !!channel),
            takeUntil(this.#onDestroy)
        ).subscribe(channel => {
            this.#channelId = channel.id;
            this.loadList();
        });

        this.mainCapacity$.pipe(
            takeUntil(this.#onDestroy)
        ).subscribe(capacity => {
            capacity?.venue_template_id && this.#venueTplService.loadSectors(capacity.venue_template_id);
        });
    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
        this.#memberExtSrv.restrictions.clear();
    }

    trackBy = (i: number, item: Restriction): string => item.sid;

    add(): void {
        const data = new ObMatDialogConfig(null);
        this.#dialog.open<RestrictionDialog, RestrictionListElem, RestrictionListElem>(RestrictionDialog, data)
            .beforeClosed().pipe(
                filter(restriction => !!restriction),
                switchMap(restriction => this.#memberExtSrv.restrictions.create(this.#channelId, restriction)),
                tap(() => this.#ephemeralSrv.showSaveSuccess())
            ).subscribe();
    }

    remove(restriction: Restriction): void {
        this.#msgDialogService.showDeleteConfirmation({
            confirmation: { messageParams: { name: restriction.restriction_name } },
            delete$: this.#memberExtSrv.restrictions.delete(this.#channelId, restriction)
        });
    }

    load(restriction?: Restriction): void {
        if (restriction.loaded) return;
        this.#memberExtSrv.restrictions.load(this.#channelId, restriction).subscribe();
    }

    rename(restriction: Restriction): void {
        const data = new ObMatDialogConfig(restriction);
        this.#dialog.open<RestrictionDialog, Restriction, Restriction>(RestrictionDialog, data)
            .beforeClosed().pipe(
                filter(restriction => !!restriction),
                switchMap(changes => this.#memberExtSrv.restrictions.update(this.#channelId, changes)),
                tap(() => this.#ephemeralSrv.showSaveSuccess())
            )
            .subscribe();
    }

    private loadList(): void {
        this.#memberExtSrv.restrictions.list.load(this.#channelId);
        this.#memberExtSrv.channelCapacities.load(this.#channelId);
        this.#memberExtSrv.roles.load(this.#channelId);
    }
}
