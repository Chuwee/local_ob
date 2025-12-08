import { EntitiesService, EntityZoneTemplate } from '@admin-clients/cpanel/organizations/entities/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, of } from 'rxjs';
import { first, mergeMap, switchMap } from 'rxjs/operators';

export const entityZoneTemplateDetailsResolver: ResolveFn<EntityZoneTemplate> = (route: ActivatedRouteSnapshot) => {
    const entitiesSrv = inject(EntitiesService);
    const router = inject(Router);
    const breadcrumbsSrv = inject(BreadcrumbsService);
    const allRouteParams = Object.assign({}, ...route.pathFromRoot.map(path => path.params));
    const templateId = allRouteParams.templateId;
    const entityId = allRouteParams.entityId;

    entitiesSrv.zoneTemplate.clear();
    return entitiesSrv.getEntity$().pipe(
        first(Boolean),
        switchMap(entity => {
            entitiesSrv.zoneTemplate.load(entity.id, templateId);
            return combineLatest([
                entitiesSrv.zoneTemplate.get$(),
                entitiesSrv.zoneTemplate.error$()
            ]);
        }),
        first(([template, templateError]) => !!(template || templateError)),
        mergeMap(([template, templateError]) => {
            if (templateError) {
                router.navigate(['/entities', entityId, 'zone-templates']);
                return EMPTY;
            } else {
                breadcrumbsSrv.addDynamicSegment(route.data?.['breadcrumb'], template.name);
                return of(template);
            }
        })
    );
};
