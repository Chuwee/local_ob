import {
    ChannelsService, ReviewConfigElement, ReviewConfigElementFilter
} from '@admin-clients/cpanel/channels/data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig, SearchablePaginatedListComponent
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, switchMap } from 'rxjs';
import { ReviewConfigElementCreateComponent } from './create/review-config-element-create.component';
import { ReviewConfigElementEditComponent } from './edit/review-config-element-edit.component';

const PAGE_SIZE = 10;

@Component({
    selector: 'ob-review-config-elements',
    templateUrl: './review-config-elements.component.html',
    styleUrl: './review-config-elements.component.scss',
    imports: [
        MatIcon, TranslatePipe, DateTimePipe, SearchablePaginatedListComponent, MatIconButton,
        MatIcon, MatButton, EllipsifyDirective
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReviewConfigElementsComponent {
    readonly #matDialog = inject(MatDialog);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);

    readonly $channelId = input.required<number>({ alias: 'channelId' });
    readonly $enabled = input<boolean>(true, { alias: 'enabled' });

    #filters: ReviewConfigElementFilter = { limit: PAGE_SIZE, offset: null, q: null };
    readonly pageSize = PAGE_SIZE;
    readonly dateTimeFormats = DateTimeFormats;

    readonly configsData$ = this.#channelsSrv.reviewConfigElements.getData$().pipe(map(sessions => sessions || []));
    readonly configsMetadata$ = this.#channelsSrv.reviewConfigElements.getMetadata$();
    readonly loading$ = this.#channelsSrv.reviewConfigElements.loading$();
    readonly $isHandsetOrTablet = toSignal(this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches)));

    loadElements(filters: ReviewConfigElementFilter): void {
        this.#filters = filters;
        this.#channelsSrv.reviewConfigElements.load(this.$channelId(), this.#filters);
    }

    openCreateDialog(): void {
        this.#matDialog.open(
            ReviewConfigElementCreateComponent,
            new ObMatDialogConfig({ channelId: this.$channelId() })
        ).beforeClosed()
            .pipe(
                filter(Boolean),
                switchMap(newConfig => this.#channelsSrv.reviewConfigElements.add(this.$channelId(), newConfig.scope, newConfig.data))
            )
            .subscribe(() => {
                this.#channelsSrv.reviewConfigElements.load(this.$channelId());
                this.#ephemeralSrv.showCreateSuccess();
            });
    }

    openEditDialog(config: ReviewConfigElement): void {
        this.#matDialog.open(
            ReviewConfigElementEditComponent,
            new ObMatDialogConfig({ config })
        ).beforeClosed()
            .pipe(
                filter(Boolean),
                switchMap(updatedConfig => this.#channelsSrv.reviewConfigElements.update(this.$channelId(), updatedConfig))
            )
            .subscribe(() => {
                this.#channelsSrv.reviewConfigElements.load(this.$channelId());
                this.#ephemeralSrv.showSaveSuccess();
            });
    }

    openDeleteDialog(config: ReviewConfigElement): void {
        this.#msgDialogSrv
            .showWarn({
                size: DialogSize.SMALL,
                title: 'CHANNELS.REVIEWS.CONFIG.ACTIONS.DELETE_TITLE',
                message: 'CHANNELS.REVIEWS.CONFIG.ACTIONS.DELETE_MESSAGE',
                actionLabel: 'FORMS.ACTIONS.DELETE',
                showCancelButton: true
            })
            .pipe(
                filter(Boolean),
                switchMap(() => this.#channelsSrv.reviewConfigElements.delete(this.$channelId(), config.scope, config.scope_id))
            )
            .subscribe(() => {
                this.#channelsSrv.reviewConfigElements.load(this.$channelId());
                this.#ephemeralSrv.showDeleteSuccess();
            });
    }
}
