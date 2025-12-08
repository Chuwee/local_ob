import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Channel, ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { ChannelMemberExternalService, ChannelCapacity } from '@admin-clients/cpanel-channels-member-external-data-access';
import { MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { VenueTemplatesService, VenueTemplateStatus } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, inject, OnInit, QueryList, ViewChildren } from '@angular/core';
import { UntypedFormArray, UntypedFormBuilder, Validators } from '@angular/forms';
import { combineLatest, defer, filter, first, mapTo, Observable, of, tap, throwError } from 'rxjs';

@Component({
    selector: 'app-members-external-capacities',
    templateUrl: './capacities.component.html',
    styleUrls: ['./capacities.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class CapacitiesComponent implements OnInit {
    readonly #channelsService = inject(ChannelsService);
    readonly #venueTplsSrv = inject(VenueTemplatesService);
    readonly #channelMemberSrv = inject(ChannelMemberExternalService);
    readonly #msgDialog = inject(MessageDialogService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #cdf = inject(ChangeDetectorRef);

    #channel: Channel;

    @ViewChildren('section') sections!: QueryList<ElementRef<HTMLElement>>;

    venueTemplates$ = this.#venueTplsSrv.getVenueTemplatesListData$();

    capacities$ = this.#channelMemberSrv.capacities.get$();
    channelCapacities$ = this.#channelMemberSrv.channelCapacities.get$();
    loading$ = booleanOrMerge([
        this.#channelMemberSrv.channelCapacities.updateMappingInProgress$(),
        this.#channelMemberSrv.channelCapacities.loading$()
    ]);

    error$ = combineLatest([
        this.#channelMemberSrv.capacities.error$(),
        this.#channelMemberSrv.channelCapacities.error$()
    ]).pipe(
        filter(errors => errors.some(error => !!error)),
        mapTo(true)
    );

    form: UntypedFormArray;

    ngOnInit(): void {
        this.#channelMemberSrv.channelCapacities.clear();

        this.initializeForm();

        this.#channelsService.getChannel$()
            .pipe(first())
            .subscribe(channel => {
                this.#channel = channel;
                this.#channelMemberSrv.capacities.load(channel.id);
                this.#channelMemberSrv.channelCapacities.load(channel.id);
                this.#venueTplsSrv.loadVenueTemplatesList({
                    limit: 999,
                    offset: 0,
                    sort: 'name:asc',
                    status: [VenueTemplateStatus.active],
                    entityId: this.#channel.entity.id,
                    has_avet_mapping: true
                });
            });

    }

    add(): void {
        const main = this.form.value?.length === 0 || false;
        this.addForm({ main });
        this.form.markAsDirty();
        this.#cdf.detectChanges();
        this.sections?.last?.nativeElement?.scrollIntoView({ inline: 'center' });
    }

    main(index: number): void {
        this.form.controls.forEach((control, i) => {
            if (i === index) {
                return;
            }
            control.patchValue({ main: false });
            control.get('venue_template_id').disable();
        });
        this.form.at(index).patchValue({ main: true });
        this.form.at(index).get('venue_template_id').enable();
        this.form.markAsDirty();
    }

    delete(index: number): void {
        this.#msgDialog.showDeleteConfirmation({
            confirmation: {
                title: 'CHANNELS.MEMBER_EXTERNAL.CAPACITIES.DELETE_TITLE',
                message: 'CHANNELS.MEMBER_EXTERNAL.CAPACITIES.DELETE_MESSAGE'
            },
            delete$: this.remove(index)
        });
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            return this.#channelMemberSrv.channelCapacities.save(this.#channel.id, this.form.value).pipe(
                tap(() => this.form.markAsPristine())
            );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg();
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this.initializeForm();
    }

    updateMappings(): void {
        this.#channelMemberSrv.channelCapacities.updateMapping(this.#channel.id).subscribe();
    }

    private initializeForm(): void {
        this.form = new UntypedFormArray([]);
        this.#channelMemberSrv.channelCapacities.get$()
            .pipe(first(val => !!val))
            .subscribe(capacities => capacities.forEach(elem => this.addForm(elem)));
    }

    private addForm(
        { main = false, name, id, virtual_zone_id: zoneId, venue_template_id: venueId }: Partial<ChannelCapacity>
    ): void {
        this.form.push(this.#fb.group({
            name: [name, [Validators.required]],
            id: [id, [Validators.required]],
            virtual_zone_id: [zoneId, []],
            venue_template_id: [{ value: venueId, disabled: !main }, [Validators.required]],
            main: [main, [Validators.required]]
        }));
    }

    private remove(index: number): Observable<boolean> {
        return defer(() => {
            this.form.removeAt(index);
            this.form.markAsDirty();
            this.#cdf.detectChanges();
            return of(true);
        });
    }

}
