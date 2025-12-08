import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { TourListFilters, TourStatus, ToursService } from '@admin-clients/cpanel/promoters/tours/data-access';
import { EntitiesBaseService, Entity, EntitiesFilterFields } from '@admin-clients/shared/common/data-access';
import { SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { animate, style, transition, trigger } from '@angular/animations';
import { AsyncPipe, NgForOf, NgIf } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatDateFormats, MAT_DATE_FORMATS } from '@angular/material/core';
import { SatPopoverComponent, SatPopoverModule } from '@ncstate/sat-popover';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, of } from 'rxjs';
import { shareReplay, switchMap } from 'rxjs/operators';

@Component({
    selector: 'app-tours-list-filter',
    templateUrl: './tours-list-filter.component.html',
    styleUrls: ['./tours-list-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: [
        trigger('inOutAnimation', [
            transition(':enter', [
                style({ height: 0, opacity: 0 }),
                animate('300ms ease-out', style({ height: '*', opacity: 1 }))
            ]),
            transition(':leave', [
                style({ height: '*', opacity: 1 }),
                animate('300ms ease-in', style({ height: 0, opacity: 0 }))
            ])
        ])
    ],
    imports: [
        MaterialModule,
        ReactiveFormsModule,
        FlexLayoutModule,
        TranslatePipe,
        SatPopoverModule,
        SelectSearchComponent,
        NgIf, NgForOf,
        AsyncPipe,
        EllipsifyDirective
    ]
})
export class ToursListFilterComponent implements OnInit, AfterViewInit {
    @ViewChild('filterPopover')
    private _filterPopover: SatPopoverComponent;

    entities$: Observable<Entity[]>;
    statusList = Object.values(TourStatus)
        .map(type => ({ id: type, name: `TOUR.STATUS_OPTS.${type}` }));

    filtersForm: UntypedFormGroup;

    readonly canSelectEntity$ = this._auth.canReadMultipleEntities$();

    constructor(
        private _fb: UntypedFormBuilder,
        private _entitiesSrv: EntitiesBaseService,
        private _toursSrv: ToursService,
        private _auth: AuthenticationService,
        @Inject(MAT_DATE_FORMATS) private readonly formats: MatDateFormats
    ) { }

    ngOnInit(): void {
        // Init reactive form:
        this.filtersForm = this._fb.group({
            entityId: null,
            status: [[
                TourStatus.active,
                TourStatus.inactive
            ]]
        });
        // Map data observables to component:
        this.entities$ = this.canSelectEntity$.pipe(
            switchMap(canSelectEntity => {
                if (canSelectEntity) {
                    this._entitiesSrv.entityList.load({
                        limit: 999,
                        sort: 'name:asc',
                        fields: [EntitiesFilterFields.name]
                    });
                    return this._entitiesSrv.entityList.getData$();
                }
                return of([]);
            }),
            shareReplay(1)
        );
    }

    ngAfterViewInit(): void {
        // Initial filters aplication:
        this.onApplyBtnClick();
    }

    onCloseBtnClick(): void {
        this._filterPopover.close();
    }

    onApplyBtnClick(): void {
        const filters: TourListFilters = {};

        if (this.filtersForm.value.entityId) {
            filters.entityId = this.filtersForm.value.entityId;
        }
        if (this.filtersForm.value.status?.length === 0) {
            this.filtersForm.controls['status'].setValue(Object.values(TourStatus));
        }
        if (this.filtersForm.value.status && this.filtersForm.value.status.length !== this.statusList.length) {
            filters.status = this.filtersForm.value.status;
        }

        this._toursSrv.setTourListFilters(filters);
        this.onCloseBtnClick();
    }

    onRemoveFiltersBtnClick(): void {
        this.filtersForm.reset();
        this.onApplyBtnClick();
    }

}
