import { APP_NAME } from '@admin-clients/shared/core/data-access';
import { inject, Injectable } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, ActivatedRouteSnapshot, NavigationEnd, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, distinctUntilChanged, Observable, ReplaySubject } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Breadcrumb } from './breadcrumb.model';

@Injectable({
    providedIn: 'root'
})
export class BreadcrumbsService {
    private readonly _dynamicSegmentsMap = new Map<string, string>();
    private readonly _dynamicSegments = new BehaviorSubject(this._dynamicSegmentsMap);
    private readonly _dynamicSegments$ = this._dynamicSegments.asObservable();
    private readonly _breadcrumbs = new ReplaySubject<Breadcrumb[]>(1);
    private readonly _breadcrumbs$ = this._breadcrumbs.asObservable();
    private readonly _router = inject(Router);
    private readonly _route = inject(ActivatedRoute);
    private readonly _titleService = inject(Title);
    private readonly _translateService = inject(TranslateService);
    private readonly _appName = inject(APP_NAME);

    startListener(): void {
        combineLatest([
            this._router.events.pipe(
                filter(event => event instanceof NavigationEnd),
                distinctUntilChanged()
            ),
            this._dynamicSegments$
        ])
            .pipe(
                map(([_, dynamicSegments]) => {
                    let breadcrumbs = [{
                        label: 'TITLES.HOME',
                        url: '/'
                    }];
                    if (this._route.root.firstChild) {
                        breadcrumbs = this.buildBreadCrumb(this._route.root.firstChild, '', breadcrumbs);
                    }
                    breadcrumbs.forEach(breadcrumb => {
                        if (dynamicSegments.has(breadcrumb.label)) {
                            breadcrumb.label = dynamicSegments.get(breadcrumb.label);
                        }
                    });
                    return breadcrumbs;
                })
            )
            .subscribe(breadcrumbs => {
                this._breadcrumbs.next(breadcrumbs);
                const title = breadcrumbs.slice(1).map(bc => this._translateService.instant(bc.label)).join(' > ');
                this._titleService.setTitle(`${title ? title + ' - ' : ''}` + this._appName);
            }
            );
    }

    addDynamicSegment(key: string | ActivatedRouteSnapshot, value: string): void {
        if (key) {
            if (key instanceof ActivatedRouteSnapshot) {
                if (key.data['breadcrumb']) {
                    this.addDynamicSegment(key.data['breadcrumb'], value);
                }
            } else {
                this._dynamicSegments.next(this._dynamicSegmentsMap.set(key, value ?? key));
            }
        }
    }

    getBreadcrumbs$(): Observable<Breadcrumb[]> {
        return this._breadcrumbs$;
    }

    private buildBreadCrumb(route: ActivatedRoute, url = '', breadcrumbs: Breadcrumb[] = []): Breadcrumb[] {
        // get the route's URL segment
        const path = route.snapshot.url.map(segment => segment.path).join('/');
        if (route.routeConfig && path) {
            // append route URL to url
            url += `/${path}`;
            // verify the custom data property "breadcrumb" is specified on the route
            if (route.routeConfig.data) {
                const label = route.routeConfig.data['breadcrumb'];
                // add breadcrumb
                const breadcrumb = {
                    label,
                    url
                };
                breadcrumbs.push(breadcrumb);
            }
        }
        if (route.firstChild) {
            // if we are not on our current path yet,
            // there will be more children to look after, to build our breadcumb
            return this.buildBreadCrumb(route.firstChild, url, breadcrumbs);
        }

        // return if there are no more children
        return breadcrumbs;
    }
}
