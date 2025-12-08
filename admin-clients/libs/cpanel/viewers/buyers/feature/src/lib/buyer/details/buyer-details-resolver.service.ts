/* eslint-disable @typescript-eslint/dot-notation */
import { BuyersService, Buyer } from '@admin-clients/cpanel-viewers-buyers-data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { combineLatest, EMPTY, Observable, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

@Injectable({
    providedIn: 'any'
})
export class BuyerDetailsResolverService {

    constructor(private _buyersSrv: BuyersService, private _router: Router, private _breadcrumbsService: BreadcrumbsService) {
    }

    resolve(route: ActivatedRouteSnapshot): Observable<Buyer> | Observable<never> {
        this._buyersSrv.loadBuyer(route.paramMap.get('buyerId'));

        return combineLatest([
            this._buyersSrv.getBuyer$(),
            this._buyersSrv.getBuyerError$()
        ])
            .pipe(
                first(([buyer, error]) => buyer !== null || error !== null),
                mergeMap(([buyer, error]) => {
                    if (error) {
                        this._router.navigate(['/buyers']);
                        return EMPTY;
                    }
                    this._breadcrumbsService.addDynamicSegment(route.data['breadcrumb'], buyer.email);
                    return of(buyer);
                })
            );
    }
}
