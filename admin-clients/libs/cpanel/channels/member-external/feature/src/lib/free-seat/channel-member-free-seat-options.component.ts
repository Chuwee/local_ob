import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Metadata } from '@OneboxTM/utils-state';
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { NextMatch, ChannelMemberExternalService } from '@admin-clients/cpanel-channels-member-external-data-access';
import {
    EphemeralMessageService, SearchablePaginatedSelectionLoadEvent, SearchablePaginatedSelectionModule, pageSize
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, EventEmitter, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, Observable, throwError } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, tap } from 'rxjs/operators';

@Component({
    selector: 'app-free-seat-options',
    templateUrl: './channel-member-free-seat-options.component.html',
    styleUrls: ['./channel-member-free-seat-options.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, FlexLayoutModule, ReactiveFormsModule, TranslatePipe, FormContainerComponent,
        FormControlErrorsComponent, SearchablePaginatedSelectionModule, AsyncPipe
    ]
})
export class FreeSeatOptionsComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #onDestroy = inject(DestroyRef);
    readonly #nextMatchesPaged = new BehaviorSubject<NextMatch[]>(null);
    readonly #nextMatchesMetadataPaged = new BehaviorSubject<Metadata>(null);
    readonly #channelsService = inject(ChannelsService);
    readonly #memberExtSrv = inject(ChannelMemberExternalService);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralSrv = inject(EphemeralMessageService);

    readonly allSelectedClick = new EventEmitter<boolean>();
    readonly nextMatchesPaged$ = this.#nextMatchesPaged.asObservable();
    readonly nextMatchesMetadata$ = this.#nextMatchesMetadataPaged.asObservable();
    readonly totalMatches$ = this.nextMatchesMetadata$
        .pipe(first(Boolean), map(metadata => metadata?.total));

    readonly form = this.#fb.group({
        free_seat: false,
        transfer_seat: false,
        blocked_matches: [],
        allow_free_seat_till: [0, Validators.min(0)],
        allow_recover_seat_till: [0, Validators.min(0)]
    });

    readonly nextMatchesColumns = ['active', 'name', 'competition', 'season'];
    readonly allSelected$ = this.allSelectedClick.pipe(
        takeUntilDestroyed(this.#onDestroy),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    readonly loading$ = booleanOrMerge([
        this.#memberExtSrv.channelOptions.loading$(),
        this.#memberExtSrv.nextMatches.loading$()
    ]);

    #channelId: number;
    selectedOnly = false;

    get blockedMatches(): FormControl {
        return this.form.get('blocked_matches') as FormControl;
    }

    get selectedMatches(): number {
        return this.blockedMatches?.value?.length || 0;
    }

    ngOnInit(): void {
        this.#channelsService.getChannel$()
            .pipe(
                first(Boolean),
                switchMap(channel => {
                    this.#channelId = channel.id;
                    this.load();
                    return this.#memberExtSrv.channelOptions.get$();
                }),
                filter(membersConfig => !!membersConfig),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(membersConfig =>
                this.form.patchValue({
                    ...membersConfig,
                    blocked_matches: membersConfig?.blocked_matches.map(matchId => ({ id: matchId }))
                }));

        combineLatest([
            this.allSelected$,
            this.#memberExtSrv.nextMatches.get$().pipe(first(nextMatches => !!nextMatches))
        ]).pipe(
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(([allSelected, nextMatches]) => {
            this.blockedMatches.setValue(allSelected ? nextMatches : []);
            //Hackish as hell
            this.form.markAsDirty();
        });
    }

    ngOnDestroy(): void {
        this.#memberExtSrv.channelOptions.clear();
        this.#memberExtSrv.nextMatches.clear();
    }

    save(): void {
        this.save$().subscribe(() => this.load());
    }

    save$(): Observable<void> {
        if (this.form.valid && this.form.dirty) {
            const formValue = this.form.value;
            formValue.blocked_matches = formValue.blocked_matches?.map(blockedMatch => blockedMatch.id);
            return this.#memberExtSrv.channelOptions.save(this.#channelId, formValue)
                .pipe(tap(() => this.#ephemeralSrv.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg();
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this.load();
    }

    loadPagedNextMatches({ offset, q }: SearchablePaginatedSelectionLoadEvent): void {
        this.#memberExtSrv.nextMatches.get$().pipe(first(nextMatches => !!nextMatches)).subscribe(nextMatches => {
            if (this.selectedOnly) {
                nextMatches = nextMatches.filter(nextMatch =>
                    !!this.blockedMatches.value.find(nm => nm.id === nextMatch.id));
            }
            if (q) {
                nextMatches = nextMatches.filter(nextMatch =>
                    nextMatch.name?.toLowerCase().includes(q.toLowerCase()) ||
                    nextMatch.competition?.toLowerCase().includes(q.toLowerCase()) ||
                    nextMatch.season?.toLowerCase().includes(q.toLowerCase()));
            }

            this.#nextMatchesPaged.next(nextMatches.slice(offset, offset + pageSize));
            this.#nextMatchesMetadataPaged.next(new Metadata({ total: nextMatches.length, offset, limit: pageSize }));
        });
    }

    private load(): void {
        this.#memberExtSrv.channelOptions.load(this.#channelId);
        this.#memberExtSrv.nextMatches.load(this.#channelId);
    }
}
