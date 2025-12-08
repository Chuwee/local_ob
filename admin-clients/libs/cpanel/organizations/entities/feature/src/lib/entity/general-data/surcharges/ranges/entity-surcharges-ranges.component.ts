import { cleanRangesBeforeSave, RangeTableComponent } from '@admin-clients/shared/common/ui/components';
import { RangeElement } from '@admin-clients/shared-utility-models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        RangeTableComponent
    ],
    selector: 'app-entity-surcharges-ranges',
    templateUrl: './entity-surcharges-ranges.component.html'
})
export class EntitySurchargesRangesComponent implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();

    readonly surchargesForm = inject(FormBuilder).nonNullable.group({
        ranges: [null as RangeElement[]]
    });

    @Input() currencyCode: string;
    @Input() form: FormGroup;
    @Input() surchargesRequestCtrl: FormControl<RangeElement[]>;
    @Input() errorCtrl: FormControl<string>;
    @Input() data: RangeElement[];

    ngOnInit(): void {
        this.form.addControl(`${this.currencyCode}`, this.surchargesForm);

        this.surchargesRequestCtrl.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(surchargesRequest => {
                if (this.form.invalid) {
                    if (this.surchargesForm.valid || this.errorCtrl.value) return;
                    this.errorCtrl.setValue(this.currencyCode);
                } else {
                    surchargesRequest.push(...cleanRangesBeforeSave(this.surchargesForm.value.ranges)
                        .map(range => ({
                            ...range,
                            currency_code: this.currencyCode
                        }))
                    );
                }
                this.surchargesRequestCtrl.setValue(surchargesRequest, { emitEvent: false });
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }
}
