/* eslint-disable @angular-eslint/component-selector */
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { IonicModule, PickerColumn } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';
import moment from 'moment';
import { SelectedTime } from '../calendar-selector/models/calendar-selector.model';

@Component({
    selector: 'time-picker',
    imports: [CommonModule, IonicModule, TranslatePipe],
    templateUrl: './time-picker.component.html',
    styleUrls: ['./time-picker.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TimePickerComponent implements OnInit {

    @Input() readonly onlyTime?: boolean = false;
    @Input() readonly selectedTime: SelectedTime;
    @Output() readonly pickEmitter: EventEmitter<SelectedTime> = new EventEmitter<SelectedTime>();

    pickerColumns: PickerColumn[];

    ngOnInit(): void {
        this.generatePickerColumns();
    }

    save(): void {
        this.pickEmitter.emit(this.selectedTime);
    }

    onIonChange(event: CustomEvent, type: number): void {
        this.pickerColumns[type].selectedIndex = event.detail.value;
        switch (type) {
            case 0:
                this.selectedTime.timeFrom = moment(this.selectedTime.timeFrom)
                    .hours(this.pickerColumns[type].selectedIndex)
                    .valueOf();
                break;
            case 1:
                this.selectedTime.timeFrom = moment(this.selectedTime.timeFrom)
                    .minutes(this.pickerColumns[type].selectedIndex)
                    .valueOf();
                break;
            case 2:
                this.selectedTime.timeTo = moment(this.selectedTime.timeTo)
                    .hours(this.pickerColumns[type].selectedIndex)
                    .valueOf();
                break;
            case 3:
                this.selectedTime.timeTo = moment(this.selectedTime.timeTo)
                    .minutes(this.pickerColumns[type].selectedIndex)
                    .valueOf();
                break;
        }
    }

    private generatePickerColumns(): void {
        this.pickerColumns = [
            {
                name: 'hoursFrom',
                selectedIndex: this.selectedTime
                    ? moment(this.selectedTime.timeFrom).hours()
                    : 0,
                options: []
            },
            {
                name: 'minutesfrom',
                selectedIndex: this.selectedTime
                    ? moment(this.selectedTime.timeFrom).minutes()
                    : 0,
                options: []
            },
            {
                name: 'hoursTo',
                selectedIndex: this.selectedTime
                    ? moment(this.selectedTime.timeTo).hours()
                    : 0,
                options: []
            },
            {
                name: 'minutesTo',
                selectedIndex: this.selectedTime
                    ? moment(this.selectedTime.timeTo).minutes()
                    : 0,
                options: []
            }
        ];
        this.loadHourOptions();
        this.loadMinuteptions();
    }

    private loadHourOptions(): void {
        for (let i = 0; i < 24; i++) {
            const hourOption = {
                text: moment().hours(i).format('HH'),
                value: i
            };
            this.pickerColumns.forEach(column => {
                if (column.name.includes('hour')) {
                    column.options.push(hourOption);
                }
            });
        }
    }

    private loadMinuteptions(): void {
        for (let i = 0; i < 60; i++) {
            const minuteOption = {
                text: moment().minutes(i).format('mm'),
                value: i
            };
            this.pickerColumns.forEach(column => {
                if (column.name.includes('minute')) {
                    column.options.push(minuteOption);
                }
            });
        }
    }

    get stringDateFrom(): string {
        return this.onlyTime ? '' : moment(this.selectedTime.timeFrom).format('DD/MM/YYYY - ');
    }

    get stringTimeFrom(): string {
        return moment(this.selectedTime.timeFrom).format('HH:mm');
    }

    get stringDateTo(): string {
        return this.onlyTime ? '' : moment(this.selectedTime.timeTo).format('DD/MM/YYYY - ');
    }

    get stringTimeTo(): string {
        return moment(this.selectedTime.timeTo).format('HH:mm');
    }
}
