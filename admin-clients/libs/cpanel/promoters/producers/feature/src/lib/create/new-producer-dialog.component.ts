import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { PostProducer, ProducerFieldsRestrictions, ProducersService } from '@admin-clients/cpanel/promoters/producers/data-access';
import { Entity, EntitiesBaseService, EntitiesFilterFields } from '@admin-clients/shared/common/data-access';
import { DialogSize, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { NgIf, NgFor, AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, ElementRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexModule } from '@angular/flex-layout/flex';
import { Validators, ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-new-producer-dialog',
    templateUrl: './new-producer-dialog.component.html',
    styleUrls: ['./new-producer-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, FlexModule, ReactiveFormsModule,
        NgIf, SelectSearchComponent, NgFor, EllipsifyDirective, AsyncPipe,
        TranslatePipe
    ]
})

export class NewProducerDialogComponent implements OnInit {

    #canSelectEntity = false;
    readonly #dialogRef = inject(MatDialogRef<NewProducerDialogComponent>);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #producersService = inject(ProducersService);
    readonly #fb = inject(FormBuilder);
    readonly #auth = inject(AuthenticationService);
    readonly #elemRef = inject(ElementRef);
    readonly #destroyRef = inject(DestroyRef);
    readonly canSelectEntity$ = this.#auth.canReadMultipleEntities$();
    readonly producerSaving$ = this.#producersService.isProducerSaving$();
    readonly restrictions = ProducerFieldsRestrictions;

    readonly form = this.#fb.group({
        entity: [{ value: null as Entity, disabled: true }, Validators.required],
        name: [null as string, [
            Validators.required,
            Validators.maxLength(ProducerFieldsRestrictions.producerNameLength)
        ]],
        tax_id: [null as string, [
            Validators.required,
            Validators.maxLength(ProducerFieldsRestrictions.producerTaxIdMaxLength),
            Validators.pattern(ProducerFieldsRestrictions.producerTaxIdPattern)
        ]],
        social_reason: [null as string, [
            Validators.required,
            Validators.maxLength(ProducerFieldsRestrictions.producerSocialReasonMaxLength)
        ]]
    });

    entities$: Observable<Entity[]>;

    ngOnInit(): void {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
        this.canSelectEntity$
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(canSelectEntity => {
                if (canSelectEntity) {
                    this.#entitiesService.entityList.load({
                        limit: 999,
                        sort: 'name:asc',
                        fields: [EntitiesFilterFields.name],
                        type: 'MULTI_PRODUCER'
                    });
                    this.entities$ = this.#entitiesService.entityList.getData$();
                    this.form.get('entity').enable();
                    this.#canSelectEntity = true;
                }
            });
    }

    createProducer(): void {
        if (this.isValid()) {
            this.#producersService.createProducer(this._producer)
                .subscribe(id => this.close(id));
        }
    }

    close(channelId: number = null): void {
        this.#dialogRef.close(channelId);
    }

    private isValid(): boolean {
        if (this.form.valid) {
            return true;
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(this.#elemRef.nativeElement);
            return false;
        }
    }

    private get _producer(): PostProducer {
        return {
            name: this.form.value.name,
            nif: this.form.value.tax_id,
            social_reason: this.form.value.social_reason,
            entity_id: this.#canSelectEntity ? this.form.value.entity.id : null
        };
    }
}
