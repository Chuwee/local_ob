import { IdName } from '@admin-clients/shared/data-access/models';
import { maxDecimalLength } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, effect, inject, input, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormArray, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatDivider } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { TranslateModule } from '@ngx-translate/core';

type RateTicketGroupShape = {
    rate: FormControl<number | null>;
    limit: FormControl<number | null>;
};

type RateTicketGroup = FormGroup<RateTicketGroupShape>;
type RateTicketsGroupsForm = {
    enabled: FormControl<boolean>;
    rate_ticket_groups: FormArray<RateTicketGroup>;
};

@Component({
    selector: 'app-promotion-by-rate-tickets-groups',
    templateUrl: './promotion-by-rate-tickets-groups.component.html',
    styleUrls: ['./promotion-by-rate-tickets-groups.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: true,
    imports: [TranslateModule, MatCheckbox, ReactiveFormsModule, MatDivider, MatFormFieldModule, MatSelectModule,
        MatIconButton, MatIcon, MatInput
    ]
})
export class PromotionByRateTicketsGroupsComponent implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #destroyRef = inject(DestroyRef);

    readonly $rates = input.required<IdName[]>({ alias: 'rates' });
    readonly $promotionRateTicketsGroups = input.required<{ rate: number; limit: number }[]>({ alias: 'promotionRateTicketsGroups' });
    readonly $form = input.required<FormGroup<RateTicketsGroupsForm>>({ alias: 'form' });

    readonly $selectedRates = signal<number[]>([]);
    readonly $availableRates = computed(() => this.$rates()?.map(rate => ({
        id: rate.id,
        name: rate.name,
        disabled: this.$selectedRates()?.some(rateId => rateId === rate.id)
    })) ?? []);

    readonly $allowAddRateTicketsGroup = computed(() => !this.$availableRates()?.every(pt => pt.disabled));

    get rateTicketsGroups(): FormArray { return this.$form()?.get('rate_ticket_groups') as FormArray; };

    constructor() {
        effect(() => {
            if (this.$promotionRateTicketsGroups()) {
                this.rateTicketsGroups.clear();
                this.$promotionRateTicketsGroups().forEach(group => {
                    this.addRateTicketsGroup(group.rate, group.limit);
                });
            } else {
                this.rateTicketsGroups.clear();
            }
        });
    }

    ngOnInit(): void {
        this.#handleRateTicketsGroupEnabledChanges();
        this.#handleRateTicketsGroupsChanges();
    }

    deleteRateTicketsGroup(index: number): void {
        if (this.rateTicketsGroups.length < 2) this.rateTicketsGroups.at(index).reset();
        else this.rateTicketsGroups.removeAt(index);
        this.rateTicketsGroups.markAsDirty();
    }

    addRateTicketsGroup(rate: number = null, limit: number = null): void {
        const rateTicketsGroup = this.#fb.group({
            rate: [rate, [Validators.required]],
            limit: [limit, [Validators.required, Validators.min(1), maxDecimalLength(0)]]
        });

        this.rateTicketsGroups.push(rateTicketsGroup);
    }

    #handleRateTicketsGroupsChanges(): void {
        this.$form()?.controls.rate_ticket_groups.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(rateTicketsGroups => {
                this.$selectedRates.set(rateTicketsGroups.map(group => group.rate).filter(rate => rate !== null));
            });
    }

    #handleRateTicketsGroupEnabledChanges(): void {
        this.$form()?.controls.enabled.valueChanges.pipe(
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe((isEnabled: boolean) => {
            if (isEnabled) {
                this.$form()?.controls.rate_ticket_groups.enable();
                if (!(this.$form()?.controls.rate_ticket_groups as FormArray).length) {
                    this.addRateTicketsGroup();
                }
            } else { this.$form()?.controls.rate_ticket_groups.disable(); }
        });
    }

}