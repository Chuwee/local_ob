import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ProducersService, PutProducerDetails } from '@admin-clients/cpanel/promoters/producers/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { NgIf, AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, QueryList, ViewChild, ViewChildren } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatExpansionPanel, MatExpansionModule } from '@angular/material/expansion';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject, switchMap, throwError } from 'rxjs';
import { first, tap } from 'rxjs/operators';
import { ProducerBasicComponent } from './basic/producer-basic.component';
import { ProducerContactComponent } from './contact/producer-contact.component';
import {
    ProducerInvoicePrefixesComponent
} from './invoice-prefixes/producer-invoice-prefixes.component';

@Component({
    selector: 'app-producer-general-data',
    templateUrl: './producer-general-data.component.html',
    styleUrls: ['./producer-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, MatExpansionModule, ProducerBasicComponent,
        ProducerContactComponent, ProducerInvoicePrefixesComponent, NgIf,
        MatProgressSpinnerModule, AsyncPipe, TranslatePipe
    ]
})
export class ProducerGeneralDataComponent implements WritingComponent, OnDestroy {
    private readonly _fb = inject(UntypedFormBuilder);
    private readonly _producerSrv = inject(ProducersService);
    private readonly _entitiesSrv = inject(EntitiesBaseService);
    private readonly _ephemeralMessage = inject(EphemeralMessageService);

    private readonly _onDestroy = new Subject<void>();

    @ViewChildren(MatExpansionPanel) private readonly _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;
    @ViewChild(ProducerBasicComponent) private readonly _producerBasicComponent: ProducerBasicComponent;
    @ViewChild(ProducerContactComponent) private readonly _producerContactComponent: ProducerContactComponent;
    @ViewChild(ProducerInvoicePrefixesComponent) private readonly _producerInvoicePrefixesComponent: ProducerInvoicePrefixesComponent;

    readonly form = this._fb.group({});

    readonly isInProgress$ = booleanOrMerge([
        this._producerSrv.isProducerLoading$(),
        this._producerSrv.isProducerSaving$(),
        this._producerSrv.isProducersListLoading$(),
        this._producerSrv.invoiceProvider.loading$(),
        this._entitiesSrv.isEntityLoading$()
    ]);

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    cancel(): void {
        this._producerSrv.getProducer$()
            .pipe(first())
            .subscribe(producer => this._producerSrv.loadProducer(producer.id));
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void> {
        return this._producerSrv.getProducer$()
            .pipe(
                first(),
                switchMap(producer => {
                    this.form.markAllAsTouched();
                    if (this.form.valid) {
                        let result: PutProducerDetails = { id: producer.id };
                        result = this._producerBasicComponent.getResult(result);
                        result = this._producerContactComponent.getResult(result);
                        result = this._producerInvoicePrefixesComponent.getResult(result);
                        return this._producerSrv.saveProducerDetails(result).pipe(tap(() => {
                            this._producerSrv.loadProducer(producer.id);
                            this._ephemeralMessage.showSaveSuccess();
                        }));
                    } else {
                        this._producerBasicComponent.markForCheck();
                        this._producerContactComponent.markForCheck();
                        scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
                        return throwError(() => 'invalid form');
                    }
                })
            );
    }
}
