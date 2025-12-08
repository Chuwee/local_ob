import { ExternalAtmApi, PutATMMemberPriceZones } from '@admin-clients/cpanel/external/data-access';
import { Event } from '@admin-clients/cpanel/promoters/events/data-access';
import { ContextNotificationComponent, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import {
    VenueTemplate, VenueTemplatesService, VenueTemplateStatus
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe, NgForOf, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, Input, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';
import { finalize, map, shareReplay, tap, filter, distinctUntilChanged } from 'rxjs/operators';
import { AtmMemberType } from './atm-member-type.enum';

/**
 * ATM ad hoc component, to set up member price-zones in DDBB
 */
@Component({
    selector: 'app-atm-member-price-zones',
    templateUrl: 'atm-member-price-zones.component.html',
    styleUrls: ['atm-member-price-zones.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule,
        ReactiveFormsModule,
        MaterialModule,
        NgIf, NgForOf,
        AsyncPipe,
        TranslatePipe,
        ContextNotificationComponent
    ]
})
export class AtmMemberPriceZonesComponent implements OnInit {
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #externalAtmApi = inject(ExternalAtmApi);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);
    readonly #onDestroy = inject(DestroyRef);
    readonly #savingBS = new BehaviorSubject(false);

    @Input() event: Event;
    readonly memberTypes = Object.values(AtmMemberType);

    readonly venueTplCtrl = this.#fb.control(null as VenueTemplate);
    readonly memberZonesForm = this.#fb.group({});
    readonly venueTpls$ = this.#venueTemplatesSrv.getVenueTemplatesListData$().pipe(
        tap(templates => templates?.length && this.venueTplCtrl.reset(templates[0])),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    readonly priceTypes$ = this.#venueTemplatesSrv.getVenueTemplatePriceTypes$().pipe(
        filter(Boolean),
        tap(priceZones => {
            priceZones.forEach(zone => {
                const hasMemberType = !!zone.code && Object.values(AtmMemberType).includes(zone.code as AtmMemberType);
                this.memberZonesForm.setControl(
                    `${zone.id}`,
                    this.#fb.control(hasMemberType ? zone.code as AtmMemberType : null)
                );
            });
        }),
        map(zones => zones?.sort((a, b) => a.code.localeCompare(b.code))),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    readonly inProgress$ = booleanOrMerge([
        this.#venueTemplatesSrv.isVenueTemplatesListLoading$(),
        this.#venueTemplatesSrv.isVenueTemplatePriceTypesLoading$(),
        this.#savingBS.asObservable()
    ]);

    ngOnInit(): void {
        this.#venueTemplatesSrv.loadVenueTemplatesList({
            limit: 999,
            offset: 0,
            sort: 'name:asc',
            eventId: this.event.id,
            status: [VenueTemplateStatus.active]
        });

        this.venueTplCtrl.valueChanges
            .pipe(
                filter(Boolean),
                distinctUntilChanged(),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(venueTpl => {
                this.loadPriceTypes(venueTpl.id);
            });
    }

    save(): void {
        if (this.valid && !this.#savingBS.value) {
            this.#savingBS.next(true);
            const body: PutATMMemberPriceZones = {
                price_type_codes: Object.keys(this.memberZonesForm.value)
                    .filter(key => !!this.memberZonesForm.value[key])
                    .map(key => ({
                        price_type_id: parseInt(key),
                        member_type: this.memberZonesForm.value[key]
                    }))
            };
            this.#externalAtmApi.memberPriceZones(this.event.id, this.venueTplCtrl.value?.id, body)
                .pipe(finalize(() => this.#savingBS.next(false)))
                .subscribe(() => {
                    this.#ephemeralMessageService.showSaveSuccess();
                    this.memberZonesForm.reset();
                    this.loadPriceTypes(this.venueTplCtrl.value?.id);
                });
        }
    }

    setMemberType(memberTypeId: string, zoneId: number): void {
        Object.keys(this.memberZonesForm.value).forEach(key => {
            this.memberZonesForm.value[key] === memberTypeId && `${zoneId}` !== key ?
                this.memberZonesForm.get([key]).setValue(null) : null;
        });
    }

    loadPriceTypes(venueTplId: number): void {
        this.#venueTemplatesSrv.loadVenueTemplatePriceTypes(venueTplId);
    }

    get valid(): boolean {
        return Object.values(this.memberZonesForm.value).filter(Boolean).length === Object.values(AtmMemberType).length;
    }

}
