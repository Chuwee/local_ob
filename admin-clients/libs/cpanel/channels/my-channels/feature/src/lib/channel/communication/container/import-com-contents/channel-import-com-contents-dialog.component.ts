import { ImportComContentsGroups } from '@admin-clients/cpanel/channels/communication/data-access';
import {
    GetChannelsRequest, ChannelType,
    channelWebTypes, Channel, ChannelsExtendedService, ChannelsService
} from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    EntitiesBaseService,
    EntitiesFilterFields
} from '@admin-clients/shared/common/data-access';
import { DialogSize, MessageDialogService, SelectOption } from '@admin-clients/shared/common/ui/components';
import { atLeastOneRequiredInFormGroup } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, first, map, shareReplay, startWith, switchMap, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-channel-import-com-contents-dialog',
    templateUrl: './channel-import-com-contents-dialog.component.html',
    styleUrls: ['./channel-import-com-contents-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelImportComComponentsDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _channelId: number;
    private readonly PAGE_LIMIT = 100;

    form: UntypedFormGroup;
    operatorMode$: Observable<boolean>;
    entities$: Observable<SelectOption[]>;
    moreEntitiesAvailable$: Observable<boolean>;
    channels$: Observable<SelectOption[]>;
    moreChannelsAvailable$: Observable<boolean>;
    isReqInProgress$: Observable<boolean>;
    literalsImport: boolean;

    constructor(
        private _dialogRef: MatDialogRef<ChannelImportComComponentsDialogComponent>,
        private _auth: AuthenticationService,
        private _entitiesService: EntitiesBaseService,
        private _channelsService: ChannelsService,
        private _channelsExtSrv: ChannelsExtendedService,
        private _fb: UntypedFormBuilder,
        private _msgDialogService: MessageDialogService
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        // FormGroup creation
        this.form = this._fb.group({
            entity: null,
            originChannel: [null, Validators.required],
            importGroups: this._fb.group({
                literals: true,
                comElements: true,
                graphicElements: false,
                tickets: false
            }, {
                validators: atLeastOneRequiredInFormGroup()
            })
        });

        this._channelsService.getChannel$()
            .pipe(first(Boolean))
            .subscribe(channel => {
                this._channelId = channel.id;
                this.literalsImport = channel.type !== ChannelType.boxOffice;
            });

        this.operatorMode$ = this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);

        this.entities$ = this._entitiesService.entityList.getData$()
            .pipe(
                map(entities => {
                    const entitiesFilterOption: SelectOption[] = entities?.map(entity => ({
                        id: entity.id,
                        name: entity.name
                    }));
                    return entitiesFilterOption;
                })
            );
        this.moreEntitiesAvailable$ = this._entitiesService.entityList.getMetadata$()
            .pipe(map(metadata => metadata?.offset + metadata?.limit < metadata?.total));

        this.channels$ = combineLatest([
            this._channelsService.getChannel$(),
            this._channelsService.channelsList.getList$(),
            this.form.get('entity').valueChanges.pipe(startWith(null as SelectOption))
        ]).pipe(
            map(([channelToImportOn, channels, selectedEntity]: [Channel, Channel[], SelectOption]) => {
                let allowedChannelTypes: ChannelType[];
                channelToImportOn.type === ChannelType.boxOffice
                    ? allowedChannelTypes = [ChannelType.boxOffice]
                    : allowedChannelTypes = channelWebTypes;

                if (selectedEntity) {
                    channels = channels?.filter(channel => channel.entity.id === selectedEntity.id);
                    const currentSelChannel = this.form.get('originChannel').value;
                    if (currentSelChannel && !channels?.includes(currentSelChannel)) {
                        this.form.get('originChannel').reset();
                    }
                }

                channels = channels?.filter(channel => allowedChannelTypes.includes(channel.type)
                    && channel.id !== channelToImportOn.id);
                const channelsFilterOption: SelectOption[] = channels?.map(channel => ({
                    id: channel.id,
                    name: channel.name
                }));
                return channelsFilterOption;
            }),
            takeUntil(this._onDestroy),
            shareReplay(1)
        );

        this.moreChannelsAvailable$ = this._channelsService.channelsList.getMetadata$()
            .pipe(map(metadata => metadata?.offset + metadata?.limit < metadata?.total));

        // Clear channels for loading correctly from select-server-search-component if entity changes
        this.form.get('entity').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(() => this._channelsService.channelsList.clear());

        this.isReqInProgress$ = this._channelsExtSrv.isCloneContentsSaving$();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._entitiesService.entityList.clear();
        this._channelsService.channelsList.clear();
    }

    loadEntities(q: string, next = false): void {
        this._entitiesService.loadServerSearchEntityList({
            limit: this.PAGE_LIMIT,
            sort: 'name:asc',
            fields: [EntitiesFilterFields.name],
            type: 'CHANNEL_ENTITY',
            q
        }, next);
    }

    loadChannels(q: string, next = false): void {
        const entitySelected: SelectOption = this.form.get('entity').value;
        const request: GetChannelsRequest = {
            limit: this.PAGE_LIMIT,
            offset: 0,
            sort: 'name:asc',
            entityId: entitySelected?.id ? entitySelected.id as number : undefined,
            name: q
        };
        if (!next) {
            this._channelsService.channelsList.load(request);
        } else {
            this._channelsService.channelsList.loadMore(request);
        }
    }

    importContents(): void {
        if (this.form.valid) {
            this._msgDialogService.showWarn({
                size: DialogSize.SMALL,
                title: 'TITLES.ALERT',
                message: 'CHANNELS.IMPORT_CONTENTS.REPLACE_CONTENTS_WARNING',
                actionLabel: 'FORMS.ACTIONS.OK',
                showCancelButton: true
            })
                .pipe(
                    filter(Boolean),
                    switchMap(accepted => {
                        if (accepted) {
                            const srcChannelId = this.form.value.originChannel.id;
                            const contents = Object.entries(this.form.value.importGroups)
                                .filter(([_, value]) => value)
                                .map(([key, _]) => ImportComContentsGroups[key]);
                            return this._channelsExtSrv.cloneContents(this._channelId, {
                                channel_id: srcChannelId,
                                contents
                            });
                        }
                    }))
                .subscribe(resp => {
                    if (resp?.length) {
                        this.close({ isImported: true, errors: resp.map(el => el.type) });
                    } else {
                        this.close({ isImported: true });
                    }
                });
        } else {
            this.form.markAllAsTouched();
        }
    }

    close(result: { isImported: boolean; errors?: ImportComContentsGroups[] } = { isImported: false }): void {
        this._dialogRef.close(result);
    }
}
