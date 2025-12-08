import {
    biProviders,
    BiReport,
    BiReportPrompt,
    BiService,
    PostBiReportAnswerRequest
} from '@admin-clients/cpanel/bi/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { AsyncPipe } from '@angular/common';
import {
    ChangeDetectionStrategy,
    Component, DestroyRef,
    EventEmitter,
    inject,
    Input,
    OnInit,
    Output
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup } from '@angular/forms';
import { BehaviorSubject, first } from 'rxjs';
import { BiReportPromptsSelectionCheckComponent } from './check/bi-report-prompts-selection-check.component';
import { BiReportPromptsSelectionMultipleComponent } from './multiple/bi-report-prompts-selection-multiple.component';
import { BiReportPromptsSelectionRadioComponent } from './radio/bi-report-prompts-selection-radio.component';
import { BiReportPromptsSelectionSingleComponent } from './single/bi-report-prompts-selection-single.component';

enum SelectionType {
    radio = 'radio',
    singleDropdown = 'singleDropdown',
    checkbox = 'checkbox',
    multipleDropdown = 'multipleDropdown'
}

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        BiReportPromptsSelectionRadioComponent, BiReportPromptsSelectionSingleComponent, BiReportPromptsSelectionCheckComponent,
        BiReportPromptsSelectionMultipleComponent, AsyncPipe
    ],
    providers: [biProviders],
    selector: 'app-bi-report-prompts-selection',
    templateUrl: './bi-report-prompts-selection.component.html'
})
export class BiReportPromptsSelectionComponent implements OnInit {
    readonly #biReportsSrv = inject(BiService);
    readonly #auth = inject(AuthenticationService);
    readonly #destroyRef = inject(DestroyRef);

    readonly #maxListElements = 4;

    readonly answers$ = this.#biReportsSrv.reportPromptAnswers.get$().pipe(first(Boolean));
    readonly selectionType = SelectionType;
    readonly selectionTypeBS = new BehaviorSubject<SelectionType>(null);
    readonly limit = 10;

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
                    { limit: this.limit, offset: 0, impersonation }
                );
            });

        this.#biReportsSrv.reportPromptAnswers.loading$()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(isLoading => this.loadingEmitter.emit({ promptId: this.biReportPrompt.id, isLoading }));

        this.#biReportsSrv.reportPromptAnswers.get$()
            .pipe(first(Boolean))
            .subscribe(answers => {
                const totalAnswers = answers.length;
                if (this.biReportPrompt.restrictions.max_selections === 1 && totalAnswers < this.#maxListElements) {
                    this.selectionTypeBS.next(SelectionType.radio);
                } else if (this.biReportPrompt.restrictions.max_selections === 1) {
                    this.selectionTypeBS.next(SelectionType.singleDropdown);
                } else if (this.biReportPrompt.restrictions.max_selections !== 1 && totalAnswers < this.#maxListElements) {
                    this.selectionTypeBS.next(SelectionType.checkbox);
                } else if (this.biReportPrompt.restrictions.max_selections !== 1) {
                    this.selectionTypeBS.next(SelectionType.multipleDropdown);
                }
            });
    }
}
