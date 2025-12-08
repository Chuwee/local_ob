/* eslint-disable @typescript-eslint/dot-notation */
import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, isMultiCurrency$, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { categoriesProviders } from '@admin-clients/cpanel/organizations/data-access';
import { ProductsService, PutProductLanguage, PutProductRequest } from '@admin-clients/cpanel/products/my-products/data-access';
import { DialogSize, EphemeralMessageService, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, QueryList, ViewChildren } from '@angular/core';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormBuilder } from '@angular/forms';
import { MatExpansionModule, MatExpansionPanel } from '@angular/material/expansion';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, forkJoin, Observable, of, shareReplay, switchMap, tap, throwError } from 'rxjs';

import { ProductPrincipalInfoCurrencyComponent } from './currency/product-principal-info-currency.component';
import { ProductPrincipalInfoDataComponent } from './data/product-principal-info-data.component';
import { ProductPrincipalInfoLanguageComponent } from './language/product-principal-info-language.component';
import { CategoriesSelectionComponent } from '@admin-clients/cpanel/shared/ui/components';

@Component({
    selector: 'app-product-principal-info',
    providers: [
        categoriesProviders
    ],
    imports: [
        AsyncPipe, MatExpansionModule, MatProgressSpinnerModule,
        FormContainerComponent, TranslatePipe, FlexModule, FlexLayoutModule,
        ProductPrincipalInfoDataComponent,
        ProductPrincipalInfoLanguageComponent,
        ProductPrincipalInfoCurrencyComponent, CategoriesSelectionComponent
    ],
    templateUrl: './product-principal-info.component.html',
    styleUrls: ['./product-principal-info.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductPrincipalInfoComponent implements WritingComponent {
    readonly #fb = inject(FormBuilder);
    readonly #productsSrv = inject(ProductsService);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #auth = inject(AuthenticationService);
    readonly #messageDialogSrv = inject(MessageDialogService);
    #productId: number;

    @ViewChildren(MatExpansionPanel) private readonly _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    readonly form = this.#fb.nonNullable.group({
        putProductCtrl: null as Partial<PutProductRequest>
    });

    readonly canWrite$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.EVN_MGR]).pipe(
        tap(canWrite => {
            if (!canWrite) {
                this.form.disable({ emitEvent: false });
            }
        }),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    readonly product$ = this.#productsSrv.product.get$().pipe(
        filter(Boolean),
        tap(product => this.#productId = product.product_id),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    readonly isLoadingOrSaving$ = booleanOrMerge([
        this.#productsSrv.product.inProgress$(),
        this.#productsSrv.product.languages.inProgress$()
    ]);

    //TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
    readonly isMultiCurrency$ = isMultiCurrency$();

    cancel(): void {
        this.reloadModels();
    }

    save$(): Observable<void[]> {
        if (this.form.valid) {
            // Makes child component set the value of putEventCtrl
            this.form.controls.putProductCtrl.setValue({});
            const { currency_code: currencyCode } = this.form.controls.putProductCtrl.value;
            let canContinue$: Observable<boolean>;
            if (currencyCode) {
                canContinue$ = this.#messageDialogSrv.showWarn({
                    size: DialogSize.SMALL,
                    title: 'FORMS.INFOS.CHANGE_CURRENCY_WARN',
                    message: 'PRODUCT.FORMS.INFOS.CHANGE_CURRENCY_WARN_DETAILS',
                    showCancelButton: true
                });
            } else {
                canContinue$ = of(true);
            }
            const putProduct$ = this.#productsSrv.product.update(this.#productId, this.form.controls.putProductCtrl.value);
            const obsArray$: Observable<void>[] = [putProduct$];

            if (this.form.controls['language']?.touched) {
                const langs = this.mapLanguages(this.form.controls['language']?.value);
                const putProductLangs$ = this.#productsSrv.product.languages.update(this.#productId, langs);
                obsArray$.unshift(putProductLangs$);
            }

            const updateProductPrincipalInfo$ = forkJoin(obsArray$).pipe(tap(() => this.#ephemeralMessageSrv.showSaveSuccess()));

            return canContinue$.pipe(switchMap(canContinue => canContinue ? updateProductPrincipalInfo$ : of(null)));
        } else {
            this.form.markAllAsTouched();
            //SetValue in order to rerender child components with form fields in order to show input errors.
            this.form.setValue(this.form.getRawValue());
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            this.form.controls.putProductCtrl.reset(null, { emitEvent: false });
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe(() => this.reloadModels());
    }

    private reloadModels(): void {
        this.#productsSrv.product.load(this.#productId);
        this.form.markAsPristine();
        this.form.markAsUntouched();
        this.form.controls.putProductCtrl.reset(null, { emitEvent: false });
    }

    private mapLanguages({ defaultLanguage, languagesGroup }: {
        defaultLanguage: string;
        languagesGroup: Record<string, boolean>;
    }
    ): PutProductLanguage[] {
        const langs = Object.keys(languagesGroup)
            .filter(langKey => languagesGroup[langKey])
            .map(lang => ({
                code: lang,
                is_default: lang === defaultLanguage
            }));
        return langs;
    }
}
