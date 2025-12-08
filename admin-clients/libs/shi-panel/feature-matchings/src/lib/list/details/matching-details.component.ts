import { DetailOverlayData, DetailOverlayService } from '@OneboxTM/detail-overlay';
import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { CustomScrollDirective } from '@admin-clients/shi-panel/utility-directives';
import { Matching, MatchingStatus, UserPermissions } from '@admin-clients/shi-panel/utility-models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject, filter, map, switchMap, takeUntil } from 'rxjs';
import { MatchingsApi } from '../../matchings.api';
import { MatchingsService } from '../../matchings.service';
import { MatchingsState } from '../../state/matchings.state';

@Component({
    imports: [
        CommonModule, FlexLayoutModule, TranslatePipe, DateTimePipe,
        MatIconModule, MatProgressSpinnerModule, FlexLayoutModule,
        MatButtonModule, CustomScrollDirective
    ],
    selector: 'app-matching-details',
    templateUrl: './matching-details.component.html',
    styleUrls: ['./matching-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [MatchingsService, MatchingsApi, MatchingsState]
})
export class MatchingDetailsComponent implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    private readonly _matchingsService = inject(MatchingsService);
    private readonly _authService = inject(AuthenticationService);
    private readonly _msgDialogSrv = inject(MessageDialogService);
    private readonly _detailOverlayService = inject(DetailOverlayService);

    readonly matchingStatus = MatchingStatus;
    readonly dateTimeFormats = DateTimeFormats;

    readonly isLoading$ = this._matchingsService.details.loading$();

    readonly hasWritePermissions$: Observable<boolean> = this._authService.getLoggedUser$()
        .pipe(map(loggedUser => AuthenticationService.doesUserHaveSomePermission(loggedUser, [UserPermissions.mappingWrite])));

    readonly overlayData = inject(DetailOverlayData);
    matching: Matching;
    shiTaxonomiesList = '-';
    suplTaxonomiesList = '-';

    ngOnInit(): void {
        this._matchingsService.details.load(this.overlayData.data.supplier, this.overlayData.data.id);

        this._matchingsService.details.getMatchingData$()
            .pipe(
                filter(Boolean),
                takeUntil(this._onDestroy)
            ).subscribe(matching => {
                this.matching = matching;
                if (matching.supplier_event.taxonomies) {
                    const suplTaxonomies = matching.supplier_event.taxonomies.map(taxonomy => taxonomy.name);
                    this.suplTaxonomiesList = suplTaxonomies.join(', ');
                }
                if (matching.shi_event?.taxonomies) {
                    const taxonomies = matching.shi_event.taxonomies.map(taxonomy => taxonomy.name);
                    this.shiTaxonomiesList = taxonomies.join(', ');
                }
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    openCreateMappingDialog(id: string): void {
        this._msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.CREATE_MAPPING',
            message: 'MATCHINGS.CREATE_MAPPING_WARNING',
            actionLabel: 'FORMS.ACTIONS.MAP',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this._matchingsService.list.createMatchingFromDetail(this.overlayData.data.supplier, id))
            )
            .subscribe(() => {
                this._detailOverlayService.close(true);
            });
    }
}
