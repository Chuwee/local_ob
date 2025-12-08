import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import {
    PostTicketPassbok, TicketPassbookType, TicketsPassbookService, TicketsPassbookState, VMCreateTicketPassbok
} from '@admin-clients/cpanel-promoters-tickets-passbook-data-access';
import { EntitiesBaseService, EntitiesBaseState, Entity, EntitiesFilterFields } from '@admin-clients/shared/common/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, OnInit, OnDestroy, inject } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Subject, Observable, combineLatest, of } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, take, tap } from 'rxjs/operators';

@Component({
    selector: 'app-new-ticket-passbook-dialog',
    templateUrl: './new-ticket-passbook-dialog.component.html',
    styleUrls: ['./new-ticket-passbook-dialog.component.scss'],
    providers: [
        TicketsPassbookState,
        TicketsPassbookService,
        EntitiesBaseService,
        EntitiesBaseState
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class NewTicketPassbookDialogComponent implements OnInit, OnDestroy {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ticketsPassbookSrv = inject(TicketsPassbookService);
    readonly #auth = inject(AuthenticationService);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #dialogRef = inject(MatDialogRef<NewTicketPassbookDialogComponent>);

    private _canSelectEntity: boolean;
    private _onDestroy: Subject<void> = new Subject();
    readonly passbookTypes = TicketPassbookType;
    readonly canSelectEntity$ = this.#auth.canReadMultipleEntities$()
        .pipe(first(), tap(canReadMultipleEntities => this._canSelectEntity = canReadMultipleEntities));

    form: UntypedFormGroup;
    ticketPassbookList$: Observable<VMCreateTicketPassbok[]>;
    ticketPassbookSaving$: Observable<boolean>;
    entities$: Observable<Entity[]>;
    isAvetEntityDigitalSeasonTicket$: Observable<boolean>;
    isTicketsPassbookListInProgress$: Observable<boolean>;

    ngOnInit(): void {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
        this.isAvetEntityDigitalSeasonTicket$ = this.#auth.getLoggedUser$()
            .pipe(
                filter(user => !!user),
                map(user => {
                    if (user.entity.settings?.allow_avet_integration && user.entity.settings?.allow_digital_season_ticket) {
                        this.#dialogRef.removePanelClass(DialogSize.MEDIUM);
                        this.#dialogRef.addPanelClass(DialogSize.LARGE);
                    }
                    return user.entity.settings?.allow_avet_integration && user.entity.settings?.allow_digital_season_ticket;
                })
            );

        this.ticketPassbookSaving$ = this.#ticketsPassbookSrv.isTicketPassbookSaving$();

        this.initForm();

        this.isTicketsPassbookListInProgress$ = combineLatest([
            this.#ticketsPassbookSrv.isTicketsPassbookListInProgress$(), this.#entitiesService.entityList.inProgress$()
        ]).pipe(map(loadings => loadings.some(isLoading => isLoading)));

        combineLatest([this.canSelectEntity$, this.isAvetEntityDigitalSeasonTicket$])
            .pipe(take(1))
            .subscribe(([canSelectEntity, avetEntity]) => {
                if (canSelectEntity) {
                    this.#dialogRef.removePanelClass(DialogSize.MEDIUM);
                    this.#dialogRef.addPanelClass(DialogSize.LARGE);
                }
                if (!canSelectEntity && !avetEntity) {
                    this.#ticketsPassbookSrv.loadTicketPassbookList({ limit: 999, offset: 0, type: TicketPassbookType.order });
                }
            });

        this.entities$ = this.canSelectEntity$.pipe(
            switchMap(canSelectEntity => {
                if (canSelectEntity) {
                    return this.#entitiesService.entityList.getData$().pipe(filter(entities => !!entities),
                        tap(entities => {
                            if (entities.length === 0) {
                                this.form.get('entity').disable();
                                this.form.get('name').disable();
                                this.form.get('baseTemplate').disable();
                            } else {
                                entities.length === 1 && this.form.get('entity').setValue(entities[0].id);
                                this.form.get('entity').enable();
                                this.form.get('name').enable();
                                this.form.get('baseTemplate').enable();
                            }
                        }
                        ));
                }
                return of([]);
            }),
            shareReplay(1)
        );

        this.ticketPassbookList$ = combineLatest([
            this.#ticketsPassbookSrv.getTicketPassbookListData$(),
            this.entities$,
            this.canSelectEntity$
        ])
            .pipe(
                filter(([ticketPassbook]) => !!ticketPassbook),
                map(([ticketsPassbook, entities, canSelectEntity]) => {
                    ticketsPassbook.length === 1 && this.form.get('baseTemplate').setValue(ticketsPassbook[0]);

                    const vmTicketPassbook: VMCreateTicketPassbok[] = ticketsPassbook.map(tp => ({
                        entity_id: tp.entity_id,
                        code: tp.code,
                        name: canSelectEntity ? `${tp.name} - ${entities.find(entity => entity.id === tp.entity_id)?.name}` : tp.name,
                        type: tp.type
                    }));
                    return vmTicketPassbook;
                })
            );

    }

    ngOnDestroy(): void {
        this.#entitiesService.entityList.clear();
        this.#ticketsPassbookSrv.clearTicketPassbook();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    createTicketPassbook(): void {
        if (!this._canSelectEntity) {
            this.form.get('entity').setValue(this.form.value.baseTemplate.entity_id);
        }
        if (this.form.valid) {
            const ticketPassbook: PostTicketPassbok = {
                entity_id: this.form.value?.entity,
                code: new Date().getTime().toString(),
                name: this.form.value.name,
                template_code_to_copy: this.form.value.baseTemplate.code,
                origin_entity_id: this.form.value.baseTemplate.entity_id
            };
            this.#ticketsPassbookSrv.createTicketPassbook(ticketPassbook)
                .subscribe(id => this.close({ ticketPassbookId: id, entityId: ticketPassbook.entity_id }));
        } else {
            this.form.markAllAsTouched();
        }
    }

    close(data: { ticketPassbookId: number; entityId: number } = null): void {
        this.#dialogRef.close(data);
    }

    onPassbookTypeChanges(): void {
        const baseTemplateControl = this.form.get('baseTemplate') as UntypedFormControl;
        baseTemplateControl.setValue(null);

        if (this.form.get('type').value === TicketPassbookType.memberOrder) {
            this.#ticketsPassbookSrv.loadTicketPassbookList({ limit: 999, offset: 0, type: TicketPassbookType.memberOrder });
            //Only show avet entities that allow digital season ticket
            if (this._canSelectEntity) {
                this.#entitiesService.entityList.load({
                    limit: 999, sort: 'name:asc', fields: [EntitiesFilterFields.name], allow_avet_integration: true,
                    allow_digital_season_ticket: true
                });
            }
        } else {
            if (this._canSelectEntity) {
                this.#entitiesService.entityList.load({ limit: 999, sort: 'name:asc', fields: [EntitiesFilterFields.name] });
            }
            this.#ticketsPassbookSrv.loadTicketPassbookList({ limit: 999, offset: 0, type: TicketPassbookType.order });
        }
    }

    private initForm(): void {
        this.form = this.#fb.group({
            entity: [null, Validators.required],
            name: [null, Validators.required],
            baseTemplate: [null, Validators.required],
            type: null
        });
    }

}
