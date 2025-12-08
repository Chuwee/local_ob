
import { Collective, CollectivesService, CollectiveStatus, CollectiveValidationMethod } from '@admin-clients/cpanel/collectives/data-access';
import { ENTITY_SERVICE, LOGIN_CONFIG_SERVICE } from '@admin-clients/cpanel/shared/data-access';
import { SearchablePaginatedSelectionLoadEvent, SearchablePaginatedSelectionModule } from '@admin-clients/shared/common/ui/components';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, FormGroupDirective, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, filter, first, map, shareReplay } from 'rxjs';

const PAGE_SIZE = 5;

@Component({
    selector: 'ob-collective-login',
    templateUrl: './collective-login.component.html',
    styleUrls: ['./collective-login.component.scss'],
    imports: [
        SearchablePaginatedSelectionModule, MatIconModule, ReactiveFormsModule, MatInputModule,
        TranslatePipe, AsyncPipe, MatTooltipModule, MatCheckboxModule, MatButtonModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})

export class CollectiveLoginComponent implements OnInit {
    readonly #formGroupDirective = inject(FormGroupDirective);
    readonly #collectiveSrv = inject(CollectivesService);
    readonly #entitySrv = inject(ENTITY_SERVICE);
    readonly #loginConfSrv = inject(LOGIN_CONFIG_SERVICE);
    readonly #destroyRef = inject(DestroyRef);

    readonly #filter = new BehaviorSubject({
        offset: 0,
        q: null as string,
        selectedOnly: false
    });

    form: FormGroup<{
        collectives: FormControl<Collective[]>;
        memberLimit: FormGroup<{
            enableLimit: FormControl<boolean>;
            limit: FormControl<number>;
        }>;
    }>;

    readonly pageSize = PAGE_SIZE;

    readonly selectedOnly$ = this.#filter.asObservable().pipe(map(filter => filter.selectedOnly));
    readonly collectives$ = combineLatest([
        this.#collectiveSrv.getCollectivesListData$().pipe(filter(Boolean)),
        this.#filter.asObservable()
    ]).pipe(
        map(([collectives, filter]) => {
            if (filter.selectedOnly) {
                collectives = collectives.filter(c =>
                    !!this.form.controls.collectives.value.find(cl => cl.id === c.id));
            }
            if (filter.q?.length) {
                collectives = collectives.filter(cl =>
                    cl.name.toLowerCase().includes(filter.q.toLowerCase()));
            }
            return {
                data: collectives.slice(filter.offset, filter.offset + this.pageSize),
                metadata: { total: collectives.length, offset: filter.offset, limit: this.pageSize }
            };
        }),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    readonly totalCollectives$ = this.collectives$.pipe(map(ce => ce.metadata?.total));
    readonly collectivesList$ = this.collectives$.pipe(map(ce => ce.data));
    readonly collectivesMetadata$ = this.collectives$.pipe(map(ce => ce.metadata));

    ngOnInit(): void {
        this.form = this.#formGroupDirective.control;

        this.#loadCollectives();
        this.#refreshFormDataHandler();

        this.form.controls.memberLimit.get('enableLimit').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe((isEnabled: boolean) => {
                if (isEnabled) {
                    this.form.controls.memberLimit.get('limit').enable({ emitEvent: false });
                } else {
                    this.form.controls.memberLimit.get('limit').disable({ emitEvent: false });
                }
            });
    }

    changeSelectedOnly(): void {
        this.#filter.next({
            ...this.#filter.value,
            selectedOnly: !this.#filter.value.selectedOnly,
            offset: 0
        });
    }

    loadPagedCollectives({ offset, q }: SearchablePaginatedSelectionLoadEvent): void {
        this.#filter.next({
            ...this.#filter.value,
            q,
            offset
        });
    }

    #loadCollectives(): void {
        this.#entitySrv.getEntity$().pipe(
            first(Boolean)
        ).subscribe(entity => {
            const req = {
                entity_id: entity.id,
                status: CollectiveStatus.active,
                validation_method: [CollectiveValidationMethod.userPassword]
            };
            this.#collectiveSrv.fetchCollectives(req);
        });
    }

    #refreshFormDataHandler(): void {
        combineLatest([
            this.#collectiveSrv.getCollectivesListData$(),
            this.#loginConfSrv.authConfig.get$()
        ]).pipe(
            filter(resp => resp.every(Boolean)),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(([collectives, config]) => {
            const selectedCollectives = config.authenticators?.filter(auth =>
                auth.type === 'COLLECTIVE'
            );
            const updateCollectives = [];
            if (selectedCollectives) {
                const selected = selectedCollectives.map(auth => ({ id: Number(auth.id), name: auth.id }));
                selected.forEach(selectedCollective => {
                    const updatedCollective = collectives.find(collective => selectedCollective.id === collective.id);
                    if (updatedCollective) {
                        updateCollectives.push(updatedCollective);
                    }
                });
            }

            this.form.patchValue({
                collectives: updateCollectives,
                memberLimit: {
                    enableLimit: config.max_members?.enabled,
                    limit: config.max_members?.limit
                }
            });

            this.form.markAsPristine();
            this.form.markAsUntouched();
        });
    }
}
