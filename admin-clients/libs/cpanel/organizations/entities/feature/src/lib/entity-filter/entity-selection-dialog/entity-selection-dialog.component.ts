import { EntitiesBaseService, GetEntitiesRequest } from '@admin-clients/shared/common/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, Inject, inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { filter, Subject, take } from 'rxjs';

@Component({
    selector: 'app-entity-selection-dialog',
    templateUrl: './entity-selection-dialog.component.html',
    styleUrls: ['./entity-selection-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class EntitySelectionDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();

    private readonly _dialogRef = inject(MatDialogRef<EntitySelectionDialogComponent, number>);
    private readonly _entitiesService = inject(EntitiesBaseService);
    private readonly _fb = inject(FormBuilder);

    entityForm = this._fb.group({
        entityId: [null as number, Validators.required]
    });

    readonly entities$ = this._entitiesService.entityList.getData$();
    readonly isLoading$ = this._entitiesService.entityList.inProgress$();

    constructor(
        @Inject(MAT_DIALOG_DATA) private _data: GetEntitiesRequest
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this._entitiesService.entityList.load(this._data);
        this._entitiesService.getEntity$()
            .pipe(take(1), filter(Boolean))
            .subscribe(entity => this.entityForm.controls.entityId.setValue(entity.id));
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    selectClick(): void {
        if (this.isValid()) {
            this.close(this.entityForm.value.entityId);
        }
    }

    close(entity: number = null): void {
        this._dialogRef.close(entity);
    }

    private isValid(): boolean {
        if (this.entityForm.valid) {
            return true;
        } else {
            this.entityForm.markAllAsTouched();
            return false;
        }
    }
}

