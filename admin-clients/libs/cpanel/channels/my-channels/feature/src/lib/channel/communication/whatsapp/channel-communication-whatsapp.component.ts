import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Channel, ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { Entity } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject, combineLatest, throwError } from 'rxjs';
import { filter, first, map, startWith, switchMap, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-channel-communication-whatsapp',
    templateUrl: './channel-communication-whatsapp.component.html',
    styleUrls: ['./channel-communication-whatsapp.component.scss'],
    imports: [FormContainerComponent, TranslatePipe, CommonModule, MaterialModule, ReactiveFormsModule, FlexLayoutModule],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChannelWhatsappContentsComponent implements WritingComponent, OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    private readonly _channelsService = inject(ChannelsService);
    private readonly _entitiesService = inject(EntitiesService);
    private readonly _ephemeralSrv = inject(EphemeralMessageService);
    private readonly _fb = inject(FormBuilder);
    whatsappTemplates$ = this._entitiesService.whatsappTemplates.get$()
        .pipe(
            filter(Boolean),
            map(value => value.sort((a, b) => a.id - b.id)));

    channel: Channel;
    entity: Entity;
    form = this._fb.group({
        use_entity_config: true,
        whatsapp_template: null as number
    });

    isLoadingOrSaving$ = combineLatest([
        this._channelsService.isChannelLoading$(),
        this._entitiesService.whatsappTemplates.inProgress$(),
        this._entitiesService.isEntityLoading$()
    ]).pipe(map(loadings => loadings.some(isLoading => isLoading)));

    cancel(): void {
        this._channelsService.loadChannel(this.channel.id.toString());
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            return this._channelsService.saveChannel(this.channel.id,
                {
                    settings: {
                        whatsapp: {
                            override_entity_config: !this.form.controls.use_entity_config.value,
                            whatsapp_template: this.form.controls.whatsapp_template.value
                        }
                    }
                }).pipe(tap(() => {
                    this._ephemeralSrv.showSaveSuccess();
                    this._channelsService.loadChannel(this.channel.id.toString());
                }));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }

    }

    ngOnInit(): void {
        this._entitiesService.getEntity$().pipe(filter(Boolean), switchMap(entity => this.form.controls.use_entity_config.valueChanges
            .pipe(
                startWith(this.form.controls.use_entity_config.value),
                tap(value => {
                    if (value) {
                        this.form.controls.whatsapp_template.disable();
                        this.form.controls.whatsapp_template.patchValue(entity?.settings?.whatsapp?.whatsapp_template ?? 1);
                    } else {
                        this.form.controls.whatsapp_template.enable();
                    }
                }),
                takeUntil(this._onDestroy)
            )
        )).subscribe();
        this._channelsService.getChannel$()
            .pipe(first(value => value !== null))
            .subscribe(channel => {
                this.channel = channel;
                this._entitiesService.loadEntity(channel.entity?.id);
                this._entitiesService.whatsappTemplates.load(channel.entity?.id);
            });
        combineLatest([this._channelsService.getChannel$(), this._entitiesService.getEntity$()])
            .pipe(
                filter(([channel, entity]) => Boolean(channel) && Boolean(entity)),
                takeUntil(this._onDestroy))
            .subscribe(([channel, entity]) => {
                this.channel = channel;
                this.entity = entity;
                this.form.reset({
                    whatsapp_template: channel.settings?.whatsapp?.override_entity_config ?
                        (channel.settings?.whatsapp?.whatsapp_template ?? entity.settings?.whatsapp?.whatsapp_template ?? 1)
                        : entity.settings?.whatsapp?.whatsapp_template ?? 1,
                    use_entity_config: !channel.settings?.whatsapp?.override_entity_config
                });
            });
        this._channelsService.getChannel$().pipe(filter(Boolean), takeUntil(this._onDestroy)).subscribe(value => {
            this.form.reset({
                whatsapp_template: value.settings?.whatsapp?.whatsapp_template,
                use_entity_config: !value.settings?.whatsapp?.override_entity_config
            });
        });
    }

    ngOnDestroy(): void {
        this._entitiesService.whatsappTemplates.clear();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }
}
