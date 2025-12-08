import {
    ChannelType, channelWebTypes, Channel, PostChannelRequest,
    ChannelsService, ChannelFieldsRestrictions
} from '@admin-clients/cpanel/channels/data-access';
import {
    CollectivesService, CollectiveType, CollectiveValidationMethod, Collective, GetCollectivesRequest
} from '@admin-clients/cpanel/collectives/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    Entity, EntitiesBaseService, EntitiesBaseState
} from '@admin-clients/shared/common/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { combineLatest, Observable, of, Subject } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, takeUntil, tap, withLatestFrom } from 'rxjs/operators';

@Component({
    selector: 'app-new-channel-dialog',
    templateUrl: './new-channel-dialog.component.html',
    styleUrls: ['./new-channel-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        EntitiesBaseService, EntitiesBaseState
    ],
    standalone: false
})
export class NewChannelDialogComponent implements OnInit, OnDestroy {
    private _onDestroy: Subject<void> = new Subject();

    newChannelForm: UntypedFormGroup;
    operatorMode$: Observable<boolean>;
    entities$: Observable<Channel[]>;
    inProgress$: Observable<boolean>;
    maxChannelNameLength = ChannelFieldsRestrictions.channelNameLength;
    maxChannelUrlLength = ChannelFieldsRestrictions.channelUrlLength;
    channelTypes$: Observable<ChannelType[]>;
    collectives$: Observable<Collective[]>;

    constructor(
        private _dialogRef: MatDialogRef<NewChannelDialogComponent>,
        private _auth: AuthenticationService,
        private _entitiesService: EntitiesBaseService,
        private _channelsService: ChannelsService,
        private _collectivesService: CollectivesService,
        private _fb: UntypedFormBuilder
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        // FormGroup creation
        this.newChannelForm = this._fb.group({
            entity: [null, Validators.required],
            name: [
                null, [Validators.required, Validators.maxLength(ChannelFieldsRestrictions.channelNameLength)]
            ],
            type: [null, [Validators.required]],
            url: [
                { value: null, disabled: true },
                [
                    Validators.required,
                    Validators.maxLength(ChannelFieldsRestrictions.channelUrlLength),
                    Validators.pattern(ChannelFieldsRestrictions.channelUrlPattern)
                ]
            ],
            collective: [
                { value: null, disabled: true }, [Validators.required]
            ]
        });
        this.inProgress$ = booleanOrMerge([
            this._channelsService.isChannelSaving$(),
            this._entitiesService.entityList.inProgress$(),
            this._collectivesService.isCollectiveListLoading$()
        ]);
        this.operatorMode$ = this._auth.getLoggedUser$().pipe(
            filter(user => user !== null),
            map(user => {
                if (AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR])) {
                    return true;
                } else {
                    this.newChannelForm.patchValue({ entity: user.entity });
                    return false;
                }
            }),
            shareReplay(1)
        );
        this.entities$ = this.operatorMode$.pipe(
            switchMap((isOperator: boolean) => {
                if (isOperator) {
                    this._entitiesService.entityList.load({
                        limit: 999,
                        sort: 'name:asc',
                        type: 'CHANNEL_ENTITY'
                    });
                    return this._entitiesService.entityList.getData$()
                        .pipe(
                            first(entities => !!entities),
                            tap(entities => this.newChannelForm.patchValue({ entity: entities[0] }))
                        );
                }
                return of([]);
            }),
            shareReplay(1)
        );
        this.channelTypes$ = this.newChannelForm.get('entity').valueChanges
            .pipe(
                map((entity: Entity) => {
                    let channelTypes = Object.values(ChannelType);
                    if (entity) {
                        if (!entity.settings?.enable_B2B || !entity.settings?.types.includes('EVENT_ENTITY')) {
                            channelTypes = channelTypes.filter(type => type !== ChannelType.webB2B);
                        }
                        if (!entity.settings?.allow_avet_integration) {
                            channelTypes = channelTypes
                                .filter(type => type !== ChannelType.webSubscribers && type !== ChannelType.members);
                        }
                        const channelTypeCtrl = this.newChannelForm.get('type');
                        if (channelTypeCtrl.value && !channelTypes.includes(channelTypeCtrl.value)) {
                            channelTypeCtrl.setValue(null);
                        }
                    }
                    return channelTypes;
                })
            );
        this.collectives$ = this._collectivesService.getCollectivesListData$()
            .pipe(
                filter(collectives => !!collectives),
                tap(collectives => this.newChannelForm.patchValue({ collective: collectives[0] ?? null })),
                shareReplay(1)
            );

        this.initFormChangesHandlers();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    createChannel(): void {
        if (this.newChannelForm.valid) {
            const data: PostChannelRequest = this.newChannelForm.value;
            this._channelsService.createChannel({ ...data })
                .subscribe(id => this.close(id));
        } else {
            this.newChannelForm.markAllAsTouched();
        }
    }

    close(channelId: number = null): void {
        this._dialogRef.close(channelId);
    }

    private initFormChangesHandlers(): void {
        this.newChannelForm.get('type').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(channelType => {
                if (channelType && channelWebTypes.includes(channelType) || channelType === ChannelType.members) {
                    this.newChannelForm.get('url').enable();
                } else {
                    this.newChannelForm.get('url').setValue(null);
                    this.newChannelForm.get('url').disable();
                }
            });

        combineLatest([
            this.newChannelForm.get('entity').valueChanges,
            this.newChannelForm.get('type').valueChanges
        ])
            .pipe(
                filter(([entity, _]: [Entity, ChannelType]) => !!entity),
                withLatestFrom(this.operatorMode$),
                takeUntil(this._onDestroy)
            )
            .subscribe(([[entity, channelType], isOperator]) => {
                this.newChannelForm.get('collective').setValue(null);
                if (channelType === ChannelType.webSubscribers) {
                    const req: GetCollectivesRequest = {
                        type: CollectiveType.external,
                        validation_method: [CollectiveValidationMethod.userPassword],
                        sort: 'name:asc'
                    };
                    if (isOperator) {
                        req.entity_id = entity.id;
                    }
                    this._collectivesService.loadCollectivesList(req);
                    this.newChannelForm.get('collective').enable();
                } else {
                    this._collectivesService.clearCollectivesList();
                    this.newChannelForm.get('collective').disable();
                }
            });
    }
}
