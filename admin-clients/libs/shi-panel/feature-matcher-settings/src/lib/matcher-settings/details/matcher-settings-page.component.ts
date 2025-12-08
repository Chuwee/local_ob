import { MessageDialogService, NavTabsMenuComponent, UnsavedChangesDialogResult } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { SupplierSelectionButtonComponent } from '@admin-clients/shi-panel/feature-supplier-selection';
import { SupplierName } from '@admin-clients/shi-panel/utility-models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatTabsModule } from '@angular/material/tabs';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, catchError, map, Observable, of, switchMap, tap } from 'rxjs';
import { MatcherSettingsApi } from '../../api/matcher-settings.api';
import { MatcherSettingsService } from '../../matcher-settings.service';
import { MatcherSettingsState } from '../../state/matcher-settings.state';

@Component({
    imports: [
        CommonModule, RouterModule, TranslatePipe, MatTabsModule, FlexLayoutModule,
        NavTabsMenuComponent, SupplierSelectionButtonComponent
    ],
    selector: 'app-matcher-settings-page',
    templateUrl: './matcher-settings-page.component.html',
    styleUrls: ['./matcher-settings-page.component.scss'],
    providers: [MatcherSettingsService, MatcherSettingsApi, MatcherSettingsState],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MatcherSettingsPageComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #matcherSettingsService = inject(MatcherSettingsService);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #activatedRoute = inject(ActivatedRoute);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);

    readonly #selectedSupplierName = new BehaviorSubject<string>(SupplierName.tevo);
    #childComponent: WritingComponent;

    readonly selectedSupplierName$ = this.#selectedSupplierName.asObservable();
    readonly isLoading$ = this.#matcherSettingsService.matcherConfiguration.isLoading$();
    readonly deepPath$ = getDeepPath$(this.#router, this.#route);

    ngOnInit(): void {
        this.#selectedSupplierName
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(supplierId => {
                this.#router.navigate([], {
                    relativeTo: this.#activatedRoute,
                    queryParams: { supplier: supplierId },
                    queryParamsHandling: 'merge'
                });
            });

        this.#selectedSupplierName.next(this.#activatedRoute.snapshot.queryParams['supplier'] || SupplierName.tevo);
    }

    childComponentChange(component: WritingComponent): void {
        this.#childComponent = component;
    }

    changeSelectedSupplier(supplierName: string): void {
        this.changeSelectedSupplier$(supplierName).subscribe();
    }

    private changeSelectedSupplier$(supplierName: string): Observable<boolean> {
        if (!this.#childComponent.form.dirty || this.#selectedSupplierName.getValue() === supplierName) {
            this.#selectedSupplierName.next(supplierName);
            return of(true);
        }
        return this.#messageDialogService.openRichUnsavedChangesWarn().pipe(
            switchMap(result => {
                if (result === UnsavedChangesDialogResult.cancel) {
                    return of(false);
                }
                if (result === UnsavedChangesDialogResult.continue) {
                    this.#selectedSupplierName.next(supplierName);
                    return of(true);
                }
                return this.#childComponent.save$().pipe(
                    tap(() => this.#selectedSupplierName.next(supplierName)),
                    map(() => true),
                    catchError(() => of(false))
                );
            })
        );

    }
}
