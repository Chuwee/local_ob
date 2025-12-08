import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { Observable, Subject, throwError } from 'rxjs';
import { tap, filter, takeUntil } from 'rxjs/operators';
import { PutVoucherGroup, VoucherGroupFieldRestrictions, VouchersService } from '@admin-clients/cpanel-vouchers-data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';

@Component({
    selector: 'app-group-principal-info',
    templateUrl: './group-principal-info.component.html',
    styleUrls: ['./group-principal-info.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class GroupPrincipalInfoComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _groupId: number;
    private _vgRestrictions = VoucherGroupFieldRestrictions;

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanels: QueryList<MatExpansionPanel>;

    reqInProgress$: Observable<boolean>;
    form: UntypedFormGroup;

    constructor(
        private _voucherSrv: VouchersService,
        private _fb: UntypedFormBuilder,
        private _ephemeralSrv: EphemeralMessageService
    ) { }

    ngOnInit(): void {
        this.initForm();
        this.model();
    }

    ngOnDestroy(): void {
        this._onDestroy.complete();
        this._onDestroy.next(null);
    }

    cancel(): void {
        this._voucherSrv.loadVoucherGroup(this._groupId);
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const { name, description } = this.form.value;
            const group: PutVoucherGroup = { name, description };
            return this._voucherSrv.saveVoucherGroup(this._groupId, group)
                .pipe(
                    tap(() => {
                        this._ephemeralSrv.showSaveSuccess();
                        this._voucherSrv.loadVoucherGroup(this._groupId);
                    })
                );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanels);
            return throwError(() => 'invalid form');
        }
    }

    private initForm(): void {
        this.form = this._fb.group({
            name: [null, [
                Validators.required,
                Validators.maxLength(this._vgRestrictions.maxNameLength),
                Validators.minLength(this._vgRestrictions.minNameLength)]],
            description: [null, Validators.maxLength(this._vgRestrictions.maxDescriptionLength)]
        });
    }

    private model(): void {
        this.reqInProgress$ = this._voucherSrv.isVoucherGroupSaving$();

        this._voucherSrv.getVoucherGroup$()
            .pipe(
                filter(group => !!group),
                takeUntil(this._onDestroy)
            ).subscribe(group => {
                this._groupId = group.id;
                this.form.patchValue({ name: group.name, description: group.description });
                this.form.markAsPristine();
            });
    }

}
