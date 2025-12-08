import { NotesModule } from '@admin-clients/cpanel/common/feature/notes';
import { AsyncPipe, CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatSidenavModule } from '@angular/material/sidenav';
import { OrderNotesContainerComponent } from './container/order-notes-container.component';
import { OrderNoteDetailsComponent } from './details/order-note-details.component';
import { OrderNotesListComponent } from './list/order-notes-list.component';
import { OrderNotesRoutingModule } from './order-notes-routing.module';

@NgModule({
    imports: [
        OrderNotesRoutingModule,
        NotesModule,
        MatSidenavModule,
        FlexLayoutModule,
        AsyncPipe,
        CommonModule
    ],
    declarations: [
        OrderNotesListComponent,
        OrderNoteDetailsComponent,
        OrderNotesContainerComponent
    ]
})
export class OrderNotesModule { }
