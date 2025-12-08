import { Tour, ToursService } from '@admin-clients/cpanel/promoters/tours/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { filter, shareReplay, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-tour-details',
    templateUrl: './tour-details.component.html',
    styleUrls: ['./tour-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        RouterModule,
        FlexLayoutModule
    ]
})
export class TourDetailsComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    tour$: Observable<Tour>;
    deepPath$ = getDeepPath$(this._router, this._route);

    private get _breadcrumbSegmentKey(): string | undefined {
        return this._route.snapshot.data['breadcrumb'];
    }

    constructor(
        private _route: ActivatedRoute,
        private _breadcrumbsService: BreadcrumbsService,
        private _toursSrv: ToursService,
        private _router: Router
    ) {
    }

    ngOnInit(): void {
        this.init();
        this.loadDataHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    private init(): void {
        this.tour$ = this._toursSrv.getTour$()
            .pipe(
                filter(tour => !!tour),
                shareReplay(1)
            );
    }

    private loadDataHandler(): void {
        this.tour$
            .pipe(takeUntil(this._onDestroy))
            .subscribe(tour => this._breadcrumbsService.addDynamicSegment(this._breadcrumbSegmentKey, tour.name));
    }
}
