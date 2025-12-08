import { ChannelsPipesModule, IsV3$Pipe } from '@admin-clients/cpanel/channels/data-access';
import { ChannelsVouchersApi, ChannelsVouchersService, ChannelsVouchersState } from '@admin-clients/cpanel/channels/vouchers/data-access';
import { entitiesProviders } from '@admin-clients/cpanel/organizations/entities/data-access';
import {
    ChipsComponent, ChipsFilterDirective, ContextNotificationComponent, PaginatorComponent,
    PopoverComponent, PopoverFilterDirective, SearchInputComponent, SelectSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { ChannelsRoutingModule } from './channels-routing.module';
import { NewChannelDialogComponent } from './create/new-channel-dialog.component';
import { ChannelsListComponent } from './list/channels-list.component';
import { ChannelsListFilterComponent } from './list/filter/channels-list-filter.component';

@NgModule({
    providers: [
        ChannelsVouchersService,
        ChannelsVouchersApi,
        ChannelsVouchersState,
        ...entitiesProviders
    ],
    declarations: [
        ChannelsListComponent,
        NewChannelDialogComponent
    ],
    imports: [
        TranslatePipe,
        ReactiveFormsModule,
        CommonModule,
        MaterialModule,
        ContextNotificationComponent,
        ChannelsPipesModule,
        ChannelsRoutingModule,
        PopoverComponent,
        PopoverFilterDirective,
        ChipsFilterDirective,
        SelectSearchComponent,
        FlexLayoutModule,
        SearchInputComponent,
        ChipsComponent,
        PaginatorComponent,
        EllipsifyDirective,
        ChannelsListFilterComponent,
        IsV3$Pipe
    ]
})
export class ChannelsModule { }
