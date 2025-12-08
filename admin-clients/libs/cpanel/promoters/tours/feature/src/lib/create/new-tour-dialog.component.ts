import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { PostTour, TourFieldsRestrictions, Tour, ToursService } from '@admin-clients/cpanel/promoters/tours/data-access';
import { EntitiesBaseService, EntitiesFilterFields } from '@admin-clients/shared/common/data-access';
import { DialogSize, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, of, Subject } from 'rxjs';
import { first, shareReplay, switchMap, withLatestFrom } from 'rxjs/operators';

@Component({
    selector: 'app-new-tour-dialog',
    templateUrl: './new-tour-dialog.component.html',
    styleUrls: ['./new-tour-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule,
        TranslatePipe,
        ReactiveFormsModule,
        FlexLayoutModule,
        SelectSearchComponent,
        FormControlErrorsComponent,
        AsyncPipe, NgIf, NgFor,
        EllipsifyDirective
    ]
})
export class NewTourDialogComponent implements OnInit, OnDestroy {
    private _onDestroy: Subject<void> = new Subject();
    readonly canSelectEntity$ = this._auth.canReadMultipleEntities$();

    newTourForm: UntypedFormGroup;
    entities$: Observable<Tour[]>;
    patchUserEntity$: Observable<void>;
    maxTourNameLength: number = TourFieldsRestrictions.tourNameLength;
    reqInProgress$: Observable<boolean>;

    constructor(
        private _dialogRef: MatDialogRef<NewTourDialogComponent>,
        private _auth: AuthenticationService,
        private _entitiesService: EntitiesBaseService,
        private _toursService: ToursService,
        private _fb: UntypedFormBuilder) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        // FormGroup creation
        this.reqInProgress$ = this._toursService.isTourLoading$();
        this.newTourForm = this._fb.group({
            entity: [null, Validators.required],
            name: [
                null, [Validators.required, Validators.maxLength(TourFieldsRestrictions.tourNameLength)]
            ]
        });

        this.entities$ = this._auth.getLoggedUser$()
            .pipe(
                first(user => user !== null),
                withLatestFrom(this.canSelectEntity$),
                switchMap(([user, canSelectEntity]) => {
                    if (canSelectEntity) {
                        this._entitiesService.entityList.load({
                            limit: 999,
                            sort: 'name:asc',
                            fields: [EntitiesFilterFields.name]
                        });
                        return this._entitiesService.entityList.getData$();
                    } else {
                        this.newTourForm.patchValue({ entity: user.entity });
                        return of([]);
                    }
                }),
                shareReplay(1)
            );
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    createTour(): void {
        if (this.isValid()) {
            const newTour: PostTour = {
                entity_id: this.newTourForm.value.entity.id,
                name: this.newTourForm.value.name
            };
            this._toursService.createTour(Object.assign({}, newTour))
                .subscribe(id => this.close(id));
        }
    }

    close(tourId: number = null): void {
        this._dialogRef.close(tourId);
    }

    private isValid(): boolean {
        if (this.newTourForm.valid) {
            return true;
        } else {
            this.newTourForm.markAllAsTouched();
            return false;
        }
    }

}
