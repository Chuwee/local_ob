import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { Terminal, TerminalsService } from '@admin-clients/cpanel-channels-terminals-data-access';
import {
    EntitiesBaseService, EntitiesFilterFields, EntityStatus, GetEntitiesRequest
} from '@admin-clients/shared/common/data-access';
import {
    CopyTextComponent,
    DialogSize, EphemeralMessageService, GoBackComponent, MessageDialogService,
    SelectServerSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { DateTimeFormats, IdName } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, Observable, shareReplay, switchMap, take, tap, throwError } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        ReactiveFormsModule,
        TranslatePipe,
        FormContainerComponent,
        FormControlErrorsComponent,
        FlexLayoutModule,
        SelectServerSearchComponent,
        DateTimePipe,
        GoBackComponent,
        CopyTextComponent
    ],
    selector: 'app-terminal-details',
    templateUrl: './terminal-details.component.html',
    styleUrls: ['./terminal-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TerminalDetailsComponent implements OnDestroy, WritingComponent {

    private readonly _terminalsSrv = inject(TerminalsService);
    private readonly _messageDialogSrv = inject(MessageDialogService);
    private readonly _entitiesSrv = inject(EntitiesBaseService);
    private readonly _fb = inject(FormBuilder);
    private readonly _ephemeralMessageSrv = inject(EphemeralMessageService);

    readonly shortDateTimeFormat = DateTimeFormats.shortDateTime;

    readonly form = this._fb.group({
        licenseEnabled: null as boolean,
        entity: [null as IdName, Validators.required],
        name: [null as string, Validators.required]
    });

    readonly terminal$ = this._terminalsSrv.terminal.get$()
        .pipe(
            tap(terminal => this.updateFormValue(terminal)),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly entities$ = this._entitiesSrv.entityList.getData$();
    readonly moreEntitiesAvailable$ = this._entitiesSrv.entityList.getMetadata$()
        .pipe(map(metadata => !metadata || metadata.total > metadata.offset + metadata.limit));

    readonly inProgress$ = this._terminalsSrv.terminal.inProgress$();

    ngOnDestroy(): void {
        this._terminalsSrv.terminal.clear();
    }

    save(): void {
        this._terminalsSrv.terminal.get$()
            .pipe(
                take(1),
                switchMap(terminal => this.save$().pipe(map(() => terminal)))
            )
            .subscribe(terminal => this._terminalsSrv.terminal.load(terminal.id));
    }

    save$(): Observable<unknown> {
        if (this.form.valid) {
            const formValue = this.form.value;
            return this._terminalsSrv.terminal.get$()
                .pipe(
                    take(1),
                    switchMap(terminal => this._terminalsSrv.terminal.save(
                        terminal.id,
                        {
                            license_enabled: formValue.licenseEnabled,
                            entity_id: formValue.entity.id,
                            name: formValue.name
                        }
                    )),
                    tap(() => this._ephemeralMessageSrv.showSaveSuccess())
                );
        } else {
            this.form.markAllAsTouched();
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this._terminalsSrv.terminal.get$().pipe(take(1))
            .subscribe(terminal => this.updateFormValue(terminal));
    }

    loadEntities({ q, nextPage }: { q?: string; nextPage?: boolean }): void {
        const request: GetEntitiesRequest = {
            type: 'CHANNEL_ENTITY',
            fields: [EntitiesFilterFields.name],
            status: [EntityStatus.active]
        };
        if (!nextPage) {
            this._entitiesSrv.entityList.load({ ...request, q });
        } else {
            this._entitiesSrv.entityList.loadMore({ ...request, q });
        }
    }

    regenerateLicense(): void {
        if (this.form.pristine) {
            this._terminalsSrv.terminal.get$()
                .pipe(
                    take(1),
                    switchMap(terminal =>
                        this._messageDialogSrv.showWarn({
                            size: DialogSize.SMALL,
                            title: 'TERMINALS.FORMS.INFOS.REGENERATE_LICENSE_WARNING',
                            message: 'TERMINALS.FORMS.INFOS.REGENERATE_LICENSE',
                            actionLabel: 'TERMINALS.ACTIONS.REGENERATE_CODE',
                            showCancelButton: true
                        })
                            .pipe(
                                filter(Boolean),
                                map(() => terminal)
                            )
                    ),
                    switchMap(terminal => this._terminalsSrv.terminal.regenerateLicense(terminal.id).pipe(map(() => terminal)))
                )
                .subscribe(terminal => {
                    this._ephemeralMessageSrv.showSuccess({ msgKey: 'TERMINALS.FORMS.FEEDBACK.REGENERATE_LICENSE_SUCCESS' });
                    this._terminalsSrv.terminal.load(terminal.id);
                });
        }
    }

    private updateFormValue(terminal: Terminal): void {
        this.form.reset({
            entity: terminal.entity,
            name: terminal?.name,
            licenseEnabled: terminal?.license.enabled
        });
    }
}
