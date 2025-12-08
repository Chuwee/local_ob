import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    SeasonTicketGenerationStatus,
    PutSeasonTicket,
    SeasonTicket,
    SeasonTicketsService,
    SeasonTicketStatus,
    PutSeasonTicketStatus,
    SeasonTicketFieldsRestrictions
} from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        TranslatePipe,
        MaterialModule,
        ReactiveFormsModule,
        FlexLayoutModule,
        FormControlErrorsComponent
    ],
    selector: 'app-season-ticket-principal-info-st-data',
    templateUrl: './season-ticket-principal-info-st-data.component.html'
})
export class SeasonTicketPrincipalInfoStDataComponent implements OnInit {
    private readonly _seasonTicketSrv = inject(SeasonTicketsService);
    private readonly _fb = inject(FormBuilder);
    private readonly _destroyRef = inject(DestroyRef);
    private _seasonTicket: SeasonTicket;

    readonly seasonTicketStatus = SeasonTicketStatus;
    readonly generationStatus = SeasonTicketGenerationStatus;
    readonly dataForm = this._fb.group({
        name: [null as string, [
            Validators.required,
            Validators.maxLength(SeasonTicketFieldsRestrictions.seasonTicketNameLength),
            Validators.pattern(SeasonTicketFieldsRestrictions.seasonTicketNamePattern)
        ]],
        reference: [null as string, [
            Validators.maxLength(
                SeasonTicketFieldsRestrictions.seasonTicketReferenceLength
            )
        ]]
    });

    @Input() putSeasonTicketCtrl: FormControl<PutSeasonTicket>;
    @Input() putSeasonTicketStatusCtrl: FormControl<PutSeasonTicketStatus>;
    @Input() seasonTicketStatusCtrl: FormControl<SeasonTicketStatus>;
    @Input() form: FormGroup;
    @Input() set seasonTicket(seasonTicket: SeasonTicket) {
        this._seasonTicket = seasonTicket;
        this.dataForm.reset({
            name: seasonTicket?.name,
            reference: seasonTicket?.reference
        }, { emitEvent: false });
    }

    get seasonTicket(): SeasonTicket {
        return this._seasonTicket;
    }

    readonly isGenerationStatusInProgress$ = this._seasonTicketSrv.seasonTicketStatus.isGenerationStatusInProgress$();
    readonly isGenerationStatusError$ = this._seasonTicketSrv.seasonTicketStatus.isGenerationStatusError$();

    ngOnInit(): void {
        this.form.addControl('data', this.dataForm, { emitEvent: false });

        this._seasonTicketSrv.seasonTicketStatus.isGenerationStatusReady$()
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(isGenStatusReady => {
                if (isGenStatusReady) {
                    this.seasonTicketStatusCtrl.enable();
                } else {
                    this.seasonTicketStatusCtrl.disable();
                }
            });

        this._seasonTicketSrv.seasonTicketStatus.get$()
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(stStatus => {
                this.seasonTicketStatusCtrl.setValue(stStatus.status, { emitEvent: false });
            });

        this.putSeasonTicketCtrl.valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(putSeasonTicket => {
                if (this.form.invalid) return;

                const { name, reference } = this.dataForm.controls;
                if (name.dirty) {
                    putSeasonTicket.name = name.value;
                }
                if (reference.dirty) {
                    putSeasonTicket.reference = reference.value;
                }
                this.putSeasonTicketCtrl.setValue(putSeasonTicket, { emitEvent: false });
            });

        this.putSeasonTicketStatusCtrl.valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(putSeasonTicketStatus => {
                if (this.form.invalid) return;

                if (this.seasonTicketStatusCtrl.dirty) {
                    putSeasonTicketStatus.status = this.seasonTicketStatusCtrl.value;
                }

                this.putSeasonTicketStatusCtrl.setValue(putSeasonTicketStatus, { emitEvent: false });
            });
    }
}
