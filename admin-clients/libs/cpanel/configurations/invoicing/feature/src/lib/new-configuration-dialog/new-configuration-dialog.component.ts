import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { InvoicingService, InvoicingEntityOperatorTypes } from '@admin-clients/cpanel-configurations-invoicing-data-access';
import { EntitiesFilterFields, Entity, GetEntitiesRequest } from '@admin-clients/shared/common/data-access';
import {
    minMaxRangeValidator, DialogSize, SelectServerSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, Observable } from 'rxjs';

@Component({
    selector: 'app-new-invoicing-config-dialog',
    templateUrl: './new-configuration-dialog.component.html',
    styleUrls: ['./new-configuration-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule, MaterialModule, ReactiveFormsModule, FlexModule,
        TranslatePipe, FormControlErrorsComponent,
        SelectServerSearchComponent
    ]
})
export class NewInvoicingConfigDialogComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #dialogRef = inject(MatDialogRef<NewInvoicingConfigDialogComponent, boolean>);
    readonly #invoicingSrv = inject(InvoicingService);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #fb = inject(UntypedFormBuilder);

    readonly $isEntityConfig = signal(false);

    readonly entities$: Observable<Entity[]> = this.#entitiesSrv.entityList.getData$().pipe(filter(list => !!list));

    readonly invoicingOperatorType = InvoicingEntityOperatorTypes;

    readonly moreAvailableEntities$ = this.#entitiesSrv.entityList.getMetadata$()
        .pipe(map(metadata => !!metadata && metadata.offset + metadata.limit < metadata.total));

    form: UntypedFormGroup;

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
        this.$isEntityConfig.set(this.#dialogRef._containerInstance._config.data?.isEntityConfig);
    }

    ngOnInit(): void {
        const minCtrl = this.#fb.control(0);
        const maxCtrl = this.#fb.control(0);

        this.form = this.#fb.group({
            operatorId: [null, Validators.required],
            config: this.#fb.group({
                fixed: [0, [Validators.min(0)]],
                variable: [0, [Validators.min(0)]],
                min: minCtrl,
                max: maxCtrl,
                invitation: [0, [Validators.min(0)]],
                refund: [0, [Validators.min(0)]],
                type: [
                    this.$isEntityConfig() ? InvoicingEntityOperatorTypes.undefined : null as InvoicingEntityOperatorTypes,
                    Validators.required
                ]
            })
        });

        minCtrl.setValidators([Validators.min(0), minMaxRangeValidator(minCtrl, maxCtrl)]);
        maxCtrl.setValidators([Validators.min(0), minMaxRangeValidator(minCtrl, maxCtrl)]);

        this.form.controls['config'].valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => {
                minCtrl.updateValueAndValidity({ emitEvent: false });
                maxCtrl.updateValueAndValidity({ emitEvent: false });
            });
    }

    createConfig(): void {
        const { operatorId, config } = this.form.getRawValue();
        if (this.form.valid && operatorId?.id) {
            this.#invoicingSrv.createEntityConfig(operatorId.id, config).subscribe(() => this.close(true));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
        }
    }

    close(created = false): void {
        this.#dialogRef.close(created);
    }

    loadEntities({ q, nextPage }: { q?: string; nextPage?: boolean }): void {
        const request: GetEntitiesRequest = {
            q,
            type: this.$isEntityConfig() ? 'ENTITY' : 'OPERATOR',
            sort: 'name:asc',
            limit: 100,
            fields: [EntitiesFilterFields.name]
        };
        if (!nextPage) {
            this.#entitiesSrv.entityList.load(request);
        } else {
            this.#entitiesSrv.entityList.loadMore(request);
        }
    }
}
