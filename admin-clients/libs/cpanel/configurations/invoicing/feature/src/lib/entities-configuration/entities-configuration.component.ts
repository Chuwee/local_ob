import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    InvoicingService,
    ConfigTableElement, GetInvoicingEntityConfigRequest,
    InvoicingEntityConfiguration
    , InvoicingEntityOperatorTypes
} from '@admin-clients/cpanel-configurations-invoicing-data-access';
import {
    EphemeralMessageService, FilterItem, ListFilteredComponent,
    ListFiltersService, ObMatDialogConfig, SearchInputComponent, SortFilterComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, NgIf } from '@angular/common';
import {
    AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef,
    inject, OnDestroy, OnInit, Signal, viewChild
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import {
    AbstractControl, ReactiveFormsModule, UntypedFormArray, UntypedFormBuilder, UntypedFormControl,
    UntypedFormGroup, ValidationErrors, ValidatorFn, Validators
} from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, forkJoin, Observable, tap, throwError } from 'rxjs';
import { NewInvoicingConfigDialogComponent } from '../new-configuration-dialog/new-configuration-dialog.component';

@Component({
    selector: 'app-entities-configuration',
    templateUrl: './entities-configuration.component.html',
    styleUrls: ['./entities-configuration.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [ListFiltersService],
    imports: [
        FormContainerComponent, SearchInputComponent, FlexLayoutModule, MaterialModule,
        NgIf, AsyncPipe, TranslatePipe, ReactiveFormsModule
    ]
})
export class EntitiesInvoicingConfigurationComponent extends ListFilteredComponent implements OnInit, OnDestroy, AfterViewInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #matDialog = inject(MatDialog);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #invoicingSrv = inject(InvoicingService);
    private readonly _matSort: Signal<MatSort> = viewChild(MatSort);
    private readonly _searchInputComponent: Signal<SearchInputComponent> = viewChild(SearchInputComponent);

    readonly inputColumns = ['fixed', 'variable', 'min', 'max', 'invitation', 'refund'];
    readonly columns = ['name', ...this.inputColumns];

    form: UntypedFormGroup;
    reqInProgress$: Observable<boolean>;
    configsArray: UntypedFormArray;

    #sortFilterComponent: SortFilterComponent;
    #request: GetInvoicingEntityConfigRequest;

    configs$: Observable<InvoicingEntityConfiguration[]>;
    configs: InvoicingEntityConfiguration[];

    trackByFn = (_, item: InvoicingEntityConfiguration): number => item.entity.id;

    ngOnInit(): void {
        this.initForm();
        this.model();
    }

    override ngOnDestroy(): void {
        this.#invoicingSrv.clearEntitiesConfigs();
        this.form.removeControl('configs');
    }

    ngAfterViewInit(): void {
        this.#sortFilterComponent = new SortFilterComponent(this._matSort());
        this.initListFilteredComponent([
            this.#sortFilterComponent,
            this._searchInputComponent()
        ]);
    }

    loadData(filters: FilterItem[]): void {
        this.#request = {
            type: InvoicingEntityOperatorTypes.undefined
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
                }
            }
        });
        this.#invoicingSrv.loadEntitiesConfigs(this.#request);
    }

    save(): void {
        this.save$()
            .pipe(
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(() => this.#invoicingSrv.loadEntitiesConfigs(this.#request));
    }

    save$(): Observable<void | void[]> {
        if (this.form.valid) {
            const obs$: Observable<void>[] = [];
            const configsForm = this.form.get('configs') as UntypedFormGroup;
            Object.values(configsForm.controls).forEach((control, i) => {
                if (control.dirty) {
                    obs$.push(this.#invoicingSrv.updateEntityConfig(
                        this.configs[i].entity.id,
                        { ...control.value, type: this.configs[i].type }
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
        this.#matDialog.open<NewInvoicingConfigDialogComponent, { isEntityConfig: boolean }, boolean>(
            NewInvoicingConfigDialogComponent, new ObMatDialogConfig({ isEntityConfig: true })
        )
            .beforeClosed()
            .subscribe(created => {
                if (created) {
                    this.#ephemeralSrv.showCreateSuccess();
                    this.#invoicingSrv.loadEntitiesConfigs(this.#request);
                }
            });
    }

    private initForm(): void {
        this.form = this.#fb.group({});
        this.configsArray = this.#fb.array([]);
        this.form.setControl('configs', this.configsArray);
    }

    private model(): void {
        this.configs$ = this.#invoicingSrv.getEntitiesConfigs$()
            .pipe(
                filter(list => !!list),
                takeUntilDestroyed(this.#destroyRef)
            );
        this.configs$
            .pipe(
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(configs => this.processConfigs(configs));

        this.reqInProgress$ = booleanOrMerge([
            this.#invoicingSrv.isEntitiesConfigsLoading$(),
            this.#invoicingSrv.isEntityConfigSaving$()
        ]);
    }

    private processConfigs(configs: ConfigTableElement[]): void {
        this.configsArray.clear(); // deletes all elements of inputs array
        this.configsArray.markAsPristine();
        this.configs = configs;
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
