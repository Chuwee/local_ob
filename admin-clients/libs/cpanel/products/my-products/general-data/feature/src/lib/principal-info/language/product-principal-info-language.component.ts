import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { LanguageSelector, LanguageSelectorComponent } from '@admin-clients/shared/common/ui/components';
import { CommonModule } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit, ViewChild, inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject, combineLatest, filter, map, takeUntil } from 'rxjs';

@Component({
    selector: 'app-product-principal-info-language',
    imports: [CommonModule, TranslatePipe, LanguageSelectorComponent],
    templateUrl: './product-principal-info-language.component.html',
    styleUrls: ['./product-principal-info-language.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductPrincipalInfoLanguageComponent implements OnInit, AfterViewInit, OnDestroy {
    private readonly _entitiesSrv = inject(EntitiesBaseService);
    private readonly _productsSrv = inject(ProductsService);
    private readonly _onDestroy = new Subject<void>();

    @ViewChild(LanguageSelectorComponent) private readonly _languageSelector: LanguageSelectorComponent;

    readonly languageSelectorData$: Observable<LanguageSelector> =
        combineLatest([
            this._productsSrv.product.languages.get$(),
            this._entitiesSrv.getEntityAvailableLenguages$()
        ]).pipe(
            filter(sources => sources.every(Boolean)),
            map(([productLangs, languages]) => ({
                default: productLangs ? productLangs.find(lang => lang.is_default)?.code : '',
                selected: productLangs ? productLangs.map(lang => lang.code) : [],
                languages
            }))
        );

    @Input() form: FormGroup;

    ngOnInit(): void {
        this._productsSrv.product.get$()
            .pipe(
                filter(Boolean),
                takeUntil(this._onDestroy)
            )
            .subscribe(product => {
                this._productsSrv.product.languages.load(product.product_id);
            });
    }

    ngAfterViewInit(): void {
        this.form.addControl('language', this._languageSelector.form, { emitEvent: false });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._productsSrv.product.languages.clear();
    }
}
