import { Terminal, TerminalsService } from '@admin-clients/cpanel-channels-terminals-data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { EMPTY, of, race, switchMap } from 'rxjs';
import { first } from 'rxjs/operators';

export const terminalDetailsResolver: ResolveFn<Terminal> = (route: ActivatedRouteSnapshot) => {
    const router = inject(Router);
    const terminalsSrv = inject(TerminalsService);
    const breadcrumbsService = inject(BreadcrumbsService);
    const id = Number(route.paramMap.get('terminalId'));
    if (!isNaN(id)) {
        terminalsSrv.terminal.load(Number(id));
        return race([terminalsSrv.terminal.get$(), terminalsSrv.terminal.error$()])
            .pipe(
                first(Boolean),
                switchMap(result => {
                    if (result instanceof HttpErrorResponse) {
                        router.navigate(['/terminals']);
                        return EMPTY;
                    } else {
                        breadcrumbsService.addDynamicSegment(route, result.name);
                        return of(result);
                    }
                })
            );
    } else {
        router.navigate(['/terminals']);
        return EMPTY;
    }
};
