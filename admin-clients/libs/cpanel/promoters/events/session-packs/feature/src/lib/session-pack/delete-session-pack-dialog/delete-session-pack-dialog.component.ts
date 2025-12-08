import { EventSessionsService, Session } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import {
    venueTemplatePriceTypesProviders
} from '@admin-clients/cpanel/venues/venue-templates/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { SeatStatus } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplateBlockingReason, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { filter, map, Observable } from 'rxjs';

@Component({
    selector: 'app-delete-session-pack-dialog',
    templateUrl: './delete-session-pack-dialog.component.html',
    styleUrls: ['./delete-session-pack-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [venueTemplatePriceTypesProviders],
    standalone: false
})
export class DeleteSessionPackDialogComponent implements OnInit {

    blockingReasons$: Observable<(SeatStatus.free | VenueTemplateBlockingReason)[]>;
    isLoading$: Observable<boolean>;
    form: FormGroup;

    constructor(
        private _dialogRef: MatDialogRef<DeleteSessionPackDialogComponent>,
        @Inject(MAT_DIALOG_DATA) private _data: DeleteSessionPackDialogData,
        private _venueTemplateSrv: VenueTemplatesService,
        private _sessionSrv: EventSessionsService,
        private _fb: FormBuilder
    ) {
        _dialogRef.addPanelClass(DialogSize.SMALL);
        _dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this._venueTemplateSrv.loadVenueTemplateBlockingReasons(this._data.sessionPack.venue_template.id);
        this.isLoading$ = this._venueTemplateSrv.isVenueTemplateBlockingReasonsLoading$();
        this.blockingReasons$ = this._venueTemplateSrv.getVenueTemplateBlockingReasons$()
            .pipe(
                filter(blockingReasons => !!blockingReasons),
                map(blockingReasons => [SeatStatus.free, ...blockingReasons])
            );
        this.form = this._fb.group({
            status: [null, [Validators.required]]
        });
    }

    cancel(): void {
        this._dialogRef.close();
    }

    delete(): void {
        this._sessionSrv.deleteSession(
            this._data.sessionPack.event.id,
            this._data.sessionPack.id,
            this.form.value.status === SeatStatus.free ? undefined : this.form.value.status.id
        ).subscribe(() => {
            this._dialogRef.close(true);
        });
    }
}

export interface DeleteSessionPackDialogData {
    sessionPack: Session;
}
