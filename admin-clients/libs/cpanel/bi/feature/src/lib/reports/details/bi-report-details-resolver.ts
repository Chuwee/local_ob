import { BiReport, BiReportPrompt, BiService } from '@admin-clients/cpanel/bi/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, first, mergeMap, of } from 'rxjs';

export const biReportDetailsResolver: ResolveFn<{ biReport: BiReport; biReportPrompts: BiReportPrompt[] }> = (route: ActivatedRouteSnapshot) => {
    const biSrv = inject(BiService);
    const breadcrumbsSrv = inject(BreadcrumbsService);
    const router = inject(Router);
    const auth = inject(AuthenticationService);

    const id = route.paramMap.get('biReportId');
    const impersonation = route.queryParamMap.get('userId');

    auth.impersonation.set(impersonation);

    biSrv.report.load(id, { impersonation });
    biSrv.reportPrompts.load(id, { impersonation });
    return combineLatest([
        biSrv.report.get$(),
        biSrv.report.error$(),
        biSrv.reportPrompts.get$(),
        biSrv.reportPrompts.error$()
    ]).pipe(
        first(([biReport, biReportError, biReportPrompts, biReportPromptsError]) =>
            (!!biReport && !!biReportPrompts) || !!biReportError || !!biReportPromptsError),
        mergeMap(([biReport, biReportError, biReportPrompts, biReportPromptsError]) => {
            if (biReportError || biReportPromptsError) {
                biSrv.report.clear();
                biSrv.reportPrompts.clear();
                router.navigate(['/bi-reports']);
                return EMPTY;
            }
            if (route?.data?.['breadcrumb']) {
                breadcrumbsSrv.addDynamicSegment(route.data['breadcrumb'], biReport.name);
            }
            return of({ biReport, biReportPrompts });
        })
    );
};
