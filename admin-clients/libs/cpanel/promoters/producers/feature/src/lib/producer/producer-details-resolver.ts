/* eslint-disable @typescript-eslint/dot-notation */
import { ProducerDetails, ProducersService } from '@admin-clients/cpanel/promoters/producers/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

export const producerDetailsResolver: ResolveFn<ProducerDetails> = (route: ActivatedRouteSnapshot) => {
    const producerSrv = inject(ProducersService);
    const router = inject(Router);
    const breadcrumbsSrv = inject(BreadcrumbsService);

    const id = route.paramMap.get('producerId');
    producerSrv.loadProducer(parseInt(id, 10));

    return combineLatest([
        producerSrv.getProducer$(),
        producerSrv.getProducerError$()
    ])
        .pipe(
            first(([producer, error]) => producer !== null || error !== null),
            mergeMap(([producer, error]) => {
                if (error) {
                    router.navigate(['/producers']);
                    return EMPTY;
                }
                if (route.data?.['breadcrumb']) {
                    breadcrumbsSrv.addDynamicSegment(route.data['breadcrumb'], producer.name);
                }
                return of(producer);
            })
        );
};
