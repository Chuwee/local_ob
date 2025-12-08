import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { OperatorShards, OperatorsService, PostOperator } from '@admin-clients/cpanel-configurations-operators-data-access';
import {
    CurrenciesService, LanguagesService, TimezonesService, GatewaysService
} from '@admin-clients/shared/common/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import {
    AbstractControl, ReactiveFormsModule, UntypedFormBuilder, ValidationErrors, ValidatorFn, Validators
} from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject, first } from 'rxjs';

@Component({
    selector: 'app-new-operator-dialog',
    templateUrl: './new-operator-dialog.component.html',
    styleUrls: ['./new-operator-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        FlexLayoutModule,
        MaterialModule,
        TranslatePipe,
        ReactiveFormsModule,
        FormControlErrorsComponent
    ]
})
export class NewOperatorDialogComponent implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    private readonly _dialogRef = inject(MatDialogRef<NewOperatorDialogComponent, { id: number; password: string }>);
    private readonly _operatorsSrv = inject(OperatorsService);
    private readonly _timezonesSrv = inject(TimezonesService);
    private readonly _languagesSrv = inject(LanguagesService);
    private readonly _gatewaysSrv = inject(GatewaysService);
    private readonly _currenciesSrv = inject(CurrenciesService);

    readonly form = inject(UntypedFormBuilder).group({
        name: [null, [Validators.required]],
        shortName: [null, [Validators.required, Validators.maxLength(30), this.noBlankSpacesValidator()]],
        currency: [null, [Validators.required]],
        timezone: [null, [Validators.required]],
        shard: [null, [Validators.required]],
        language: [null, [Validators.required]],
        gateways: [[], [Validators.required]]
    });

    readonly timezones$ = this._timezonesSrv.timezones.get$().pipe(first(Boolean));
    readonly languages$ = this._languagesSrv.getLanguages$().pipe(first(Boolean));
    readonly gateways$ = this._gatewaysSrv.gatewaysList.get$().pipe(first(Boolean));
    readonly currencies$ = this._currenciesSrv.currencies.get$().pipe(first(Boolean));
    readonly shardsList = Object.values(OperatorShards);
    readonly reqInProgress$ = booleanOrMerge([
        this._operatorsSrv.operator.loading$(),
        this._languagesSrv.isLanguagesInProgress$(),
        this._timezonesSrv.timezones.loading$(),
        this._currenciesSrv.currencies.loading$(),
        this._gatewaysSrv.gatewaysList.loading$()
    ]);

    constructor(
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this._timezonesSrv.timezones.load();
        this._languagesSrv.loadLanguages(true);
        this._gatewaysSrv.gatewaysList.load();
        this._currenciesSrv.currencies.load();
    }

    ngOnDestroy(): void {
        this._languagesSrv.clearLanguages();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    createOperator(): void {
        if (this.form.valid) {
            const { name, shortName, currency, timezone, shard, language, gateways } = this.form.value;
            const operator: PostOperator = {
                name,
                short_name: shortName,
                currency_code: currency,
                olson_id: timezone,
                shard,
                language_code: language,
                gateways
            };
            this._operatorsSrv.operator.create(operator)
                .subscribe(response => this.close(response));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
        }
    }

    close(operator: { id: number; password: string } = null): void {
        this._dialogRef.close(operator);
    }

    private noBlankSpacesValidator(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => (
            control?.value?.includes(' ') ? { noBlankSpaces: true } : null
        );
    }
}
