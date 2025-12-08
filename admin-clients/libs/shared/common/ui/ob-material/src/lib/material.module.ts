import { NgModule, Provider } from '@angular/core';
import { MatAutocompleteModule } from '@angular/material/autocomplete'; // no se puede migrar hasta migrar mat select
import { MatBadgeModule } from '@angular/material/badge';
import { MatButtonModule } from '@angular/material/button';
import { MAT_BUTTON_TOGGLE_DEFAULT_OPTIONS, MatButtonToggleDefaultOptions, MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatCardModule } from '@angular/material/card';
import { MAT_CHECKBOX_DEFAULT_OPTIONS, MatCheckboxDefaultOptions, MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MatRippleModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import {
    MAT_FORM_FIELD_DEFAULT_OPTIONS,
    MatFormFieldDefaultOptions
} from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MAT_LIST_CONFIG, MatListConfig, MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorIntl, MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MAT_RADIO_DEFAULT_OPTIONS, MatRadioDefaultOptions, MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSliderModule } from '@angular/material/slider';
import { MAT_SNACK_BAR_DEFAULT_OPTIONS, MatSnackBarConfig, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MAT_TOOLTIP_DEFAULT_OPTIONS, MatTooltipModule } from '@angular/material/tooltip';
import { MatTreeModule } from '@angular/material/tree';
import { TranslateService } from '@ngx-translate/core';
import { MatPaginatorCustomIntl } from './components/paginator/mat-paginator-custom-intl';
import { ObFormFieldLabelDirective } from './directives/form-field-label.directive';

export const provideMaterialSettings = (): Provider[] => [
    {
        provide: MAT_BUTTON_TOGGLE_DEFAULT_OPTIONS,
        useValue: {
            hideSingleSelectionIndicator: true,
            hideMultipleSelectionIndicator: true
        } as MatButtonToggleDefaultOptions
    },
    {
        provide: MAT_TOOLTIP_DEFAULT_OPTIONS,
        useValue: {
            showDelay: 500,
            hideDelay: 0,
            touchendHideDelay: 1500,
            disableTooltipInteractivity: true
        }
    },
    {
        provide: MAT_LIST_CONFIG,
        useValue: { hideSingleSelectionIndicator: true } as MatListConfig
    },
    {
        provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
        useValue: { floatLabel: 'always', subscriptSizing: 'fixed' } as MatFormFieldDefaultOptions
    },
    {
        provide: MAT_CHECKBOX_DEFAULT_OPTIONS,
        useValue: { color: 'primary' } as MatCheckboxDefaultOptions
    },
    {
        provide: MAT_RADIO_DEFAULT_OPTIONS,
        useValue: { color: 'primary' } as MatRadioDefaultOptions
    },
    {
        provide: MAT_SNACK_BAR_DEFAULT_OPTIONS,
        useValue: {
            duration: 5000,
            horizontalPosition: 'right',
            verticalPosition: 'top'
        } as MatSnackBarConfig
    },
    {
        provide: MatPaginatorIntl,
        useFactory: (translate: TranslateService) => new MatPaginatorCustomIntl(translate),
        deps: [TranslateService]
    }
];

const MODULES = [
    MatAutocompleteModule,
    MatBadgeModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatCardModule,
    MatCheckboxModule,
    MatChipsModule,
    MatDatepickerModule,
    MatDialogModule,
    MatExpansionModule,
    MatGridListModule,
    MatIconModule,
    MatInputModule,
    MatMenuModule,
    MatPaginatorModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatRadioModule,
    MatRippleModule,
    MatSelectModule,
    MatSidenavModule,
    MatSliderModule,
    MatSlideToggleModule,
    MatSnackBarModule,
    MatSortModule,
    MatTabsModule,
    MatToolbarModule,
    MatTooltipModule,
    MatTreeModule,
    MatTableModule,
    MatListModule,
    ObFormFieldLabelDirective
];

/**
 * PLEASE DO NOT USE THIS MODULE ANYMORE.
 * @deprecated Use material components from `@angular/material` instead.
 * This module is no longer needed and will be removed in the future.
 */
@NgModule({
    imports: MODULES,
    exports: MODULES,
    providers: [
        provideMaterialSettings()
    ]
})
export class MaterialModule { }
