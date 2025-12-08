import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output, inject
} from '@angular/core';
import { IonicModule, ModalController, PickerColumn } from '@ionic/angular';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import moment from 'moment';

@Component({
    selector: 'date-picker',
    imports: [CommonModule, IonicModule, TranslatePipe],
    templateUrl: './date-picker.component.html',
    styleUrls: ['./date-picker.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DatePickerComponent implements OnInit {
    private readonly _translateService = inject(TranslateService);
    private readonly _modalCtrl = inject(ModalController);

    pickerColumns: PickerColumn[];
    currentValueMonth: number;
    currentValueYear: number;

    @Output() readonly selectDate: EventEmitter<string> = new EventEmitter<string>();
    @Input() selectedTime: number;

    ngOnInit(): void {
        this.generatePickerColumns();
    }

    get stringDate(): string {
        return `${this._translateService.instant(
            'MONTHS.LONG-' +
            moment(this.selectedTime).format('MM').toUpperCase()
        )} ${moment(this.selectedTime).format('YYYY')}`;
    }

    onSave(): void {
        const jsonFormatted: string = moment(this.selectedTime).toJSON();
        this.selectDate.emit(jsonFormatted);
        this._modalCtrl.dismiss();
    }

    onIonChangeMonth(event: CustomEvent): void {
        this.currentValueMonth = event.detail.value;
        this.selectedTime = moment(this.selectedTime)
            .month(this.currentValueMonth)
            .valueOf();
    }

    onIonChangeYear(event: CustomEvent): void {
        this.currentValueYear = event.detail.value;
        this.selectedTime = moment(this.selectedTime)
            .year(this.currentValueYear)
            .valueOf();
    }

    private generatePickerColumns(): void {
        this.pickerColumns = [
            {
                name: 'months',
                selectedIndex: moment().month(),
                options: []
            },
            {
                name: 'years',
                selectedIndex: moment().year(),
                options: []
            }
        ];
        this.loadMonthOptions();
        this.loadYearOptions();

        this.currentValueMonth = moment(this.selectedTime).month();
        this.currentValueYear = moment(this.selectedTime).year();

    }

    private loadMonthOptions(): void {
        const selectedMonth = moment(this.selectedTime).month();
        let selectedMonthIndex = 0;
        for (let i = 0; i < 12; i++) {
            this._translateService
                .get(
                    'MONTHS.LONG-' +
                    moment().month(i).format('MM').toUpperCase()
                )
                .subscribe(translation => {
                    const monthOption = {
                        text: translation,
                        value: i
                    };
                    const monthColumn = this.pickerColumns.find(column => column.name === 'months');
                    monthColumn.options.push(monthOption);
                    if (selectedMonth === i) {
                        monthColumn.selectedIndex = selectedMonthIndex;
                    }

                });
            selectedMonthIndex++;
        }
    }

    private loadYearOptions(): void {
        const selectedYear = moment(this.selectedTime).year();
        let selectedYearIndex = 0;
        for (let i = 1950; i <= moment().year(); i++) {
            const yearOption = {
                text: moment().year(i).format('YYYY'),
                value: i
            };

            const yearColumn = this.pickerColumns.find(column => column.name === 'years');

            yearColumn.options.push(yearOption);

            if (selectedYear === i) {
                yearColumn.selectedIndex = selectedYearIndex;
            }

            selectedYearIndex++;
        }
    }
}
