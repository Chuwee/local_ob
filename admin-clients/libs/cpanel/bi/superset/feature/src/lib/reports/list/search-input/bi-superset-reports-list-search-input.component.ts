import { BiReport, BiSupersetService } from '@admin-clients/cpanel/bi/data-access';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, viewChild } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule, MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MatIconButton } from '@angular/material/button';
import { MatFormFieldModule, MatPrefix } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';
import { debounceTime, first, map, Observable, of, switchMap } from 'rxjs';
import { BiSupersetReportsComponent } from '../../bi-superset-reports.component';
import { VmBiReportCategorySearch } from '../../models/vm-reports.model';
import { BiReportsListSearchInputHighlightPipe } from '@admin-clients/cpanel-bi-utility-utils';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-bi-superset-reports-list-search-input',
    imports: [
        AsyncPipe, ReactiveFormsModule, TranslatePipe, BiReportsListSearchInputHighlightPipe, MatPrefix, MatIconButton,
        MatFormFieldModule, MatIcon, MatInput, MatAutocompleteModule
    ],
    templateUrl: './bi-superset-reports-list-search-input.component.html'
})
export class BiSupersetReportsListSearchInputComponent extends BiSupersetReportsComponent implements OnInit {
    readonly #biService = inject(BiSupersetService);
    readonly #destroyRef = inject(DestroyRef);

    readonly _autocompleteTrigger = viewChild(MatAutocompleteTrigger);

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
        this._autocompleteTrigger().closePanel();
        const searchValue = this.searchInput.value;
        if (searchValue) {
            this.auth.impersonation.get$()
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

}
