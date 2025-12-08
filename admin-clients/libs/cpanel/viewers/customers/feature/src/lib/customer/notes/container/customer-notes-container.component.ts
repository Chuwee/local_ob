import { CustomerNotesService } from '@admin-clients/cpanel-viewers-customers-data-access';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe, NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatSidenavModule } from '@angular/material/sidenav';
import { RouterOutlet } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { map } from 'rxjs/operators';
import { CustomerNotesStateMachine } from '../customer-notes-state-machine';
import { CustomerNotesListComponent } from '../list/customer-notes-list.component';

@Component({
    selector: 'app-customer-notes-container',
    imports: [FlexLayoutModule, MatSidenavModule, CustomerNotesListComponent, RouterOutlet, AsyncPipe, NgClass],
    templateUrl: './customer-notes-container.component.html',
    styleUrls: ['./customer-notes-container.component.scss'],
    providers: [
        CustomerNotesService,
        CustomerNotesStateMachine
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerNotesContainerComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    sidebarWidth$: Observable<string>;

    constructor(
        private _breakpointObserver: BreakpointObserver
    ) {
    }

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
