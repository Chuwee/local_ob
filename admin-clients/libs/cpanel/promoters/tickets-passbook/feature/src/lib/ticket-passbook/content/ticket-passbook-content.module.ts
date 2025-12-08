import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { SearchInputComponent, TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DefaultIconComponent } from '@admin-clients/shared-common-ui-default-icon';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { TicketPassbookDownloadComponent } from '../ticket-passbook-download/ticket-passbook-download.component';
import { TicketPassbookBackPageComponent } from './back-page/ticket-passbook-back-page.component';
import { TicketPassbookCoverPageComponent } from './cover-page/ticket-passbook-cover-page.component';
import { TicketAddCustomContentsTabComponent } from './editor/add-content-dialog/custom/custom-contents-tab.component';
import { CustomContentFieldComponent } from './editor/add-content-dialog/custom/custom-field/custom-content-field.component';
import { TicketStandardContentsTabComponent } from './editor/add-content-dialog/standard/standard-contents-tab.component';
import { TicketAddContentDialogComponent } from './editor/add-content-dialog/ticket-add-content-dialog.component';
import { TicketContentListComponent } from './editor/content-list/ticket-content-list.component';
import { TicketPassbookContentsEditorComponent } from './editor/ticket-passbook-contents-editor.component';
import { CustomContentPlaceholderPipe } from './pipes/custom-content-placeholder.pipe';
import { TicketPassbookContentRoutingModule } from './ticket-passbook-content-routing.module';
import { TicketPassbookContentComponent } from './ticket-passbook-content.component';

@NgModule({
    declarations: [
        TicketPassbookContentComponent,
        TicketPassbookCoverPageComponent,
        TicketPassbookBackPageComponent,
        TicketContentListComponent,
        TicketAddContentDialogComponent,
        TicketAddCustomContentsTabComponent,
        TicketStandardContentsTabComponent,
        CustomContentFieldComponent,
        CustomContentPlaceholderPipe,
        TicketPassbookContentsEditorComponent
    ],
    imports: [
        DefaultIconComponent,
        DragDropModule,
        TicketPassbookContentRoutingModule,
        TicketPassbookDownloadComponent,
        TabsMenuComponent,
        TabDirective,
        MaterialModule,
        CommonModule,
        TranslatePipe,
        LastPathGuardListenerDirective,
        FlexLayoutModule,
        FormContainerComponent,
        SearchInputComponent,
        ReactiveFormsModule,
        EllipsifyDirective
    ]
})
export class TicketPassbookContentModule {
}
