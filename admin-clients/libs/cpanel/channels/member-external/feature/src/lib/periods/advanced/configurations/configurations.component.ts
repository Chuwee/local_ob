import { Channel, ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import {
    ChannelMemberExternalService, DynamicConfiguration, DynamicConfigurations, MemberPeriods
} from '@admin-clients/cpanel-channels-member-external-data-access';
import { ContextNotificationComponent, ObMatDialogConfig, SearchTableComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { CommonModule } from '@angular/common';
import { Component, ChangeDetectionStrategy, OnInit, OnDestroy, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { combineLatest, filter, first, map, Observable, Subject, withLatestFrom } from 'rxjs';
import { ConfigurationDialogComponent, ConfigurationData } from '../configuration/configuration-dialog.component';

const suffix = 'MEMBER_EXTERNAL.ADVANCED';

@Component({
    selector: 'app-member-external-configurations',
    templateUrl: './configurations.component.html',
    styleUrls: ['./configurations.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe,
        CommonModule,
        FlexLayoutModule,
        MaterialModule,
        ContextNotificationComponent,
        FormContainerComponent,
        SearchTableComponent
    ]
})
export class ConfigurationsComponent implements OnInit, OnDestroy {
    readonly #memberExtSrv = inject(ChannelMemberExternalService);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #route = inject(ActivatedRoute);
    readonly #matDialog = inject(MatDialog);
    readonly #translate = inject(TranslateService);

    #onDestroy = new Subject<void>();
    #channel: Channel;
    #operations$: Observable<DynamicConfigurations>;

    configurations$: Observable<DynamicConfigurations>;

    readonly loading$ = this.#memberExtSrv.channelConfigurations.loading$();
    readonly error$ = this.#memberExtSrv.channelConfigurations.error$();

    period$: Observable<MemberPeriods> = this.#route.data.pipe(map(data => data['period']));

    filter = (q: string, a: DynamicConfiguration): boolean =>
        this.#translate.instant(`${suffix}.OPERATIONS.${a.operation_name}`).toLowerCase().includes(q.toLowerCase()) ||
        this.#translate.instant(`${suffix}.TYPES.${a.type}`)?.toLowerCase().includes(q.toLowerCase()) ||
        a.implementation?.toLowerCase().includes(q.toLowerCase());

    ngOnInit(): void {
        this.#memberExtSrv.channelConfigurations.clear();

        this.#channelsSrv.getChannel$().pipe(first())
            .subscribe(channel => {
                this.#channel = channel;
                this.#memberExtSrv.channelConfigurations.load(channel.id);
            });

        this.#operations$ = this.#memberExtSrv.channelConfigurations.get$().pipe(
            filter(val => !!val),
            withLatestFrom(this.#memberExtSrv.configurations.get$()),
            map(([channelConfigs, configs]) => [
                ...(this.filterNotConfiguredOperations(configs, channelConfigs)),
                ...channelConfigs
            ])
        );

        this.configurations$ = combineLatest([
            this.#operations$,
            this.period$
        ]).pipe(
            map(([configurations, orderType]) => configurations?.filter(config =>
                !orderType || !config.order_type || config.order_type === orderType
            ))
        );
    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    show(configuration: DynamicConfiguration): void {
        this.#matDialog.open<ConfigurationDialogComponent, ConfigurationData, void>(
            ConfigurationDialogComponent,
            new ObMatDialogConfig({ channel: this.#channel, configuration })
        );
    }

    private filterNotConfiguredOperations(
        configs: DynamicConfigurations,
        channelConfigs: DynamicConfigurations
    ): DynamicConfigurations {
        return configs.filter((config, index) =>
            configs.findIndex(elem => elem.operation_name === config.operation_name) === index &&
            !channelConfigs?.find(elem => elem.operation_name === config.operation_name)
        ).map(elem => ({ ...elem, implementation: undefined }));
    }

}

