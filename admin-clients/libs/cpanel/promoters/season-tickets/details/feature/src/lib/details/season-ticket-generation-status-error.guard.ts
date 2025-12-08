import {
    SeasonTicketGenerationStatus, GetSeasonTicketStatusResponse, SeasonTicketsService
} from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { Location } from '@angular/common';
import { inject } from '@angular/core';
import { CanActivateFn, CanMatchFn, PRIMARY_OUTLET, Router, UrlTree } from '@angular/router';
import { combineLatest, Observable, of } from 'rxjs';
import { first, map, switchMap, take } from 'rxjs/operators';

export const seasonTicketGenerationStatusErrorCanActivateGuard: CanActivateFn = () => checkGenerationStatus$();
export const seasonTicketGenerationStatusErrorCanMatchGuard: CanMatchFn = () => checkGenerationStatus$();

function checkGenerationStatus$(): Observable<boolean> {
    const SEASON_TICKET_PATH = 'season-tickets';
    const router = inject(Router);
    const seasonTicketsSrv = inject(SeasonTicketsService);
    const location = inject(Location);

    return seasonTicketsSrv.seasonTicketStatus.get$().pipe(
        take(1),
        switchMap((normalAppNavigationSeasonTicketStatus: GetSeasonTicketStatusResponse) => {
            // If we enter by Url, the router params are not yet available.
            const urlObj: UrlTree = router.parseUrl(location.path());
            // We are in the primary outlet
            const seasonTicketIndex = urlObj.root.children[PRIMARY_OUTLET].segments.findIndex(
                urlSegment => urlSegment.path === SEASON_TICKET_PATH
            );
            if (seasonTicketIndex > -1 && seasonTicketIndex + 1 < urlObj.root.children[PRIMARY_OUTLET].segments.length) {
                // We are in season-ticket lazy module
                if (!normalAppNavigationSeasonTicketStatus) {
                    // It has temporal coupling with the angular native router order events and the own routing
                    // structure of the season ticket lazy module. Entering by Url to the elements guarded
                    // (all except season-ticket-general-data), the can activate guards are the first to be
                    // checked, so we don't have seasonTicketStatus state.
                    // So, if we enter by Url, the router is not yet available, so we have to extract the seasonTicketId
                    // from the url because season-ticket-status is not available and the router params are not yet available
                    const seasonTicketIdByUrl = urlObj.root.children[PRIMARY_OUTLET].segments[seasonTicketIndex + 1].path;
                    seasonTicketsSrv.seasonTicketStatus.load(seasonTicketIdByUrl);
                    return combineLatest([
                        seasonTicketsSrv.seasonTicketStatus.get$(),
                        seasonTicketsSrv.seasonTicketStatus.error$()
                    ]).pipe(
                        first(([seasonTicketStatus, errorSeasonTicketStatus]) =>
                            seasonTicketStatus !== null ||
                            errorSeasonTicketStatus !== null
                        ),
                        map(([seasonTicketStatus, errorSeasonTicket]) => {
                            if (errorSeasonTicket) {
                                router.navigate([`/${SEASON_TICKET_PATH}`]);
                                return false;
                            }
                            return checkErrorStatus(seasonTicketStatus, router, SEASON_TICKET_PATH);
                        })
                    );
                } else {
                    // It has temporal coupling with the angular native router order events and the own routing
                    // structure of the season ticket lazy module.Entering by normal app navigation,
                    // the season-ticket-details.resolver.service.ts is the first to be checked,
                    // so we already have seasonTicketStatus state
                    return of(null).pipe(
                        map(() => checkErrorStatus(normalAppNavigationSeasonTicketStatus, router, SEASON_TICKET_PATH))
                    );
                }
            } else {
                // We are not season-ticket lazy module
                return of(null).pipe(
                    map(() => { router.navigate([`/${SEASON_TICKET_PATH}`]); return false; })
                );
            }
        }
        )
    );
}

function checkErrorStatus(seasonTicketStatus: GetSeasonTicketStatusResponse, router: Router, path: string): boolean {
    if (seasonTicketStatus.generation_status === SeasonTicketGenerationStatus.error) {
        router.navigate([`/${path}/${seasonTicketStatus.season_ticket_id}/general-data`]);
        return false;
    }
    return true;
}
