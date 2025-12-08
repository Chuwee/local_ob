import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import { BiSupersetService } from '@admin-clients/cpanel/bi/data-access';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, inject, signal, viewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { embedDashboard } from '@superset-ui/embedded-sdk';
import { catchError, of } from 'rxjs';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatButton } from '@angular/material/button';

const DEFAULT_DASHBOARD = {
    'pro': '78ad23b3-481e-43ee-a4d1-7e1ffb2b7bf7',
    'pre': 'c8342207-3d39-4570-b40e-46e966dfd185'
};

/**
 * EventEmbeddedDashboardComponent
 * 
 * This component uses the official Apache Superset Embedded SDK to embed Superset dashboards
 * within the event details page. It provides a more secure and robust way to display
 * Superset dashboards compared to iframe embedding.
 * 
 * Features:
 * - Uses @superset-ui/embedded-sdk for secure embedding
 * - Environment-aware Superset URL configuration
 * - Loading states and error handling
 * - Retry functionality
 * - Responsive design
 * - Uses Angular signals for reactive state management
 * - Proper view lifecycle management with AfterViewInit
 * 
 * Usage:
 * Navigate to /events/{eventId}/superset/{dashboardId} to view an embedded dashboard
 * 
 * The component fetches an embedded token from the API endpoint:
 * /bi-api/v1/reports/embedded/{dashboardId}/token
 * 
 * @see https://github.com/apache/superset/tree/master/superset-embedded-sdk
 */
@Component({
    selector: 'ob-superset-dashboard',
    standalone: true,
    imports: [MatProgressSpinner, MatButton],
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.css']
})
export class SupersetDashboardComponent implements AfterViewInit {
    readonly #route = inject(ActivatedRoute);
    readonly #supersetSrv = inject(BiSupersetService);
    readonly #env = inject(ENVIRONMENT_TOKEN);
    readonly #cdr = inject(ChangeDetectorRef);

    dashboardContainer = viewChild<ElementRef>('dashboardContainer');

    loading = signal(true);
    error = signal<string | null>(null);

    ngAfterViewInit(): void {
        this.loadDashboard();
    }

    retry(): void {
        this.loadDashboard();
    }

    private loadDashboard(): void {
        const dashboardId = this.#route.snapshot.paramMap.get('dashboardId') || DEFAULT_DASHBOARD[this.#env.env];

        if (!dashboardId) {
            this.error.set('Dashboard ID is required');
            this.loading.set(false);
            return;
        }

        this.loading.set(true);
        this.error.set(null);

        this.#supersetSrv.getEmbeddedToken(dashboardId)
            .pipe(
                catchError(err => {
                    console.error('Error loading embedded token:', err);
                    return of(null);
                })
            )
            .subscribe(response => {
                this.loading.set(false);
                this.#cdr.detectChanges();

                if (response?.token) {
                    this.embedSupersetDashboard(dashboardId, response.token);
                } else {
                    this.error.set('Failed to load dashboard token');
                }
            });
    }

    private async embedSupersetDashboard(dashboardId: string, token: string): Promise<void> {
        try {
            const container = this.dashboardContainer();

            if (!container) {
                this.error.set('Dashboard container not found');
                return;
            }

            const isProd = this.#env.env === 'pro';
            const supersetUrl = isProd ? 'https://dash.oneboxtds.com' : 'https://dash.oneboxtds.net';

            await embedDashboard({
                id: dashboardId,
                supersetDomain: supersetUrl,
                mountPoint: container.nativeElement,
                fetchGuestToken: () => Promise.resolve(token),
                dashboardUiConfig: {
                    hideTitle: true,
                    hideChartControls: true,
                    hideTab: false,
                    filters: {
                        expanded: false
                    }
                }
            });
        } catch (error) {
            console.error('Error embedding Superset dashboard:', error);
            this.error.set('Failed to load dashboard');
        }
    }

}
