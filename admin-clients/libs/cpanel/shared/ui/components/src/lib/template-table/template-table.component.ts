import { venuesProviders } from '@admin-clients/cpanel/venues/data-access';
import { EntityExternalCapacity, ExternalInvetories } from '@admin-clients/shared/common/data-access';
import { ContextNotificationComponent, SearchablePaginatedSelectionModule } from '@admin-clients/shared/common/ui/components';
import {
    VenueTemplatesService, VenueTemplatesState
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { Attribute, ChangeDetectionStrategy, Component, input, Optional } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-template-table',
    templateUrl: './template-table.component.html',
    styleUrls: ['./template-table.component.scss'],
    providers: [
        venuesProviders,
        VenueTemplatesService,
        VenueTemplatesState
    ],
    imports: [
        TranslatePipe,
        MatIcon,
        SearchablePaginatedSelectionModule,
        ContextNotificationComponent
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TemplateTableComponent {
    readonly $data = input.required<EntityExternalCapacity[] | ExternalInvetories[]>({ alias: 'data' });
    readonly $pageForm = input.required<UntypedFormGroup>({ alias: 'pageForm' });
    readonly $columnTitle = input.required<string>({ alias: 'columnTitle' });
    readonly $rows = input.required<string[]>({ alias: 'rows' });
    readonly $textInfo = input<string>(null, { alias: 'textInfo' });

    get sga(): boolean {
        return this._classNames?.includes('sga') || false;
    }

    constructor(
        @Optional() @Attribute('class') private _classNames: string
    ) { }

}
