import { DetailOverlayData, DetailOverlayService } from '@OneboxTM/detail-overlay';
import {
    VerticalTimelineComponent, TimelineElement, TimelineElementStatus, EphemeralMessageService, MessageDialogService, DialogSize
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { CustomScrollDirective } from '@admin-clients/shi-panel/utility-directives';
import { UserPermissions } from '@admin-clients/shi-panel/utility-models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, switchMap } from 'rxjs';
import { ListingsService } from '../../listings.service';
import { ListingStatus } from '../../models/listing-status.enum';
import { ListingStockTypes } from '../../models/listing-stock-types.enum';
import { listingTimelineStructure } from '../../models/listing-timeline.model';
import { Transition } from '../../models/listing-transition.model';
import { ListingProductsComponent } from './listing-details-products/listing-details-products.component';

@Component({
    selector: 'app-listing-details',
    templateUrl: './listing-details.component.html',
    styleUrls: ['./listing-details.component.scss', '../listings-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule, FlexLayoutModule, TranslatePipe, VerticalTimelineComponent, DateTimePipe,
        ListingProductsComponent, CustomScrollDirective, MatIcon, MatButton, MatTooltip, MatProgressSpinner
    ]
})
export class ListingDetailsComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #listingsService = inject(ListingsService);
    readonly #authService = inject(AuthenticationService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #detailOverlayService = inject(DetailOverlayService);
    readonly listingStatus = ListingStatus;
    readonly dateTimeFormats = DateTimeFormats;
    readonly stockTypes = ListingStockTypes;

    readonly isLoading$ = booleanOrMerge([
        this.#listingsService.details.loading$(),
        this.#listingsService.transitions.loading$()
    ]);

    readonly data = inject(DetailOverlayData);
    readonly listing$ = this.#listingsService.details.getListingData$()
        .pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#destroyRef)
        );

    readonly hasWritePermissions$ = this.#authService.getLoggedUser$()
        .pipe(map(loggedUser => AuthenticationService.doesUserHaveSomePermission(loggedUser, [UserPermissions.listingWrite])));

    transitions: TimelineElement[];

    ngOnInit(): void {
        this.#listingsService.details.load(this.data.data);
        this.#listingsService.transitions.load(this.data.data);

        this.#listingsService.transitions.get$()
            .pipe(filter(Boolean))
            .subscribe(transitions => this.transitions = this.mapTransitions(transitions));
    }

    showMore(event: { elem: TimelineElement; pos: number }): void {
        if (event.elem.grouped) {
            this.transitions = this.ungroupTransitions(event.elem, event.pos);
        } else if (!event.elem.grouped) {
            this.transitions = this.groupTransition(event.elem, event.pos);
        }
    }

    blacklistListing(listingCode: string, blacklisted: boolean): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: blacklisted ? 'LISTINGS.TITLES.WHITELIST' : 'LISTINGS.TITLES.BLACKLIST',
            message: blacklisted ? 'LISTINGS.INFOS.WHITELIST_DESC' : 'LISTINGS.INFOS.BLACKLIST_DESC',
            actionLabel: blacklisted ? 'LISTINGS.FORMS.WHITELIST' : 'LISTINGS.FORMS.BLACKLIST',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this.#listingsService.details.updateListingBlacklist(listingCode, blacklisted))
            )
            .subscribe(() => {
                this.#ephemeralSrv.showSuccess({
                    msgKey: blacklisted ? 'LISTINGS.INFOS.WHITELISTING_SUCCESS' : 'LISTINGS.INFOS.BLACKLISTING_SUCCESS',
                    msgParams: { listingCode }
                });
                this.#detailOverlayService.close();
            });
    }

    private mapTransitions(transitions: Transition[]): TimelineElement[] {
        const timelineStructure = listingTimelineStructure;
        const timelineElements: TimelineElement[] = [];

        transitions.forEach(transition => {
            const timelineElement = {
                title: transition.action,
                date: transition.date,
                status: transition.status,
                description: transition.description ? transition.description : null,
                elements: []
            };
            timelineElements.push(timelineElement);
        });

        timelineStructure.forEach(element => {
            const index = timelineElements.findIndex(e => e.title === element);
            if (index === -1 && (element !== 'SHI_UPDATE' && element !== 'EXCHANGE_RATE_UPDATE')) {
                timelineElements.push({ title: element, status: TimelineElementStatus.disabled });
            }
        });

        if (!timelineElements.find(e => ['SHI_DELETE', 'INTERNAL_DELETE'].includes(e.title))) {
            timelineElements.push({ title: 'DELETE', status: TimelineElementStatus.disabled });
        }

        const groupedTransitions = this.groupTransitions(timelineElements);
        return groupedTransitions;
    }

    private groupTransitions(transitions: TimelineElement[]): TimelineElement[] {
        let currentStat;
        let currentTitle;
        const agg: TimelineElement[] = [];
        transitions.forEach(transition => {
            if (currentStat === transition.status && currentTitle === transition.title) {
                agg[agg.length - 1].elements.push(transition);
            } else {
                agg.push({
                    title: transition.title,
                    status: transition.status,
                    date: transition.date,
                    description: transition.description,
                    elements: [],
                    grouped: true
                });
            }
            currentStat = transition.status;
            currentTitle = transition.title;
        });

        return agg;
    }

    private groupTransition(transition: TimelineElement, pos: number): TimelineElement[] {
        const groupedTransitions = Object.assign([], this.transitions);
        transition.elements.forEach(() => {
            groupedTransitions.splice(pos + 1, 1);
        });
        groupedTransitions[pos].grouped = true;

        return groupedTransitions;
    }

    private ungroupTransitions(transition: TimelineElement, pos: number): TimelineElement[] {
        const ungroupedTransitions = Object.assign([], this.transitions);
        transition.elements.forEach((elem, i) => {
            ungroupedTransitions.splice(pos + i + 1, 0, elem);
        });
        ungroupedTransitions[pos].grouped = false;

        return ungroupedTransitions;
    }
}
