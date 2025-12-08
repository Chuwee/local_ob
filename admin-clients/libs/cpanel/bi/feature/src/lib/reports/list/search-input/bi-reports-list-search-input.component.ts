import { BiReport, BiService } from '@admin-clients/cpanel/bi/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { Platform } from '@angular/cdk/platform';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, ViewChild } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule, MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MatIconButton } from '@angular/material/button';
import { MatFormFieldModule, MatPrefix } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { debounceTime, first, map, Observable, of, switchMap } from 'rxjs';
import { BI_SUBMIT } from '../../bi-reports.routes';
import { VmBiReportCategorySearch } from '../../models/vm-reports.model';
import { BiReportsListSearchInputHighlightPipe } from './highlight/bi-reports-list-search-input-highlight.pipe';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-bi-reports-list-search-input',
    imports: [
        AsyncPipe, FlexModule, ReactiveFormsModule, TranslatePipe, BiReportsListSearchInputHighlightPipe, MatPrefix, MatIconButton,
        MatFormFieldModule, MatIcon, MatInput, MatAutocompleteModule
    ],
    templateUrl: './bi-reports-list-search-input.component.html'
})
export class BiReportsListSearchInputComponent implements OnInit {
    readonly #auth = inject(AuthenticationService);
    readonly #biService = inject(BiService);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    readonly #destroyRef = inject(DestroyRef);
    readonly #platform = inject(Platform);
    readonly #biSubmit = inject(BI_SUBMIT);

    @ViewChild(MatAutocompleteTrigger) private readonly _autocompleteTrigger: MatAutocompleteTrigger;

    readonly searchInput = inject(FormBuilder).control('');
    readonly isReportsSearch$ = this.#biService.reportsSearch.get$().pipe(map(biReports => !!biReports));
    readonly biReportsSuggestions$: Observable<VmBiReportCategorySearch[]> = this.searchInput.valueChanges
        .pipe(
            debounceTime(100),
            switchMap(searchValue => {
                if (!searchValue) {
                    return of(null);
                }
                return this.#biService.reportsList.get$()
                    .pipe(
                        first(),
                        map(biReports => {
                            const matchingValue = searchValue.toLowerCase().trim();
                            const matchingBiReports: BiReport[] = [];
                            let matchCount = 0;

                            for (const biReport of biReports) {
                                if (biReport.name.toLowerCase().includes(matchingValue)) {
                                    matchingBiReports.push(biReport);
                                    matchCount++;

                                    if (matchCount >= 10) {
                                        break; // Stop searching once 10 matches are found
                                    }
                                }
                            }

                            return matchingBiReports.reduce<VmBiReportCategorySearch[]>((acc, biReport) => {
                                const category = acc.find(category => category.name === biReport.category);
                                if (!category) {
                                    acc.push({
                                        name: biReport.category,
                                        reports: [biReport]
                                    });
                                    return acc;
                                }

                                category.reports.push(biReport);
                                return acc;
                            }, []);
                        })
                    );
            })
        );

    ngOnInit(): void {
        this.#biService.reportsSearch.get$()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(reportsSearch => {
                if (!reportsSearch) {
                    this.searchInput.reset();
                }
            });
    }

    search(): void {
        this._autocompleteTrigger.closePanel();
        const searchValue = this.searchInput.value;
        if (searchValue) {
            this.#auth.impersonation.get$()
                .pipe(first())
                .subscribe(impersonation => {
                    this.#biService.reportsSearch.load({ q: searchValue.trim(), impersonation });
                });
        } else {
            this.#biService.reportsSearch.clear();
        }
    }

    clearSearch(): void {
        this.#biService.reportsSearch.clear();
    }

    load(report: BiReport): void {
        if (report.url) {
            this.#auth.getLoggedUser$()
                .pipe(first())
                .subscribe(user => {
                    this.#biSubmit(report.url, user.reports.load, user.reports.logout, this.#platform);
                });
        } else {
            this.#router.navigate([report.id], { relativeTo: this.#route, queryParamsHandling: 'preserve' });
        }
    }
}
