Breadcrumbs
===========

Generic breadcrumbs component that detects when actual route contains a RoutingModule tree and some/all of them define a `data` object 
with a `breadcrumb` string property. This property has to contain the `LITERAL_KEY` that will be shown as part of the breadcrumbs UI.

Routing config example:
```
    {
        path: 'events',
        ...
        data: {
            breadcrumb: 'TITLES.MY_EVENTS'
        }
    }
```

The component also allows to set dynamic breadcrumbs segments, exposing `BreadcrumbsService` for mapping it. The only thing you have 
to do in order to map some segment to a dynamic value is to call `addDynamicSegment('LITERAL_KEY', 'dynamic substitutive value')`
**before** `NavigationEnd` routing event occurs (inside a resolver, for example).

Dynamic segment config example:
```
    resolve(route: ActivatedRouteSnapshot): Observable<Event> | Observable<never> {
        ...
        return combineLatest([
            this.eventsService.getEvent$(),
            this.eventsService.getEventError$()
        ])
            .pipe(
                ...
                mergeMap(([event, error]) => {
                    ...
                    if (route.data && route.data.breadcrumb) {
                        this.breadcrumbsService.addDynamicSegment(route.data.breadcrumb, event.name);
                    }
                    ...
                })
            );
    }
```
