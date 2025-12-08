import { Metadata } from '@OneboxTM/utils-state';
import { EventSessionsService, Session } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { SearchablePaginatedSelectionModule } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { StdVenueTplService, StdVenueTplsState, VenueTemplateView } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { ChangeDetectionStrategy, Component, EventEmitter, OnDestroy, OnInit } from '@angular/core';
import { ControlContainer, ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { Sort } from '@angular/material/sort';
import { TranslatePipe } from '@ngx-translate/core';
import { EMPTY, Observable } from 'rxjs';
import { filter, map, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-venue-template-vip-views',
    templateUrl: './venue-template-vip-views.component.html',
    styleUrls: ['./venue-template-vip-views.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [StdVenueTplsState, StdVenueTplService],
    imports: [
        SearchablePaginatedSelectionModule, MaterialModule, ReactiveFormsModule, TranslatePipe
    ]
})
export class VenueTemplateVipViewsComponent implements OnInit, OnDestroy {

    private _session: Session;
    private _filters: PageableFilter;
    private _onDestroy = new EventEmitter();

    columns = ['name', 'description', 'vip'];
    views$: Observable<VenueTemplateView[]>;
    metadata$: Observable<Metadata>;
    isLoading$: Observable<boolean>;
    form: UntypedFormGroup;
    formGroup: UntypedFormGroup;
    formGroupName = 'vip-views';

    constructor(
        private _stdVenueTplSrv: StdVenueTplService,
        private _sessionsService: EventSessionsService,
        private _controlContainer: ControlContainer,
        private _fb: UntypedFormBuilder
    ) { }

    ngOnInit(): void {
        this.formGroup = this._controlContainer.control as UntypedFormGroup;

        this.isLoading$ = this._stdVenueTplSrv.isVenueTemplateViewsLoading$();

        this._sessionsService.session.get$()
            .pipe(
                filter(session => !!session),
                takeUntil(this._onDestroy)
            )
            .subscribe(session => {
                this.form = this._fb.group({});
                this.formGroup.setControl(this.formGroupName, this.form);
                if (this._session) {
                    this._session = session;
                    this.loadViews();
                }
                this._session = session;
            });

        this.views$ = this._stdVenueTplSrv.getVenueTemplateViews$()
            .pipe(
                filter(views => !!views?.data),
                map(views => views.data),
                tap(views => this._views = views)
            );

        this.metadata$ = this._stdVenueTplSrv.getVenueTemplateViews$()
            .pipe(
                filter(views => !!views?.metadata),
                map(views => views.metadata)
            );
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    save$(): Observable<void> {
        if (this.form.dirty) {
            return this._stdVenueTplSrv.updateVenueTemplateVipViews(
                this._session.venue_template.id,
                this._views,
                this._session.id
            );
        } else {
            return EMPTY;
        }
    }

    changeSort(sort: Sort): void {
        this.loadViews({ sort: `${sort.active}:${sort.direction}` });
    }

    loadViews(filters?: PageableFilter): void {
        this._filters = { ...this._filters, ...filters };
        this._stdVenueTplSrv.loadVenueTemplateViews(this._session.venue_template.id, this._filters, this._session.id);
    }

    private get _views(): Partial<VenueTemplateView>[] {
        const viewIds: string[] = Object.keys(this.form.value);
        const values: Record<string, boolean> = this.form.value;

        return viewIds.map(id => ({ id: +id, vip: values[id] ?? false }));
    }

    private set _views(views: Partial<VenueTemplateView>[]) {
        views.forEach(view => this.form.addControl(`${view.id}`, this._fb.control(view.vip)));
    }

}
