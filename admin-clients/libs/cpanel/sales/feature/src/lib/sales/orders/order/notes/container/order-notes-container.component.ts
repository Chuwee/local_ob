import { OrderNotesStateMachine, OrderNotesApi, OrderNotesService, OrderNotesState } from '@admin-clients/cpanel-sales-data-access';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { Subject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
    selector: 'app-order-notes-container',
    templateUrl: './order-notes-container.component.html',
    styleUrls: ['./order-notes-container.component.scss'],
    providers: [
        OrderNotesApi,
        OrderNotesState,
        OrderNotesService,
        OrderNotesStateMachine
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class OrderNotesContainerComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    sidebarWidth$: Observable<string>;
    constructor(private _breakpointObserver: BreakpointObserver) { }

    ngOnInit(): void {
        this.model();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    private model(): void {
        this.sidebarWidth$ = this._breakpointObserver
            .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium])
            .pipe(
                map(result => result.matches ? '240px' : '290px')
            );
    }

}
