import { SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { NotNumberedZone, QuotaCounter } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, Input, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { VenueTemplateLabel } from '../../../models/label-group/venue-template-label-group-list.model';
import { VenueTemplateLabelGroupType } from '../../../models/label-group/venue-template-label-group-type.enum';
import { VenueTemplateZoneDialogData } from '../../../models/venue-template-zone-dialog-data.model';
import { ZoneActionType } from '../../../models/venue-tpl-tree-dialog-type.enum';
import { StandardVenueTemplateBaseService } from '../../../services/standard-venue-template-base.service';
import { FormControlNames } from '../form-names.enum';
import { VmQuotaCounter } from '../vm-quota-counter.model';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ReactiveFormsModule,
        SelectSearchComponent,
        SharedUtilityDirectivesModule
    ],
    selector: 'app-quota',
    templateUrl: './quota.component.html',
    styleUrls: ['./quota.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class QuotaComponent implements OnInit, OnDestroy {
    private readonly _quotaCountersBS = new BehaviorSubject<VmQuotaCounter[]>(null);
    quotaCounters$: Observable<VmQuotaCounter[]>;
    absCapacityDelta: number;
    capacityDeltaLiteral: string;
    targetCapacityDeltaLiteral: string;
    quotaCountersLiteral: string;
    @Input() form: UntypedFormGroup;
    @Input() nnzFormGroup: UntypedFormGroup;
    @Input() quotaFormGroup: UntypedFormGroup;
    @Input() formControlNames: typeof FormControlNames;

    get nameControl(): UntypedFormControl {
        return this.nnzFormGroup.get(this.formControlNames.name) as UntypedFormControl;
    }

    get capacityControl(): UntypedFormControl {
        return this.nnzFormGroup.get(this.formControlNames.capacity) as UntypedFormControl;
    }

    get quotaControl(): UntypedFormControl {
        return this.quotaFormGroup.get(this.formControlNames.quota) as UntypedFormControl;
    }

    get capacityDeltaControl(): UntypedFormControl {
        return this.quotaFormGroup.get(this.formControlNames.capacityDelta) as UntypedFormControl;
    }

    constructor(
        private _standardVenueTemplateSrv: StandardVenueTemplateBaseService,
        private _translateSrv: TranslateService,
        @Inject(MAT_DIALOG_DATA) private _data: VenueTemplateZoneDialogData
    ) { }

    ngOnInit(): void {
        const zone = this.getZone();
        const capacityDelta = this.getCapacityDelta(zone);
        this.setCapacityDelta(capacityDelta);
        this.setQuotaCounters(capacityDelta, zone);
    }

    ngOnDestroy(): void {
        this.quotaControl.reset();
    }

    private getZone(): NotNumberedZone {
        return this._data.action === ZoneActionType.increaseEditCapacity ?
            (this._data.zone.record?.initialState || this._data.zone) :
            this._data.zone;
    }

    private getCapacityDelta(zone: NotNumberedZone): number {
        const initialCapacity = zone.capacity;
        return this.capacityControl.value - initialCapacity;
    }

    private setCapacityDelta(capacityDelta: number): void {
        this.absCapacityDelta = Math.abs(capacityDelta);
        const isCapacityIncreased = capacityDelta > 0;
        this.capacityDeltaControl.setValue(capacityDelta);
        this.capacityDeltaLiteral = isCapacityIncreased ?
            this._translateSrv.instant('VENUE_TPL_MGR.FORMS.LABELS.ZONE_CAPACITY_INCREASED_AMOUNT') :
            this._translateSrv.instant('VENUE_TPL_MGR.FORMS.LABELS.ZONE_CAPACITY_DECREASED_AMOUNT');
        this.targetCapacityDeltaLiteral = isCapacityIncreased ?
            this._translateSrv.instant('VENUE_TPL_MGR.FORMS.INFOS.ZONE_CAPACITY_INCREASED_TARGET', { zoneName: this.nameControl.value }) :
            this._translateSrv.instant('VENUE_TPL_MGR.FORMS.INFOS.ZONE_CAPACITY_DECREASED_TARGET', { zoneName: this.nameControl.value });
    }

    private setQuotaCounters(capacityDelta: number, zone: NotNumberedZone): void {
        this.quotaCounters$ = this._quotaCountersBS.asObservable();
        const isCapacityIncreased = capacityDelta > 0;
        this.quotaCountersLiteral = isCapacityIncreased ?
            this._translateSrv.instant('VENUE_TPL_MGR.FORMS.LABELS.QUOTA_COUNTER_INCREASED') :
            this._translateSrv.instant('VENUE_TPL_MGR.FORMS.LABELS.QUOTA_COUNTER_DECREASED');
        const quotaLabels = this.getQuotaLabels();
        const quotaCounters = this.getQuotaCounters(quotaLabels, isCapacityIncreased, capacityDelta, zone);
        this._quotaCountersBS.next(quotaCounters);
        this.setInitialQuotaCounter(quotaCounters);
    }

    private getQuotaLabels(): VenueTemplateLabel[] {
        let quotaLabels: VenueTemplateLabel[];
        this._standardVenueTemplateSrv.getLabelGroups$()
            .pipe(take(1))
            .subscribe(labelGroups => quotaLabels = labelGroups.find(lg => lg.id === VenueTemplateLabelGroupType.quota).labels);
        return quotaLabels;
    }

    private getQuotaCounters(
        quotaLabels: VenueTemplateLabel[],
        isCapacityIncreased: boolean,
        capacityDelta: number,
        zone: NotNumberedZone
    ): VmQuotaCounter[] {
        let quotaCounters: VmQuotaCounter[];
        if (isCapacityIncreased) {
            quotaCounters = quotaLabels.map(quotaLabel => this.getQuotaCounterForIncreasedCapacity(quotaLabel, zone.quotaCounters));
        } else {
            quotaCounters = quotaLabels.map(quotaLabels =>
                this.getQuotaCounterForDecreasedCapacity(quotaLabels, zone.quotaCounters, capacityDelta));
        }
        return quotaCounters;
    }

    private getQuotaCounterForIncreasedCapacity(
        quotaLabel: VenueTemplateLabel, quotaCounters: QuotaCounter[]
    ): { label: VenueTemplateLabel; count: number } {
        const quotaCounter = quotaCounters?.find(quotaCounter => quotaCounter.quota.toString() === quotaLabel.id);
        return {
            label: quotaLabel,
            count: quotaCounter ? quotaCounter.count : 0
        };
    }

    private getQuotaCounterForDecreasedCapacity(
        quotaLabel: VenueTemplateLabel, quotaCounters: QuotaCounter[], capacityDelta
    ): { label: VenueTemplateLabel; count: number; disabled: boolean } {
        const quotaCounter = quotaCounters.find(quotaCounter => quotaCounter.quota.toString() === quotaLabel.id);
        return {
            label: quotaLabel,
            count: quotaCounter ? quotaCounter.count : 0,
            disabled: quotaCounter ? (capacityDelta + quotaCounter.count < 0) : true
        };
    }

    private setInitialQuotaCounter(quotaCounters: VmQuotaCounter[]): void {
        if (quotaCounters.length === 1 && !quotaCounters[0].disabled) {
            this.quotaControl.setValue(quotaCounters[0]);
        } else if (this._data.action === ZoneActionType.increaseEditCapacity && this._data.zone.record?.initialState) {
            this.quotaControl.setValue(this.getInitialQuotaCounter(quotaCounters));
        }
    }

    private getInitialQuotaCounter(quotaCounters: VmQuotaCounter[]): VmQuotaCounter {
        const targetQuotaCountersMap = new Map(quotaCounters.map(quotaCounter => [quotaCounter.label.id, quotaCounter]));
        const zoneQuotaCountersMap = new Map(this._data.zone.quotaCounters.map(quotaCounter =>
            [quotaCounter.quota.toString(), quotaCounter]));
        let foundQuotaCounterId: string;
        targetQuotaCountersMap.forEach((initialQuotaCounter, id) => {
            if (zoneQuotaCountersMap.has(id) && zoneQuotaCountersMap.get(id).count !== initialQuotaCounter.count) {
                foundQuotaCounterId = id;
            }
        });
        return targetQuotaCountersMap.get(foundQuotaCounterId);
    }
}
