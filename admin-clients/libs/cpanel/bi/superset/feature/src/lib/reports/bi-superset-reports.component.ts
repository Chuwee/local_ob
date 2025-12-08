import { BiReport } from "@admin-clients/cpanel/bi/data-access";
import { AuthenticationService } from "@admin-clients/cpanel/core/data-access";
import { Directive, inject } from "@angular/core";
import { first } from "rxjs";
import { BI_SUPERSET_SUBMIT } from "./bi-superset-reports.routes";
import { Router } from "@angular/router";
import { Platform } from "@angular/cdk/platform";
import { ENVIRONMENT_TOKEN } from "@OneboxTM/utils-environment";

@Directive()
export abstract class BiSupersetReportsComponent {
    protected readonly auth = inject(AuthenticationService);
    readonly #router = inject(Router);
    readonly #platform = inject(Platform);
    readonly #biSupersetSubmit = inject(BI_SUPERSET_SUBMIT);
    readonly #env = inject(ENVIRONMENT_TOKEN);

    load(report: BiReport): void {
        if (report.url) {
            this.auth.getToken$()
                .pipe(first())
                .subscribe(token => {
                    const isProd = this.#env.env === 'pro';
                    const loginUrl = isProd ? 'https://dash.oneboxtds.com/login/' : 'https://dash.oneboxtds.net/login/';
                    const urlTree = this.#router.createUrlTree([report.url]);
                    const reportUrl = this.#router.serializeUrl(urlTree);
                    this.#biSupersetSubmit(loginUrl, reportUrl, token, this.#platform);
                });
        }
    }
}