import {
    biProviders, BiReport, BiReportPrompt, BiService, PostBiReportAnswerRequest
} from '@admin-clients/cpanel/bi/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { ChangeDetectionStrategy, Component, DestroyRef, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { TranslatePipe } from '@ngx-translate/core';
import { first } from 'rxjs';
import { BiReportPromptsDateRangePickerComponent } from './picker/bi-report-prompts-date-range-picker.component';
import { BiReportPromptsDateRangePresetsComponent } from './presets/bi-report-prompts-date-range-presets.component';

const dateRangesTypes = {
    picker: 'picker',
    presets: 'presets'
} as const;

type DateRangeTypes = typeof dateRangesTypes[keyof typeof dateRangesTypes];

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        BiReportPromptsDateRangePickerComponent, TranslatePipe, FlexLayoutModule, ReactiveFormsModule,
        BiReportPromptsDateRangePresetsComponent, MatRadioGroup, MatRadioButton
    ],
    providers: [biProviders],
    selector: 'app-bi-report-prompts-date-range',
    templateUrl: './bi-report-prompts-date-range.component.html'
})
export class BiReportPromptsDateRangeComponent implements OnInit {
    readonly #auth = inject(AuthenticationService);
    readonly #biReportsSrv = inject(BiService);
    readonly #destroyRef = inject(DestroyRef);

    readonly dateRangesTypes = dateRangesTypes;

    readonly formControl = inject(FormBuilder)
        .nonNullable.control<DateRangeTypes>(dateRangesTypes.presets);

    @Input() biReportPrompt: BiReportPrompt;
    @Input() biReport: BiReport;
    @Input() form: FormGroup;
    @Input() requestCtrl: FormControl<PostBiReportAnswerRequest>;

    @Output() loadingEmitter = new EventEmitter<{ promptId: string; isLoading: boolean }>();

    ngOnInit(): void {
        this.#auth.impersonation.get$()
            .pipe(first())
            .subscribe(impersonation => {
                this.#biReportsSrv.reportPromptAnswers.load(
                    this.biReport.id,
                    this.biReportPrompt.id,
                    { limit: 200, offset: 0, impersonation }
                );
            });

        this.#biReportsSrv.reportPromptAnswers.loading$()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(isLoading => this.loadingEmitter.emit({ promptId: this.biReportPrompt.id, isLoading }));
    }
}
