import { DialogSize, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { IdName, SeasonTicketLinkNNZResponse, SeasonTicketNotLinkableNNZReason } from '@admin-clients/shared/data-access/models';
import {
    NotNumberedZone, SeatLinkable, Sector, StdVenueTplService, VENUE_MAP_SERVICE, VenueMapService
} from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit, Optional } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AbstractControl, ReactiveFormsModule, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { first, map, take, takeUntil } from 'rxjs/operators';
import { SEASON_TICKET_SERVICE, SeasonTicketMgrService } from '../../services/season-ticket-mgr-service.token';
import { StandardVenueTemplateBaseService } from '../../services/standard-venue-template-base.service';
import { SeasonTicketCapacityDialogComponent } from '../season-ticket-capacity-dialog/season-ticket-capacity-dialog.component';
import { SeasonTicketLinkableDialogData } from './season-ticket-linkable-dialog.data';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ReactiveFormsModule
    ],
    selector: 'app-season-ticket-linkable-dialog',
    templateUrl: './season-ticket-linkable-dialog.component.html',
    styleUrls: ['./season-ticket-linkable-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SeasonTicketLinkableDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _seasonTicket: IdName;
    private _nnzBS = new BehaviorSubject<NotNumberedZone>(null);
    private _nnz: NotNumberedZone;
    private _maxValue: number;
    isLinkableAction: boolean;
    form: UntypedFormGroup;
    nnz$: Observable<NotNumberedZone>;
    sector$: Observable<Sector>;
    isLoading$: Observable<boolean>;

    get countControl(): UntypedFormControl {
        return this.form.get('count') as UntypedFormControl;
    }

    get allTicketsControl(): UntypedFormControl {
        return this.form.get('allTickets') as UntypedFormControl;
    }

    constructor(
        private _dialogRef: MatDialogRef<SeasonTicketLinkableDialogComponent>,
        @Inject(MAT_DIALOG_DATA) private _data: SeasonTicketLinkableDialogData,
        private _fb: UntypedFormBuilder,
        private _stdVenueTplSrv: StdVenueTplService,
        @Inject(VENUE_MAP_SERVICE) @Optional() private _venueMapSrv: VenueMapService,
        private _standardVenueTemplateSrv: StandardVenueTemplateBaseService,
        @Inject(SEASON_TICKET_SERVICE) private _seasonTicketMgrService: SeasonTicketMgrService,
        private _matDialog: MatDialog
    ) {
        this._venueMapSrv ??= this._stdVenueTplSrv;
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    ngOnInit(): void {
        this.initForm();
        this.setSeasonTicket();
        this.setLoading();
        this.setIsLinkableAction();
        this.setNnz();
        this.setSector();
        this.setMaxValue();
        this.allTicketsChangeHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    cancel(): void {
        this._dialogRef.close();
    }

    save(): void {
        if (!this.form.valid) {
            this.form.markAllAsTouched();
        } else {
            const count = this.allTicketsControl.value ? this._maxValue : this.countControl.value;
            if (this.isLinkableAction) {
                this._seasonTicketMgrService.putNnzLinkable(this._seasonTicket.id, this._nnz.id, count)
                    .subscribe(seasonTicketLinkNNZResponse => {
                        this._dialogRef.close(true);
                        this.showNotLinkedReasons(seasonTicketLinkNNZResponse);
                    });
            } else {
                this._seasonTicketMgrService.putNnzNotLinkable(this._seasonTicket.id, this._nnz.id, count)
                    .subscribe(() => this._dialogRef.close(true));
            }
        }
    }

    private initForm(): void {
        this.form = this._fb.group({
            allTickets: [true, Validators.required],
            count: [{ value: 0, disabled: true }, [
                Validators.required,
                Validators.min(1),
                (control: AbstractControl) =>
                    Validators.max(this._maxValue || 1)(control)
            ]]
        });
    }

    private setLoading(): void {
        this.isLoading$ = this._venueMapSrv.isVenueMapSaving$();
    }

    private setSeasonTicket(): void {
        this._seasonTicketMgrService.getSeasonTicket$()
            .pipe(take(1))
            .subscribe(seasonTicket => this._seasonTicket = seasonTicket);
    }

    private setNnz(): void {
        this.nnz$ = this._nnzBS.asObservable();
        this._standardVenueTemplateSrv.getVenueItems$()
            .pipe(take(1))
            .subscribe(venueItems => {
                const nnzId = Array.from(this._data.items.nnzs)[0];
                this._nnz = venueItems.nnzs.get(nnzId);
                this._nnzBS.next(this._nnz);
            });
    }

    private setSector(): void {
        this.sector$ = this._venueMapSrv.getVenueMap$()
            .pipe(
                first(venueMap => !!venueMap),
                map(venueMap => venueMap.sectors.find(sector => sector.id === this._nnz.sector))
            );
    }

    private setMaxValue(): void {
        if (this.isLinkableAction) {
            this._maxValue = this._nnz.capacity - this._nnz.linkableSeats;
        } else {
            this._maxValue = this._nnz.linkableSeats;
        }
    }

    private setIsLinkableAction(): void {
        this.isLinkableAction = this._data.label.id === SeatLinkable.linkable;
    }

    private allTicketsChangeHandler(): void {
        this.allTicketsControl.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(allTickets => {
                if (allTickets) {
                    this.countControl.disable();
                } else {
                    this.countControl.enable();
                }
            });
    }

    private showNotLinkedReasons(seasonTicketLinkNNZResponse: SeasonTicketLinkNNZResponse): void {
        if (seasonTicketLinkNNZResponse.not_linked_seats) {
            this._matDialog.open(
                SeasonTicketCapacityDialogComponent,
                new ObMatDialogConfig({
                    notLinkedReasons: [{
                        messageKey: SeasonTicketNotLinkableNNZReason.someSeatsAreNotFree,
                        data: seasonTicketLinkNNZResponse.not_linked_seats
                    }]
                }))
                .beforeClosed()
                .subscribe();
        }
    }
}
