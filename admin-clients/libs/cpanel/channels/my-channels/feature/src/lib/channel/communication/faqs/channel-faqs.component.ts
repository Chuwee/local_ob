import { ChannelFaqs, ChannelsService, ChannelType, FaqsListFilters } from '@admin-clients/cpanel/channels/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { EmptyStateTinyComponent, EphemeralMessageService, MessageDialogService, ObMatDialogConfig, SearchInputComponent } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormArray, FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTable, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, forkJoin, Observable } from 'rxjs';
import { filter, map, shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { ChannelCommunicationNotifierService } from '../container/channel-communication-notifier.service';
import { ChannelFaqDialogComponent } from './faq-dialog/channel-faq-dialog.component';
import { ChannelFaqsFilterComponent } from './filter/channel-faqs-filter.component';

@Component({
    selector: 'app-channel-faqs',
    templateUrl: './channel-faqs.component.html',
    styleUrls: ['./channel-faqs.component.scss'],
    imports: [
        FormContainerComponent, SearchInputComponent, TranslatePipe, AsyncPipe, MatButtonModule,
        MatTableModule, MatProgressSpinnerModule, MatIconModule, MatTooltipModule, EmptyStateTinyComponent,
        FlexLayoutModule, ReactiveFormsModule, ChannelFaqsFilterComponent, DragDropModule, EllipsifyDirective
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChannelFaqsComponent implements OnInit, OnDestroy {
    @ViewChild('faqsTable')
    private _faqsTable: MatTable<ChannelFaqs>;

    readonly #fb = inject(FormBuilder);
    readonly #channelsService = inject(ChannelsService);
    readonly #entitiesBaseService = inject(EntitiesBaseService);
    readonly #entitiesService = inject(EntitiesService);
    readonly #ephemeralMsg = inject(EphemeralMessageService);
    readonly #messageDialogSrv = inject(MessageDialogService);
    readonly #matDialog = inject(MatDialog);
    readonly #communicationNotifierService = inject(ChannelCommunicationNotifierService);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #onDestroy = inject(DestroyRef);
    #channelId: number;
    #filters = { q: null, tag: null, language: null };

    readonly form: FormArray = this.#fb.array([]);

    readonly isHandsetOrTablet$: Observable<boolean> = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    readonly isInProgress$: Observable<boolean> = booleanOrMerge([
        this.#channelsService.isChannelLoading$(),
        this.#channelsService.faqs.list.loading$(),
        this.#channelsService.faqs.loading$()
    ]);

    readonly defaultLang = new BehaviorSubject<string>(null);
    readonly faqsList$ = this.#channelsService.getChannel$()
        .pipe(
            filter(Boolean),
            switchMap(channel => {
                this.#channelId = channel.id;
                this.defaultLang.next(channel.languages.default);
                this.#channelsService.faqs.list.load(this.#channelId, this.#filters);
                return this.#channelsService.faqs.list.get$();
            }),
            filter(Boolean),
            shareReplay(1),
            tap(faqs => {
                this.form.clear();
                faqs.forEach(faq => {
                    this.form.push(this.#fb.control(faq));
                });
                this.form.markAsPristine();
            })
        );

    readonly hasCustomers$ = this.#entitiesService.authConfig.get$()
        .pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#onDestroy),
            map(authConfig => {
                const isEnabled = authConfig?.enabled;
                const hasValidAuthenticator = authConfig?.authenticators?.some(auth =>
                    auth.type === 'DEFAULT' ||
                    (auth.type === 'VENDOR' && auth.customer_creation === 'ENABLED')
                );
                return isEnabled && hasValidAuthenticator;
            })
        );

    readonly hasSmartBooking$ = this.#entitiesBaseService.getEntity$()
        .pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#onDestroy),
            map(entity => entity.settings.external_integration?.custom_managements.filter(
                management => management.type === 'SMART_BOOKING_INTEGRATION'
            ).length > 0));

    readonly isB2b$ = this.#channelsService.getChannel$().pipe(map(channel => channel.type === ChannelType.webB2B));

    readonly categoryTypes$ = combineLatest([
        this.hasCustomers$,
        this.hasSmartBooking$,
        this.isB2b$
    ]).pipe(
        takeUntilDestroyed(this.#onDestroy),
        map(([hasCustomers, hasSmartBooking, isB2b]) => {
            const categories = [];
            if (!isB2b) categories.push('DEFAULT');
            if (hasCustomers && !isB2b) categories.push('USER_AREA');
            if (hasSmartBooking) categories.push('SMARTBOOKING');
            if (isB2b) categories.push('SEAT_SELECTION');
            return categories;
        })
    );

    readonly columns$ = this.categoryTypes$.pipe(
        filter(Boolean),
        map(categories => {
            if (categories.length > 1) return ['title', 'content', 'categories', 'actions'];
            else return ['title', 'content', 'actions'];
        })
    );

    readonly $categories = toSignal(this.categoryTypes$);
    canDrop = false;

    ngOnInit(): void {
        this.#communicationNotifierService.getRefreshDataSignal$()
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(() => this.loadFaqs());

        this.#channelsService.getChannel$()
            .pipe(take(1))
            .subscribe(channel => this.#entitiesService.authConfig.load(channel.entity.id));
    }

    ngOnDestroy(): void {
        this.#channelsService.faqs.list.clear();
        this.#entitiesService.authConfig.clear();
    }

    openNewFaq(): void {
        this.#matDialog.open(ChannelFaqDialogComponent, new ObMatDialogConfig({
            channelId: this.#channelId,
            categories: this.$categories()
        })).beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(() => {
                this.loadFaqs();
                this.#ephemeralMsg.showSaveSuccess();
            });
    }

    openEditFaq(faqKey: number): void {
        this.#matDialog.open(ChannelFaqDialogComponent, new ObMatDialogConfig({
            channelId: this.#channelId,
            faqKey,
            categories: this.$categories()
        })).beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(() => {
                this.loadFaqs();
                this.#ephemeralMsg.showSaveSuccess();
            });
    }

    delete(faqKey: number): void {
        this.#messageDialogSrv.showDeleteConfirmation({
            confirmation: {
                title: 'CHANNELS.FAQS.DELETE_TITLE',
                message: 'CHANNELS.FAQS.DELETE_MESSAGE'
            },
            delete$: this.#channelsService.faqs.delete(this.#channelId, faqKey).pipe(
                tap(() => {
                    this.#channelsService.faqs.list.load(this.#channelId);
                    this.#ephemeralMsg.showSuccess({
                        msgKey: 'CHANNELS.FAQS.DELETE_OK'
                    });
                }))
        });
    }

    cancel(): void {
        this.loadFaqs();
    }

    save(): void {
        this.save$().subscribe(() => {
            this.loadFaqs();
        });
    }

    save$(): Observable<void[]> {
        if (this.form.valid) {
            const obs$: Observable<void>[] = [];
            obs$.push(this.#channelsService.faqs.list.update(this.#channelId, this.form.value));

            return forkJoin(obs$)
                .pipe(tap(() => {
                    this.#ephemeralMsg.showSaveSuccess();
                }));
        }
    }

    onDropCondition(event: CdkDragDrop<string[]>, faqsList: ChannelFaqs[]): void {
        moveItemInArray(this.form.value, event.previousIndex, event.currentIndex);
        moveItemInArray(faqsList, event.previousIndex, event.currentIndex);
        this._faqsTable.renderRows();
        this.form.markAsDirty();
    }

    dropDisabled(): boolean {
        this.canDrop = (this.#filters.q === null || this.#filters.q === '') && this.#filters.tag === null;
        return !this.canDrop;
    }

    loadFilteredFaqs(filters: FaqsListFilters): void {
        this.#filters = { ...this.#filters, ...filters };
        this.#channelsService.faqs.list.load(this.#channelId, this.#filters);
    }

    private loadFaqs(): void {
        this.#channelsService.faqs.list.load(this.#channelId, this.#filters);
    }
}
