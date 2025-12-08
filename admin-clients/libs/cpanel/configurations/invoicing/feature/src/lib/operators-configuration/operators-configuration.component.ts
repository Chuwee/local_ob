import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    InvoicingService,
    ConfigTableElement, GetInvoicingEntityConfigRequest,
    InvoicingEntityConfiguration
    , InvoicingEntityOperatorTypes
} from '@admin-clients/cpanel-configurations-invoicing-data-access';
import {
    ChipsComponent, ChipsFilterDirective, EphemeralMessageService, FilterItem, ListFilteredComponent, ListFiltersService,
    ObMatDialogConfig, PopoverComponent, PopoverFilterDirective, SearchInputComponent, SortFilterComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import {
    AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component,
    DestroyRef, inject, OnDestroy, Signal, viewChild, ViewChild
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexModule } from '@angular/flex-layout';
import {
    AbstractControl,
    FormArray,
    FormBuilder,
    ReactiveFormsModule,
    UntypedFormControl,
    UntypedFormGroup, ValidationErrors, ValidatorFn, Validators
} from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, forkJoin, Observable, tap, throwError } from 'rxjs';
import { NewInvoicingConfigDialogComponent } from '../new-configuration-dialog/new-configuration-dialog.component';
import { OperatorsInvoicingConfigurationFilterComponent } from './filter/operators-configuration-filter.component';

@Component({
    selector: 'app-operators-configuration',
    templateUrl: './operators-configuration.component.html',
    styleUrls: ['./operators-configuration.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [ListFiltersService],
    imports: [
        FormContainerComponent, ReactiveFormsModule, CommonModule, MaterialModule, PopoverComponent,
        TranslatePipe, PopoverFilterDirective, FlexModule, ChipsComponent, SearchInputComponent, ChipsFilterDirective,
        OperatorsInvoicingConfigurationFilterComponent
    ]
})
export class OperatorsInvoicingConfigurationComponent extends ListFilteredComponent implements AfterViewInit, OnDestroy {
    readonly #destroyRef: DestroyRef = inject(DestroyRef);
    readonly #matDialog: MatDialog = inject(MatDialog);
    readonly #fb = inject(FormBuilder);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #invoicingSrv = inject(InvoicingService);

    #sortFilterComponent: SortFilterComponent;
    #request: GetInvoicingEntityConfigRequest;

    private readonly _matSort: Signal<MatSort> = viewChild(MatSort);
    @ViewChild(SearchInputComponent) private readonly _searchInputComponent: SearchInputComponent;
    @ViewChild(OperatorsInvoicingConfigurationFilterComponent)
    private readonly _filterComponent: OperatorsInvoicingConfigurationFilterComponent;

    readonly form = this.#fb.group({
        configs: this.#fb.array([])
    });

    readonly configs$: Observable<InvoicingEntityConfiguration[]> = this.#invoicingSrv.getEntitiesConfigs$()
        .pipe(
            takeUntilDestroyed(this.#destroyRef),
            filter(list => !!list),
            tap(this.processConfigs.bind(this))
        );

    readonly $configs: Signal<ConfigTableElement[]> = toSignal(this.configs$);

    readonly reqInProgress$ = booleanOrMerge([
        this.#invoicingSrv.isEntitiesConfigsLoading$(),
        this.#invoicingSrv.isEntityConfigSaving$()
    ]);

    readonly staticColumns = ['fixed', 'variable', 'min', 'max', 'invitation', 'refund'];
    readonly columns = ['name', 'type', ...this.staticColumns];

    ngAfterViewInit(): void {
        this.#sortFilterComponent = new SortFilterComponent(this._matSort());
        this.initListFilteredComponent([
            this.#sortFilterComponent,
            this._searchInputComponent,
            this._filterComponent
        ]);
    }

    override ngOnDestroy(): void {
        this.#invoicingSrv.clearEntitiesConfigs();
        this.form.removeControl('configs');
    }

    loadData(filters: FilterItem[]): void {
        this.#request = {
            type: `${InvoicingEntityOperatorTypes.channel},${InvoicingEntityOperatorTypes.event}`
        };

        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values && values.length > 0) {
                switch (filterItem.key) {
                    case 'SORT':
                        this.#request.sort = values[0].value;
                        break;
                    case 'SEARCH_INPUT':
                        this.#request.q = values[0].value;
                        break;
                    case 'TYPE':
                        this.#request.type = values.map(val => val.value).join(',');
                        break;
                }
            }
        });
        this.#invoicingSrv.loadEntitiesConfigs(this.#request);
    }

    save(): void {
        this.save$().subscribe(() => this.#invoicingSrv.loadEntitiesConfigs(this.#request));
    }

    save$(): Observable<void | void[]> {
        if (this.form.valid) {
            const obs$: Observable<void>[] = [];
            const configsForm = this.form.get('configs') as UntypedFormGroup;
            Object.values(configsForm.controls).forEach((control, i) => {
                if (control.dirty) {
                    obs$.push(this.#invoicingSrv.updateEntityConfig(
                        this.$configs().at(i).entity.id,
                        { ...control.value, type: this.$configs().at(i).type }
                    ));
                }
            });
            return forkJoin(obs$).pipe(tap(() => this.#ephemeralSrv.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this.#invoicingSrv.loadEntitiesConfigs(this.#request);
    }

    openNewConfigDialog(): void {
        this.#matDialog.open<NewInvoicingConfigDialogComponent, null, boolean>(
            NewInvoicingConfigDialogComponent, new ObMatDialogConfig()
        )
            .beforeClosed()
            .subscribe(created => {
                if (created) {
                    this.#ephemeralSrv.showCreateSuccess();
                    this.#invoicingSrv.loadEntitiesConfigs(this.#request);
                }
            });
    }

    get configsArray(): FormArray {
        return this.form.controls.configs;
    }

    private processConfigs(configs: ConfigTableElement[]): void {

        this.configsArray.clear(); // deletes all elements of inputs array
        this.configsArray.markAsPristine();
        for (const config of configs) {
            // create group of controls per config
            const configControl = this.createEntityControl(config);
            // push this control to the form array
            this.configsArray.push(configControl);
            config.ctrl = configControl;
        }
        this.#ref.markForCheck();
    }

    private createEntityControl(config: InvoicingEntityConfiguration): UntypedFormGroup {
        return this.#fb.group({
            fixed: [config.fixed, [Validators.min(0)]],
            variable: [config.variable, [Validators.min(0)]],
            min: [config.min, [Validators.min(0)]],
            max: [config.max, [Validators.min(0)]],
            invitation: [config.invitation, [Validators.min(0)]],
            refund: [config.refund, [Validators.min(0)]]
        }, { validators: this.minMaxValidator() });
    }

    private minMaxValidator(): ValidatorFn {
        return (group: AbstractControl): ValidationErrors | null => {
            const minCtrl = group.get('min') as UntypedFormControl;
            const maxCtrl = group.get('max') as UntypedFormControl;
            const minValue = minCtrl.value ?? 0;
            const maxValue = maxCtrl.value ?? 0;
            const validation = (minValue || maxValue) && (minValue >= maxValue) ? { minGreaterThanMax: true } : null;
            this.setControlError(minCtrl, validation);
            this.setControlError(maxCtrl, validation);
            if (minValue >= maxValue) {
                this.configsArray.setErrors(validation);
                this.#ref.detectChanges();
            }
            return validation;
        };
    }

    private setControlError(control: UntypedFormControl, validation: ValidationErrors | null): void {
        control.markAsTouched();
        control.setErrors(validation);
    }

}
