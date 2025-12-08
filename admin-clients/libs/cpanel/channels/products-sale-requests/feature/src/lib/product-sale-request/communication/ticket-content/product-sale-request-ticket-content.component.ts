
import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import {
    ProductCommunicationService
} from '@admin-clients/cpanel-products-communication-data-access';
import { TicketTemplatesService } from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { LanguageBarComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { BehaviorSubject, first, map, tap } from 'rxjs';
import { ProductTicketContentPassbookComponent } from './ticket-content-passbook/product-ticket-content-passbook.component';
import { ProductTicketContentPdfComponent } from './ticket-content-pdf/product-ticket-content-pdf.component';

@Component({
    selector: 'app-product-sale-request-ticket-content',
    standalone: true,
    imports: [
        FormContainerComponent, LanguageBarComponent, MaterialModule, ReactiveFormsModule,
        AsyncPipe,
        ProductTicketContentPassbookComponent, ProductTicketContentPdfComponent
    ],
    templateUrl: './product-sale-request-ticket-content.component.html',
    styleUrls: ['./product-sale-request-ticket-content.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})

export class ProductSaleRequestTicketContentComponent implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #productsService = inject(ProductsService);
    readonly #productCommunicationService = inject(ProductCommunicationService);
    readonly #ticketTemplatesService = inject(TicketTemplatesService);

    readonly form = this.#fb.group({});

    isLoadingOrSaving$ = booleanOrMerge([
        this.#productsService.product.inProgress$(),
        this.#productsService.product.languages.inProgress$(),
        this.#productCommunicationService.ticketsContents.passbook.images.loading$(),
        this.#productCommunicationService.ticketsContents.passbook.texts.loading$(),
        this.#productCommunicationService.ticketsContents.pdf.images.loading$(),
        this.#productCommunicationService.ticketsContents.pdf.texts.loading$(),
        this.#productCommunicationService.productTemplates.loading$(),
        this.#ticketTemplatesService.isTicketTemplatesLoading$()
    ]);

    readonly #selectedLanguage = new BehaviorSubject<string>(null);
    readonly selectedLanguage$ = this.#selectedLanguage.asObservable();
    readonly languages$ = this.#productsService.product.languages.get$().pipe(
        first(Boolean),
        tap(languages => this.#selectedLanguage.next(languages.find(lang => lang.is_default).code)),
        map(languages => languages.map(lang => lang.code))
    );

    ngOnInit(): void {
        this.#productsService.product.get$().pipe(first(Boolean)).subscribe(product => {
            this.#productsService.product.languages.load(product.product_id);
        });
    }

    changeLanguage(newLanguage: string): void {
        this.#selectedLanguage.next(newLanguage);
    }
}
