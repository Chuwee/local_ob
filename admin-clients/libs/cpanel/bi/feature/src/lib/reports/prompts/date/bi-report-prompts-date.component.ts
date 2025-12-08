import { BiReportPrompt, PostBiReportAnswerRequest } from '@admin-clients/cpanel/bi/data-access';
import { DateTimeModule } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        DateTimeModule,
        ReactiveFormsModule
    ],
    selector: 'app-bi-report-prompts-date',
    templateUrl: './bi-report-prompts-date.component.html'
})
export class BiReportPromptsDateComponent implements OnInit, OnDestroy {
    private readonly _fb = inject(FormBuilder);
    private readonly _destroyRef = inject(DestroyRef);

    readonly formControl = this._fb.nonNullable.control('');

    @Input() form: FormGroup;
    @Input() requestCtrl: FormControl<PostBiReportAnswerRequest>;
    @Input() biReportPrompt: BiReportPrompt;

    ngOnInit(): void {
        this.form.addControl(this.biReportPrompt.id, this.formControl, { emitEvent: false });

        if (this.biReportPrompt.restrictions.required) {
            this.formControl.setValidators(Validators.required);
        }

        this.requestCtrl.valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(request => {
                if (this.form.invalid) return;
                request.push({
                    prompt_id: this.biReportPrompt.id,
                    answers: this.formControl.value ? [this.formControl.value] : []
                });
                this.requestCtrl.setValue(request, { emitEvent: false });
            });
    }

    ngOnDestroy(): void {
        this.form.removeControl(this.biReportPrompt.id, { emitEvent: false });
    }
}
