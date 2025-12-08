import { NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatTabsModule } from '@angular/material/tabs';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { SalesSettingsApi } from '../../api/sales-settings.api';
import { SalesSettingsService } from '../../sales-settings.service';
import { SalesSettingsState } from '../../state/sales-settings.state';

@Component({
    imports: [RouterModule, TranslatePipe, MatTabsModule, NavTabsMenuComponent, AsyncPipe, FlexLayoutModule],
    selector: 'app-sales-settings-page',
    templateUrl: './sales-settings-page.component.html',
    styleUrls: ['./sales-settings-page.component.scss'],
    providers: [SalesSettingsService, SalesSettingsApi, SalesSettingsState],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SalesSettingsPageComponent {
    readonly #salesSettingsService = inject(SalesSettingsService);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);

    readonly isLoading$ = this.#salesSettingsService.salesConfiguration.isInProgress$();
    readonly deepPath$ = getDeepPath$(this.#router, this.#route);
}
