import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Pack, PacksService } from '@admin-clients/cpanel/packs/my-packs/data-access';
import { DateTimeModule, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { dateIsAfter, dateIsBefore, dateTimeGroupValidator, dateTimeValidator } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { RouterModule } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Observable, throwError } from 'rxjs';
import { filter, map, tap } from 'rxjs/operators';

@Component({
    selector: 'app-pack-programming',
    templateUrl: './pack-programming.component.html',
    styleUrls: ['./pack-programming.component.scss'],
    imports: [
        FlexLayoutModule, AsyncPipe, TranslatePipe, RouterModule, MatProgressSpinnerModule, ReactiveFormsModule,
        FormContainerComponent, MatRadioModule, DateTimeModule, MatFormFieldModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PackProgrammingComponent implements OnInit {
    readonly #packsSrv = inject(PacksService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #translate = inject(TranslateService);

    readonly loading$ = this.#packsSrv.pack.loading$();
    readonly form = this.#fb.group({
        pack_period: this.#fb.group({
            type: [null, Validators.required],
            start_date: [{ value: null, disabled: true }, Validators.required],
            end_date: [{ value: null, disabled: true }, Validators.required]
        }, {
            validators: [dateTimeGroupValidator(dateIsBefore, 'startDateAfterEndDate', 'start_date', 'end_date')]
        })
    });

    readonly $packPeriodType = toSignal(this.form.get('pack_period.type').valueChanges
        .pipe(map(packPeriodType => {
            const startDate = this.form.get('pack_period.start_date');
            const endDate = this.form.get('pack_period.end_date');
            if (packPeriodType === 'AUTOMATIC') {
                startDate.disable();
                endDate.disable();
            } else {
                startDate.enable();
                endDate.enable();
            }
            return packPeriodType;
        })));

    readonly $pack = toSignal(this.#packsSrv.pack.get$().pipe(
        filter(Boolean),
        tap(pack =>
            this.updateFormValues(pack)
        )
    ));

    ngOnInit(): void {
        this.loadPack();
        this.initValidators();
    }

    cancel(): void {
        this.loadPack();
    }

    save(): void {
        this.save$().subscribe(() =>
            this.form.markAsPristine()
        );
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const data = this.form.value;
            return this.#packsSrv.pack.update(this.$pack().id, data)
                .pipe(
                    tap(() => {
                        this.#ephemeralMessageSrv.showSaveSuccess();
                        this.loadPack();
                    }));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => new Error('Invalid form'));
        }
    }

    private loadPack(): void {
        this.#packsSrv.pack.load(this.$pack().id);
    }

    private updateFormValues(pack: Pack): void {
        this.form.reset({
            ...pack
        });
        this.form.markAsPristine();
    }

    private initValidators(): void {
        const startDate = this.form.get('pack_period.start_date');
        const endDate = this.form.get('pack_period.end_date');
        startDate.addValidators(
            dateTimeValidator(
                dateIsBefore, 'startDateAfterEndDate', endDate,
                this.#translate.instant('DATES.END_DATE').toLowerCase()
            )
        );

        endDate.addValidators(
            dateTimeValidator(
                dateIsAfter, 'startDateAfterEndDate', startDate,
                this.#translate.instant('DATES.END_DATE').toLowerCase()
            )
        );
    }
}
