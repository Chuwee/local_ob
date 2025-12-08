import { BiReport } from '@admin-clients/cpanel/bi/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { Platform } from '@angular/cdk/platform';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { first } from 'rxjs';
import { BI_SUBMIT } from '../../bi-reports.routes';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-report-list-element',
    styleUrls: ['./bi-report-list-element.component.scss'],
    imports: [
        CommonModule,
        RouterModule
    ],
    templateUrl: './bi-report-list-element.component.html'
})
export class BiReportListElementComponent {
    readonly #auth = inject(AuthenticationService);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    readonly #platform = inject(Platform);
    readonly #biSubmit = inject(BI_SUBMIT);
    @Input() biReport: BiReport;

    load(report: BiReport): void {
        if (report.url) {
            this.#auth.getLoggedUser$()
                .pipe(first())
                .subscribe(user => {
                    this.#biSubmit(report.url, user.reports.load, user.reports.logout, this.#platform);
                });
        } else {
            this.#router.navigate([report.id], { relativeTo: this.#route, queryParamsHandling: 'preserve' });
        }
    }
}