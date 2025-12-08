import {
    ProducersService, PutProducerDetails, ProducerFieldsRestrictions as restrictions
} from '@admin-clients/cpanel/promoters/producers/data-access';
import { CountriesService, RegionsService } from '@admin-clients/shared/common/data-access';
import { SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Subject } from 'rxjs';
import { filter, map, startWith, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-producer-contact',
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: './producer-contact.component.html',
    imports: [
        ReactiveFormsModule, FlexLayoutModule, MaterialModule, TranslatePipe, AsyncPipe, NgIf, NgFor, SelectSearchComponent
    ]
})
export class ProducerContactComponent implements OnInit, OnDestroy {
    private readonly _fb = inject(UntypedFormBuilder);
    private readonly _countriesService = inject(CountriesService);
    private readonly _regionsService = inject(RegionsService);
    private readonly _producerSrv = inject(ProducersService);

    private readonly _onDestroy = new Subject<void>();

    readonly contactFormGroup = this._fb
        .group({
            address: [null, Validators.maxLength(restrictions.producerAdressMaxLength)],
            city: [null, Validators.maxLength(restrictions.producerCityMaxLength)],
            postal_code: [null, [
                Validators.maxLength(restrictions.producerPostalCodeMaxLength),
                Validators.pattern(restrictions.producerPostalCodePattern)]
            ],
            country: this._fb.group({
                code: [{ value: null }]
            }),
            country_subdivision: this._fb.group({
                code: [{ value: null, disabled: true }]
            }),
            email: [null, [Validators.email, Validators.maxLength(restrictions.producerEmailMaxLength)]],
            phone: [null, [
                Validators.maxLength(restrictions.producerPhoneMaxLength),
                Validators.pattern(restrictions.producerPhonePattern)]
            ],
            name: [null, Validators.maxLength(restrictions.producerContactMaxLength)]
        });

    readonly countries$ = this._countriesService.getCountries$();
    readonly regions$ = this._regionsService.getRegions$();
    readonly filteredRegions$ = combineLatest([
        this.contactFormGroup.get('country.code').valueChanges.pipe(startWith(null as string)),
        this.regions$
    ]).pipe(
        filter(([_, regions]) => !!regions),
        map(([_, regions]) =>
            regions.filter(region => region.code.startsWith(this.contactFormGroup.get('country.code').value + '-'))
        )
    );

    readonly producerFieldsRestrictions = restrictions;

    @Input() set form(value: UntypedFormGroup) {
        if (value.contains('contact')) {
            return;
        }
        value.addControl('contact', this.contactFormGroup, { emitEvent: false });
    }

    readonly markForCheck = ((): () => void => {
        const cdr = inject(ChangeDetectorRef);
        return () => cdr.markForCheck();
    })();

    ngOnInit(): void {
        this._countriesService.loadCountries();
        this._regionsService.loadRegions();
        this.loadChangeHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        const parent = this.contactFormGroup.parent as UntypedFormGroup;
        parent.removeControl('contact');
    }

    getResult(result: PutProducerDetails): PutProducerDetails {
        if (!this.contactFormGroup.dirty) {
            return result;
        }

        return {
            ...result,
            contact: this.contactFormGroup.value
        };
    }

    private loadChangeHandler(): void {
        this._producerSrv.getProducer$()
            .pipe(filter(producer => !!producer), takeUntil(this._onDestroy))
            .subscribe(producer => {
                // Update
                this.contactFormGroup.reset(producer.contact, { emitEvent: false });
            });

        this.filteredRegions$
            .pipe(takeUntil(this._onDestroy))
            .subscribe(regions => {
                // Update
                if (!regions.length) {
                    this.contactFormGroup.get('country_subdivision.code').setValue(null, { emitEvent: false });
                }
                // Enable
                if (regions.length > 0) {
                    this.contactFormGroup.get('country_subdivision.code').enable({ emitEvent: false });
                } else {
                    this.contactFormGroup.get('country_subdivision.code').disable({ emitEvent: false });
                }
            });
    }
}
