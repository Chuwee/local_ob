import { Metadata } from '@OneboxTM/utils-state';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { SearchablePaginatedSelectionLoadEvent, SearchablePaginatedSelectionModule } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { SectorElement, StdVenueTplService, StdVenueTplsState } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, Input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';
import { filter, first, map, shareReplay } from 'rxjs/operators';

@Component({
    imports: [
        NgIf, AsyncPipe,
        FlexLayoutModule,
        ReactiveFormsModule,
        MaterialModule,
        TranslatePipe,
        SearchablePaginatedSelectionModule
    ],
    selector: 'app-event-attendants-block-edit-sectors',
    templateUrl: './attendants-block-edit-sectors.component.html',
    styleUrls: ['./attendants-block-edit-sectors.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [StdVenueTplsState, StdVenueTplService]
})
export class EventAttendantsBlockEditSectorsComponent implements OnInit {
    private readonly _eventsService = inject(EventsService);
    private readonly _venueTplService = inject(StdVenueTplService);
    private readonly _destroyRef = inject(DestroyRef);
    private readonly _changeRef = inject(ChangeDetectorRef);

    private readonly _sectorsPaged = new BehaviorSubject<SectorElement[]>(null);
    private readonly _sectorsMetadataPaged = new BehaviorSubject<Metadata>(null);
    readonly sectorsPaged$ = this._sectorsPaged.asObservable();

    @Input() form: FormControl<number[]>;
    readonly sectorsForm = new FormControl<SectorElement[]>([], Validators.required);
    readonly sectorsMetadata$ = this._sectorsMetadataPaged.asObservable();
    readonly sectorsListPageSize = 10;
    readonly sectors$ = this._venueTplService.getSectors$()
        .pipe(
            filter(Boolean),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly totalSectors$ = this.sectorsMetadata$.pipe(first(Boolean), map(meta => meta.total));
    readonly sectorsLoading$ = this._venueTplService.isSectorLoading$();

    selectedOnly = false;

    get selectedSectors(): number {
        return this.form.value?.length || 0;
    }

    ngOnInit(): void {
        this._eventsService.event.get$()
            .pipe(filter(Boolean), takeUntilDestroyed(this._destroyRef))
            .subscribe(event => {
                // avet events have only 1 template
                this._venueTplService.loadSectors(event.venue_templates[0].id);
            });

        this.sectors$
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this._destroyRef)
            ).subscribe(sectors => {
                if (this.form?.value) {
                    const disallowedSectors = sectors.filter(sector => this.form.value.find(value => value === sector.id));
                    this.sectorsForm.patchValue(disallowedSectors, { onlySelf: true });
                    this.form.markAsPristine();
                    this.form.markAsUntouched();
                }
            });

        this.form.valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(() => {
                this.sectorsForm.markAsTouched();
                this._changeRef.detectChanges();
            });

        this.sectorsForm.valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(selectedSectors => {
                if (selectedSectors) {
                    this.form.patchValue(selectedSectors.map(selectedSector => selectedSector.id), { emitEvent: false });
                }
                this.form.markAsDirty();
            });
    }

    sectorsFilterChangeHandler({ offset = 0, q }: SearchablePaginatedSelectionLoadEvent): void {
        this._venueTplService.getSectors$().pipe(first(Boolean)).subscribe(sectors => {
            if (this.selectedOnly) {
                sectors = sectors.filter(sector =>
                    this.sectorsForm.value.find(selectedSector => selectedSector.id === sector.id)
                );
            }
            if (q) {
                sectors = sectors.filter(sector =>
                    sector.name?.toLowerCase().includes(q.toLowerCase())
                );
            }
            this._sectorsPaged.next(sectors.slice(offset, offset + this.sectorsListPageSize));
            this._sectorsMetadataPaged.next(new Metadata({ total: sectors.length, offset, limit: this.sectorsListPageSize }));
        });
    }

}
