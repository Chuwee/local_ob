import { Injectable, inject } from '@angular/core';
import { Router, RoutesRecognized } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { filter } from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class BreadcrumbService {
    private readonly _router = inject(Router);
    private readonly _translateService = inject(TranslateService);

    private _breadcrumbs: string[] = [];

    init(): void {
        this._router.events
            .pipe(
                filter((event: RoutesRecognized) => event instanceof RoutesRecognized)
            )
            .subscribe((event: RoutesRecognized) => {
                const url = event.urlAfterRedirects;
                const segments = url.split('/');

                this._breadcrumbs = segments.map(url => {
                    const segment = url === '' ? 'home' : url;
                    const lastSlashIndex = segment.lastIndexOf('/');
                    const textAfterLastSlash = segment.substring(lastSlashIndex + 1);
                    const textAfterQuestionMark = textAfterLastSlash.split('?')[0];
                    const key = 'BREADCRUMBS.' + textAfterQuestionMark.toUpperCase();
                    const translation = this._translateService.instant(key);
                    return translation !== key ? translation : textAfterQuestionMark;
                });
            });
    }

    getBreadcrumbs(): string[] {
        return this._breadcrumbs;
    }
}
