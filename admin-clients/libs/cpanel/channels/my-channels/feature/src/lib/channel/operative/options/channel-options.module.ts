import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ChannelsPipesModule, IsV4$Pipe } from '@admin-clients/cpanel/channels/data-access';
import { RulesEditorDialogComponent } from '@admin-clients/cpanel/common/feature/forms';
import { CopyTextComponent, HelpButtonComponent, TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { PrefixPipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { AdditionalConditionsDialogComponent } from './additional-conditions-dialog/additional-conditions-dialog.component';
import { AdditionalConditionsTableComponent } from './additional-conditions-table/additional-conditions-table.component';
import { ChannelFormsComponent } from './channel-forms/channel-forms.component';
import { ChannelOptionsComponent } from './channel-options.component';
import { routes } from './channel-options.routes';

@NgModule({
    declarations: [
        ChannelOptionsComponent,
        ChannelFormsComponent
    ],
    providers: [
        PrefixPipe.provider('CHANNELS.OPTIONS.')
    ],
    imports: [
        MaterialModule,
        CommonModule,
        TranslatePipe,
        FormControlErrorsComponent,
        FlexLayoutModule,
        ReactiveFormsModule,
        FormContainerComponent,
        PrefixPipe,
        ChannelsPipesModule,
        AdditionalConditionsDialogComponent,
        AdditionalConditionsTableComponent,
        RouterModule.forChild(routes),
        TabsMenuComponent,
        TabDirective,
        RulesEditorDialogComponent,
        IsV4$Pipe,
        CopyTextComponent,
        HelpButtonComponent
    ]
})
export class ChannelOperativeOptionsModule { }

export default ChannelOperativeOptionsModule;
