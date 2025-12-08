import { ActivatedRoute, ActivatedRouteSnapshot, NavigationEnd, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map, startWith } from 'rxjs/operators';

export function getDeepPath(route: ActivatedRoute, deepLevel = 1): string {
    return getLeveledPath(route.snapshot, deepLevel)?.routeConfig?.path;
}

export function getDeepPath$(router: Router, route: ActivatedRoute, deepLevel = 1): Observable<string> {
    return router.events
        .pipe(
            filter(event => event instanceof NavigationEnd),
            startWith(null as NavigationEnd),
            map(() => getLeveledPath(route.snapshot, deepLevel)?.routeConfig?.path)
        );
}

function getLeveledPath(ars: ActivatedRouteSnapshot, deepLevel: number): ActivatedRouteSnapshot {
    if (ars.children?.length) {
        return deepLevel <= 1 ? ars.children[0] : getLeveledPath(ars.children[0], deepLevel - 1);
    } else {
        return null;
    }
}

