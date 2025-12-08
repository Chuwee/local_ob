import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { SaleRequestGatewayBenefitBinGroup } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import {
    CSV_FILE_PROCESSOR, CsvErrorEnum, CsvFile, CsvFileTemplate, CsvModule, csvValidator
} from '@admin-clients/shared/common/feature/csv';
import {
    Chip, ChipsComponent, DialogSize, MessageDialogService, openDialog
} from '@admin-clients/shared/common/ui/components';
import { ErrorMessage$Pipe } from '@admin-clients/shared/utility/pipes';
import {
    atLeastOneRequiredInArray, dynamicUnique, requiredFieldsInOneControl, requiredLength
} from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, effect, inject, input, model, OnInit, ViewContainerRef
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
    FormBuilder, FormGroupDirective, ReactiveFormsModule, Validators
} from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatError, MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Papa } from 'ngx-papaparse';
import { BehaviorSubject } from 'rxjs';
import { VmSaleRequestGatewayBenefit } from '../vm-sale-request-gateway-benefit.model';
import {
    SaleRequestPaymentMethodsBenefitsBinsMoreComponent
} from './more/sale-request-payment-methods-benefits-bins-more.component';

type BinsToAddMethod = 'IMPORT' | 'MANUAL';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatRadioGroup, MatRadioButton, TranslatePipe, ReactiveFormsModule, MatError, MatFormField, MatInput, MatButton, ChipsComponent,
        FormControlErrorsComponent, AsyncPipe, ErrorMessage$Pipe, CsvModule
    ],
    selector: 'app-sale-request-payment-methods-benefits-bins',
    templateUrl: './sale-request-payment-methods-benefits-bins.component.html',
    styleUrl: './sale-request-payment-methods-benefits-bins.component.scss',
    providers: [{ provide: CSV_FILE_PROCESSOR, useExisting: SaleRequestPaymentMethodsBenefitsBinsComponent }]
})
export class SaleRequestPaymentMethodsBenefitsBinsComponent implements OnInit {
    readonly #dialogSrv = inject(MatDialog);
    readonly #viewContainerRef = inject(ViewContainerRef);
    readonly #formGroup = inject(FormGroupDirective);
    readonly #fb = inject(FormBuilder);
    readonly #translateSrv = inject(TranslateService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #csvParser = inject(Papa);
    readonly #regexValidator = /^[0-9]*$/;
    readonly #requiredBinLength = 6;

    readonly $postBins = model.required<SaleRequestGatewayBenefitBinGroup['bins']>({ alias: 'postBins' });

    readonly $vmSaleRequestGatewayBenefit = input.required<VmSaleRequestGatewayBenefit>({ alias: 'vmSaleRequestGatewayBenefit' });

    readonly exampleCsv: CsvFileTemplate = {
        name: 'bins-template.csv',
        data: 411111 + '\r\n' + 550000 + '\r\n' + 660000
    };

    readonly form = this.#fb.nonNullable.group({
        type: this.#fb.nonNullable.control(null as BinsToAddMethod, Validators.required),
        bins: this.#fb.nonNullable.control([] as string[], atLeastOneRequiredInArray())
    });

    readonly manualCtrl = this.#fb.control(
        null as string,
        [Validators.required, Validators.pattern(this.#regexValidator), requiredLength(this.#requiredBinLength),
        dynamicUnique(() => this.form.controls.bins.value, (bin, currentValue) => bin === (currentValue as string)?.trim())]
    );

    readonly csvSelectionForm = this.#fb.group({
        selection: [{ file: null, processedFile: null } as CsvFile, [
            requiredFieldsInOneControl(Object.keys({ file: null, processedFile: null })),
            csvValidator(CsvErrorEnum.csvProcessorFileError)
        ]]
    });

    readonly chips$ = new BehaviorSubject<Chip[]>([]);

    readonly maxBins = 10;

    constructor() {
        effect(() => {
            const vmSaleRequestGatewayBenefit = this.$vmSaleRequestGatewayBenefit();

            if (vmSaleRequestGatewayBenefit.beingModified) {
                this.form.enable({ emitEvent: false });
                if (vmSaleRequestGatewayBenefit.beingModified.create) {
                    this.manualCtrl.disable({ emitEvent: false });
                    this.csvSelectionForm.disable({ emitEvent: false });
                } else if (vmSaleRequestGatewayBenefit.beingModified.edit) {
                    if (this.form.controls.type.value === 'MANUAL') {
                        this.manualCtrl.enable({ emitEvent: false });
                        this.csvSelectionForm.disable({ emitEvent: false });
                    } else {
                        this.manualCtrl.disable({ emitEvent: false });
                        this.csvSelectionForm.enable({ emitEvent: false });
                    }
                }
            } else {
                const [binGroup] = vmSaleRequestGatewayBenefit.bin_groups;

                this.form.controls.type.reset('MANUAL', { emitEvent: false });
                this.form.controls.bins.reset(binGroup.bins, { emitEvent: false });
                this.form.disable({ emitEvent: false });
                this.manualCtrl.reset(null, { emitEvent: false });
                this.manualCtrl.disable({ emitEvent: false });
                this.csvSelectionForm.reset(null, { emitEvent: false });
                this.csvSelectionForm.disable({ emitEvent: false });

                this.$postBins.set(binGroup.bins);
                const chips = binGroup.bins.slice(0, this.maxBins).map(bin => ({ label: bin.toString(), value: bin }));
                this.chips$.next(chips);

            }
        });

        this.form.controls.type.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(type => {
                if (type === 'MANUAL') {
                    this.csvSelectionForm.disable({ emitEvent: false });
                    this.manualCtrl.enable({ emitEvent: false });
                } else if (type === 'IMPORT') {
                    this.csvSelectionForm.enable({ emitEvent: false });
                    this.manualCtrl.disable({ emitEvent: false });
                }
            });

        this.csvSelectionForm.controls.selection.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(csvFile => {
                if (!csvFile?.processedFile && !this.csvSelectionForm.controls.selection.touched) return;

                const bins = csvFile?.processedFile as string[] ?? [];
                // because the value is set programatically, it has to emit events to the parent
                // and be market as touched
                this.form.controls.bins.setValue(bins);
                this.form.controls.bins.markAsTouched();
                const chips = bins.slice(0, this.maxBins).map(bin => ({ label: bin.toString(), value: bin }));
                this.chips$.next(chips);
                this.$postBins.set(bins);
            });
    }

    ngOnInit(): void {
        this.#formGroup.control.addControl('binsCtrl', this.form, { emitEvent: false });
    }

    addBin(): void {
        if (this.manualCtrl.valid) {
            const bin = this.manualCtrl.value;
            const bins = [...this.form.controls.bins.value, bin];
            // because the value is set programatically, it has to emit events to the parent
            // and be market as touched
            this.form.controls.bins.setValue(bins);
            this.form.controls.bins.markAsTouched();
            this.manualCtrl.reset(null, { emitEvent: false });
            this.$postBins.set(bins);
            const chips = bins.slice(0, this.maxBins).map(bin => ({ label: bin.toString(), value: bin }));
            this.chips$.next(chips);
        } else {
            this.manualCtrl.markAsTouched();
            this.manualCtrl.setValue(this.manualCtrl.getRawValue());
        }
    }

    removeBinChip(chip: Chip): void {
        const bins = this.form.controls.bins.value.filter(bin => bin !== chip.value);
        this.form.controls.bins.setValue(bins, { emitEvent: false });
        this.$postBins.set(bins);
        const chips = bins.slice(0, this.maxBins).map(bin => ({ label: bin.toString(), value: bin }));
        this.chips$.next(chips);
    }

    showMoreBins(): void {
        const binsAsIds = this.form.controls.bins.value.map(bin => ({ id: bin }));
        openDialog(this.#dialogSrv, SaleRequestPaymentMethodsBenefitsBinsMoreComponent,
            { bins: binsAsIds, isDisabled: !this.$vmSaleRequestGatewayBenefit().beingModified }, this.#viewContainerRef).beforeClosed()
            .subscribe(bins => {
                if (!bins) return;

                const binsSet = new Set(bins?.map(bin => bin.id));
                const binsResult = this.form.controls.bins.value.filter(bin => !binsSet.has(bin));
                this.form.controls.bins.setValue(binsResult, { emitEvent: false });
                this.$postBins.set(binsResult);
                const chips = binsResult.slice(0, this.maxBins).map(bin => ({ label: bin.toString(), value: bin }));
                this.chips$.next(chips);
            });
    }

    processFile(
        file: File,
        valueCB: (processedFile: unknown) => void,
        errorsCB?: () => void
    ): void {
        let hasErrors = false;
        const binsSet = new Set<string>();
        let hasInvalidBinPatterns = false;
        let hasShortBins = false;
        let hasRepeatedBins = false;
        file.text().then(res => {
            this.#csvParser.parse(res, {
                delimiter: ';', skipEmptyLines: true, worker: true,
                step: (({ data, errors }) => {
                    const bin: string = data[0];
                    const isPatternInvalidValid = !this.#regexValidator.test(bin);
                    const isLengthInvalidValid = bin.length !== this.#requiredBinLength;
                    const isRepeated = binsSet.has(bin);

                    if (!isPatternInvalidValid && !isLengthInvalidValid && !isRepeated) {
                        binsSet.add(data[0]);
                    } else {
                        if (!hasInvalidBinPatterns && isPatternInvalidValid) {
                            hasInvalidBinPatterns = true;
                        }

                        if (!hasShortBins && isLengthInvalidValid) {
                            hasShortBins = true;
                        }

                        if (!hasRepeatedBins && isRepeated) {
                            hasRepeatedBins = true;
                        }
                    }

                    if (!hasErrors && errors?.length) {
                        hasErrors = true;
                    }
                }),
                complete: (() => {
                    if (hasErrors || binsSet.size === 0) {
                        const title = this.#translateSrv.instant('TITLES.ERROR_DIALOG');
                        const message = this.#translateSrv.instant('CSV.IMPORT.PROCESS_ERROR');
                        this.#msgDialogSrv.showAlert({ size: DialogSize.SMALL, title, message });
                        errorsCB();
                    } else {
                        if (hasShortBins || hasInvalidBinPatterns || hasRepeatedBins) {
                            const title = this.#translateSrv.instant('SALE_REQUESTS.PAYMENT_METHODS.BENEFITS.TITLES.SOME_BINS_NOT_ADDED');
                            const message = this.#translateSrv.instant(
                                'SALE_REQUESTS.PAYMENT_METHODS.BENEFITS.FORMS.FEEDBACK.SOME_BINS_NOT_ADDED');
                            this.#msgDialogSrv.showWarn({ size: DialogSize.SMALL, title, message });
                        }
                        valueCB([...binsSet.values()]);
                    }
                })
            });
        });
    }
}
