import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { ContextNotificationComponent } from '../context-notification/context-notification.component';
import { SearchInputComponent } from '../search-input/search-input.component';
import { SearchablePaginatedSelectionComponent } from './searchable-paginated-selection.component';
import { SelectionListComponent } from './selection-list/selection-list.component';
import { SelectionTableComponent } from './selection-table/selection-table.component';

@NgModule({
    declarations: [SelectionTableComponent, SearchablePaginatedSelectionComponent],
    exports: [
        SelectionListComponent,
        SelectionTableComponent,
        SearchablePaginatedSelectionComponent
    ],
    imports: [
        MatCheckboxModule,
        MatPaginatorModule,
        MatProgressSpinnerModule,
        MatTableModule,
        ReactiveFormsModule,
        FlexLayoutModule,
        CommonModule,
        TranslatePipe,
        RouterModule,
        SearchInputComponent,
        ContextNotificationComponent,
        SelectionListComponent
    ]
})
export class SearchablePaginatedSelectionModule { }
