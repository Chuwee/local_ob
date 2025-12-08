import { GetChannelsRequest, WhitelabelType } from '@admin-clients/cpanel/channels/data-access';
import { AdminChannel, AdminChannelsService, PutChannelWhitelabelType } from '@admin-clients/cpanel/migration/channels/data-access';
import {
    ChipsComponent, ChipsFilterDirective, ContextNotificationComponent,
    DialogSize, EphemeralMessageService, FilterItem, ListFilteredComponent,
    ListFiltersService, MessageDialogService, PaginatorComponent, PopoverComponent,
    PopoverFilterDirective, SearchInputComponent, SortFilterComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe, NgClass } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSort, SortDirection } from '@angular/material/sort';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, of } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { AdminChannelsListFilterComponent } from './filter/admin-channels-list-filter.component';

@Component({
    selector: 'app-admin-channels-list',
    templateUrl: './admin-channels-list.component.html',
    styleUrls: ['./admin-channels-list.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        RouterLink,
        TranslatePipe,
        ReactiveFormsModule,
        NgClass, AsyncPipe,
        MaterialModule,
        ContextNotificationComponent,
        PopoverComponent,
        PopoverFilterDirective,
        ChipsFilterDirective,
        FlexLayoutModule,
        SearchInputComponent,
        ChipsComponent,
        PaginatorComponent,
        AdminChannelsListFilterComponent
    ]
})
export class AdminChannelsListComponent extends ListFilteredComponent implements AfterViewInit {
    #adminChannelsSrv = inject(AdminChannelsService);
    #ephemeralMsgSrv = inject(EphemeralMessageService);
    #breakpointObserver = inject(BreakpointObserver);
    #msgDialogSrv = inject(MessageDialogService);
    #ref = inject(ChangeDetectorRef);
    #request = new GetChannelsRequest();
    #sortFilterComponent: SortFilterComponent;

    @ViewChild(MatSort) private _matSort: MatSort;
    @ViewChild(PaginatorComponent) private _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private _searchInputComponent: SearchInputComponent;
    @ViewChild(AdminChannelsListFilterComponent) private _filterComponent: AdminChannelsListFilterComponent;

    readonly channels$ = this.#adminChannelsSrv.channelsList.getList$();
    readonly channelsPageSize = 20;
    readonly initSortCol = 'name';
    readonly initSortDir: SortDirection = 'asc';
    readonly generalStatusOptions = ['ENABLED', 'DISABLED'];
    readonly v4StatusOptions = ['MIGRATED', 'NOT_MIGRATED', 'MIGRATED_STRIPE'];
    readonly whitelabelTypeOptions = ['INTERNAL', 'EXTERNAL'];
    readonly channelsMetadata$ = this.#adminChannelsSrv.channelsList.getMetadata$();
    readonly channelsLoading$ = this.#adminChannelsSrv.channelsList.loading$();
    readonly canReadMultipleEntities$ = of(true);
    readonly isHandsetOrTablet$: Observable<boolean> = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    readonly columns = [
        'name', 'entity_name', 'status', 'type', 'whitelabel_type', 'v4_config_enabled', 'v4_enabled', 'v2_receipt_template_enabled'
    ];

    ngAfterViewInit(): void {
        this.#sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._paginatorComponent,
            this.#sortFilterComponent,
            this._searchInputComponent,
            this._filterComponent
        ]);
    }

    updateWhitelabelType({ id, name }: AdminChannel, type: WhitelabelType): void {
        const request: PutChannelWhitelabelType = {
            whitelabel_type: type
        };

        this.showWarnDialog(
            name,
            this.#adminChannelsSrv.channel.updateWhitelabelType(id, request),
            {
                dialogMessage: 'CHANNELS.WHITELABEL_TYPE.WARN_MESSAGE',
                successMessage: 'CHANNELS.WHITELABEL_TYPE.SUCCESS_MESSAGE'
            },
            true
        );
    }

    updateV4({ id, name }: AdminChannel, value: string): void {
        const migrate = value === 'MIGRATED' || value === 'MIGRATED_STRIPE';
        const request = {
            migrate_to_channels: migrate,
            stripe_hook_checked: false
        };
        if (value === 'MIGRATED_STRIPE') {
            request.stripe_hook_checked = true;
        }
        this.showWarnDialog(
            name,
            this.#adminChannelsSrv.channel.migrate(id, request),
            {
                dialogMessage: migrate ? 'CHANNELS.V4_STATUS.WARN.MESSAGE' : 'CHANNELS.V4_CONFIG_STATUS.WARN.MESSAGE',
                successMessage: migrate ? 'CHANNELS.V4_STATUS.SUCCESS' : 'CHANNELS.V4_CONFIG_STATUS.SUCCESS'
            }
        );
    }

    migrateReceipt({ id, name }: AdminChannel, migrate: boolean): void {
        const request = {
            migrate_receipt_template: migrate
        };
        this.showWarnDialog(
            name,
            this.#adminChannelsSrv.channel.migrateReceipt(id, request),
            {
                dialogMessage: 'CHANNELS.V2_RECEIPT_TEMPLATE_STATUS.WARN.MESSAGE',
                successMessage: 'CHANNELS.V2_RECEIPT_TEMPLATE_STATUS.SUCCESS'
            }
        );
    }

    showWarnDialog(channel: string, action$: Observable<void>,
        { dialogMessage, successMessage }: { dialogMessage: string; successMessage: string }, isWhitelabel = false): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: isWhitelabel ? 'CHANNELS.WHITELABEL_TYPE.WARN_TITLE' : 'TITLES.ALERT',
            message: dialogMessage,
            messageParams: { channel },
            actionLabel: 'FORMS.ACTIONS.CONTINUE',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => action$)
            )
            .subscribe({
                complete: () => this.#loadChannels(),
                next: () => this.#ephemeralMsgSrv.showSuccess({
                    msgKey: successMessage,
                    msgParams: { channel }
                }),
                error: ({ error: { code } }) => {
                    if (code === 'CHANNEL_CONTAINS_NOT_VALIDATED_STRIPE_GATEWAY') {
                        this.#msgDialogSrv.showAlert({
                            title: 'CHANNELS.V4_STATUS.STRIPE_ERROR.TITLE',
                            message: 'CHANNELS.V4_STATUS.STRIPE_ERROR.MESSAGE',
                            messageParams: { channel }
                        });
                    }
                }
            });
    }

    loadData(filters: FilterItem[]): void {
        this.#request = new GetChannelsRequest();
        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values && values.length > 0) {
                switch (filterItem.key) {
                    case 'SORT':
                        this.#request.sort = values[0].value;
                        break;
                    case 'PAGINATION':
                        this.#request.limit = values[0].value.limit;
                        this.#request.offset = values[0].value.offset;
                        break;
                    case 'SEARCH_INPUT':
                        this.#request.name = values[0].value;
                        break;
                    case 'ENTITY':
                        this.#request.entityId = values[0].value;
                        break;
                    case 'STATUS':
                        this.#request.status = values.map(val => val.value);
                        break;
                    case 'TYPE':
                        this.#request.type = values[0].value;
                        break;
                }
            }
        });

        this.#loadChannels();
    }

    #loadChannels(): void {
        this.#adminChannelsSrv.channelsList.load({ ...this.#request });
        this.#ref.detectChanges();
    }
}
