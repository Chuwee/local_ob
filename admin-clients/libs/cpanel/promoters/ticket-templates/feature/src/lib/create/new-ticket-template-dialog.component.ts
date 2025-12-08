import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { TicketTemplateDesign, TicketTemplateFieldRestriction, TicketTemplateFormat, TicketTemplatesService } from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { EntitiesBaseService, EntitiesFilterFields } from '@admin-clients/shared/common/data-access';
import { DialogSize, ObDialog, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, ElementRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexModule } from '@angular/flex-layout/flex';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, of, withLatestFrom } from 'rxjs';
import { map, startWith, switchMap, take, tap } from 'rxjs/operators';
import { compareObjectsById } from '../ticket-templates.utils';
import { IdName } from '@admin-clients/shared/data-access/models';

@Component({
    selector: 'app-new-ticket-template-dialog',
    templateUrl: './new-ticket-template-dialog.component.html',
    styleUrls: ['./new-ticket-template-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexModule, ReactiveFormsModule, SelectSearchComponent, EllipsifyDirective, MaterialModule, AsyncPipe, TranslatePipe
    ]
})
export class NewTicketTemplateDialogComponent extends ObDialog<NewTicketTemplateDialogComponent, null, number> {
    readonly #destroyRef = inject(DestroyRef);
    readonly #auth = inject(AuthenticationService);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #ticketTemplatesService = inject(TicketTemplatesService);
    readonly #elemRef = inject(ElementRef);

    readonly canSelectEntity$ = this.#auth.canReadMultipleEntities$();

    readonly form = inject(FormBuilder).group({
        entity: [null as IdName, Validators.required],
        name: [null as string, [Validators.required, Validators.maxLength(TicketTemplateFieldRestriction.nameMaxLength)]],
        format: TicketTemplateFormat.pdf,
        design: [null as TicketTemplateDesign, Validators.required]
    });

    readonly entities$ = this.canSelectEntity$.pipe(
        take(1),
        tap(canSelectEntity => {
            if (canSelectEntity) {
                this.#entitiesService.entityList.load({
                    limit: 999,
                    sort: 'name:asc',
                    fields: [EntitiesFilterFields.name, EntitiesFilterFields.allowHardTicketPdf]
                });
            }
        }),
        switchMap(() => this.#entitiesService.entityList.getData$())
    );

    readonly formats$ = this.#entitiesService.getEntity$()
        .pipe(map(entity =>
            [TicketTemplateFormat.pdf, TicketTemplateFormat.printer]
                .concat(entity?.settings.allow_hard_ticket_pdf ? [TicketTemplateFormat.hardTicketPdf] : [])
        ));

    readonly designs$ = combineLatest([
        this.#ticketTemplatesService.getDesignsList$(),
        this.form.controls.format.valueChanges.pipe(startWith(this.form.value.format))
    ])
        .pipe(map(([designs, format]) => designs.filter(design => design.format === format || !design)));

    readonly compareObjectsById = compareObjectsById;
    readonly maxTicketTemplateNameLength = TicketTemplateFieldRestriction.nameMaxLength;

    readonly saving$ = this.#ticketTemplatesService.isTicketTemplateInProgress$();

    constructor() {
        super(DialogSize.MEDIUM);
        this.#entitiesService.clearEntity();
        this.#ticketTemplatesService.loadDesigns();
        // entity load on selection, non operator user loads one time
        this.canSelectEntity$
            .pipe(
                take(1),
                withLatestFrom(this.#auth.getLoggedUser$()),
                switchMap(([canSelectEntity, user]) => {
                    // Runs until destroyed on operator users to load every selected entity.
                    // Non operator sets the entity in the form, and emits one time to load user entity.
                    if (canSelectEntity) {
                        return this.form.controls.entity.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef));
                    } else {
                        this.form.patchValue({ entity: user.entity });
                        return of(user.entity);
                    }
                })
            )
            .subscribe(entity => this.#entitiesService.loadEntity(entity.id));

    }

    create(): void {
        if (this.form.valid) {
            this.#ticketTemplatesService.createTicketTemplate({
                name: this.form.value.name,
                entity_id: this.form.value.entity.id,
                design_id: this.form.value.design.id
            })
                .subscribe(id => this.close(id));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(this.#elemRef.nativeElement);
        }
    }

    close(ticketTemplateId?: number): void {
        this.dialogRef.close(ticketTemplateId);
    }
}
