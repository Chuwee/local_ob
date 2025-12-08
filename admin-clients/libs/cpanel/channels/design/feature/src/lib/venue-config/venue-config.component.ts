import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelNavigationModes, ChannelsService, PutChannelWhitelabelSettings } from '@admin-clients/cpanel/channels/data-access';
import { EphemeralMessageService, RadioImageButtonComponent } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, QueryList, ViewChildren, inject } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDivider } from '@angular/material/divider';
import { MatExpansionModule, MatExpansionPanel } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, filter, throwError, tap, take } from 'rxjs';

@Component({
    selector: 'ob-channel-design-venue-config',
    templateUrl: './venue-config.component.html',
    styleUrls: ['./venue-config.component.scss'],
    imports: [
        FormContainerComponent, RadioImageButtonComponent, ReactiveFormsModule, TranslatePipe, MatIcon,
        MatExpansionModule, MatProgressSpinner, MatCheckboxModule, MatRadioModule, AsyncPipe,
        MatDivider
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueMapConfigComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);

    #channelId: number;

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    readonly loading$ = this.#channelsSrv.channelWhitelabelSettings.loading$();
    readonly navigationModes = ChannelNavigationModes;
    readonly form = this.#fb.group({
        navigation: null as ChannelNavigationModes,
        priceRange: false,
        availableTickets: false,
        automaticSelection: false,
        showImagesCarousel: false,
        showCompactedViewList: false,
        forceSidePanel: false
    });

    readonly $navigationValue = toSignal(this.form.get('navigation').valueChanges);

    ngOnInit(): void {
        this.#channelsSrv.getChannel$()
            .pipe(filter(Boolean), take(1))
            .subscribe(channel => {
                this.#channelId = channel.id;
                this.#channelsSrv.channelWhitelabelSettings.load(channel.id);
            });

        this.#channelsSrv.channelWhitelabelSettings.get$()
            .pipe(takeUntilDestroyed(this.#destroyRef), filter(Boolean))
            .subscribe(({ venue_map: {
                navigation_mode: navigation,
                allow_price_range_filter: priceRange,
                show_available_tickets: availableTickets,
                show_images_carousel: showImagesCarousel,
                enabled_automatic_selection: automaticSelection,
                show_compacted_view_list: showCompactedViewList,
                force_side_panel_view_list: forceSidePanel
            } }) => {
                this.form.patchValue({
                    navigation, priceRange, availableTickets, showImagesCarousel,
                    automaticSelection, showCompactedViewList: showCompactedViewList ?? false,
                    forceSidePanel: forceSidePanel ?? false
                });
                this.form.markAsPristine();
            });
    }

    save(): void {
        this.save$().subscribe({
            next: () => this.#channelsSrv.channelWhitelabelSettings.load(this.#channelId)
        });
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const isPriceZonesNavigation = this.form.value.navigation === ChannelNavigationModes.priceZones;
            const newSettings: PutChannelWhitelabelSettings = {
                venue_map: {
                    allow_price_range_filter: this.form.value.priceRange,
                    show_available_tickets: this.form.value.availableTickets,
                    show_images_carousel: isPriceZonesNavigation ? false : this.form.value.showImagesCarousel,
                    navigation_mode: this.form.value.navigation,
                    enabled_automatic_selection: this.form.value.automaticSelection,
                    show_compacted_view_list: isPriceZonesNavigation ? false : this.form.value.showCompactedViewList,
                    force_side_panel_view_list: isPriceZonesNavigation ? false : this.form.value.forceSidePanel
                }
            };
            return this.#channelsSrv.channelWhitelabelSettings.update(this.#channelId, newSettings)
                .pipe(tap(() => this.#ephemeralSrv.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this.#channelsSrv.channelWhitelabelSettings.load(this.#channelId);
    }
}
