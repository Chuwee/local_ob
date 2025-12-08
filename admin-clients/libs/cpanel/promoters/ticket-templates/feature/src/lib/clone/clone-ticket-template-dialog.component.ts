import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    TicketTemplate, TicketTemplateFieldRestriction,
    TicketTemplatesService, provideTicketTemplateService
} from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { Entity, EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { DialogSize, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { compareWithIdOrCode } from '@admin-clients/shared/data-access/models';
import { NgIf, NgFor, AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FlexModule } from '@angular/flex-layout/flex';
import { UntypedFormBuilder, UntypedFormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, tap } from 'rxjs/operators';

@Component({
    selector: 'app-clone-ticket-template-dialog',
    templateUrl: './clone-ticket-template-dialog.component.html',
    styleUrls: ['./clone-ticket-template-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        provideTicketTemplateService()
    ],
    imports: [
        FlexModule, MaterialModule, ReactiveFormsModule,
        FormControlErrorsComponent, NgIf, SelectSearchComponent, NgFor, AsyncPipe, TranslatePipe
    ]
})
export class CloneTicketTemplateDialogComponent implements OnInit {
    private _restrictions = TicketTemplateFieldRestriction;

    isInProgress$: Observable<boolean>;
    ticketTemplate: TicketTemplate;
    entities$: Observable<Entity[]>;
    isOperator$: Observable<boolean>;
    loading: boolean;
    form: UntypedFormGroup;
    compareWith = compareWithIdOrCode;

    constructor(
        private _dialogRef: MatDialogRef<CloneTicketTemplateDialogComponent>,
        private _ticketTemplatesService: TicketTemplatesService,
        private _auth: AuthenticationService,
        private _entitiesSrv: EntitiesBaseService,
        private _fb: UntypedFormBuilder,
        @Inject(MAT_DIALOG_DATA) data: { ticketTemplate: TicketTemplate }
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
        this.ticketTemplate = data.ticketTemplate;
    }

    ngOnInit(): void {
        this.isOperator$ = this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR])
            .pipe(
                filter(isOperator => isOperator !== null),
                tap(isOperator => {
                    if (isOperator) {
                        this._entitiesSrv.entityList.load({ limit: 999, offset: 0, sort: 'name:asc', type: 'EVENT_ENTITY' });
                        this.form.get('entity').enable();
                    } else {
                        this.form.get('entity').disable();
                    }
                })
            );

        this.entities$ = this._entitiesSrv.entityList.getData$()
            .pipe(filter(list => !!list));

        this.form = this._fb.group({
            name: [null, [Validators.required, Validators.maxLength(this._restrictions.nameMaxLength)]],
            entity: [this.ticketTemplate.entity, Validators.required]
        });

        this.isInProgress$ = this._ticketTemplatesService.isTicketTemplatesCloning$();
    }

    close(newVenueTemplateId: number = null): void {
        this._dialogRef.close(newVenueTemplateId);
    }

    cloneTicketTemplate(): void {
        if (this.form.valid) {
            const name = this.form.value.name;
            const entityId = this.form.value.entity?.id;
            const id = this.ticketTemplate.id;
            this._ticketTemplatesService.cloneTicketTemplate(id, name, entityId)
                .subscribe((id: number) => this.close(id));
        } else {
            this.form.markAllAsTouched();
        }
    }

}
