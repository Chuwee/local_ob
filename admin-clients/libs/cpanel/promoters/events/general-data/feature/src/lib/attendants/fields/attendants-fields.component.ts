import { AttendantsService } from '@admin-clients/cpanel/platform/data-access';
import { EventAttendantField, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CdkDragDrop, DragDropModule, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { AsyncPipe, NgClass, NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, forwardRef, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { UntypedFormGroup, ControlValueAccessor, NG_VALUE_ACCESSOR, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, map, shareReplay, takeUntil } from 'rxjs/operators';

@Component({
    imports: [
        NgIf, NgFor, NgClass, AsyncPipe,
        FlexLayoutModule,
        ReactiveFormsModule,
        MaterialModule,
        TranslatePipe,
        DragDropModule
    ],
    selector: 'app-event-attendants-fields',
    templateUrl: './attendants-fields.component.html',
    styleUrls: ['./attendants-fields.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            multi: true,
            useExisting: forwardRef(() => EventAttendantsFieldsComponent)
        }
    ]
})
export class EventAttendantsFieldsComponent implements OnInit, OnDestroy, ControlValueAccessor {
    private _onDestroy = new Subject<void>();
    form: UntypedFormGroup;
    fields$: Observable<EventAttendantField[]>;
    selectedFields$: Observable<EventAttendantField[]>;
    isLoadingOrSaving$: Observable<boolean>;
    value: EventAttendantField[];
    isDisabled: boolean;

    constructor(
        private _ref: ChangeDetectorRef,
        private _eventsService: EventsService,
        private _attendantsService: AttendantsService
    ) { }

    onChange = (_: EventAttendantField[]): void => null;
    onTouch = (): void => null;

    ngOnInit(): void {
        this._attendantsService.attendantFields.load();

        this.fields$ = combineLatest([
            this._eventsService.eventAttendantFields.getData$(),
            this._attendantsService.attendantFields.getData$()
        ]).pipe(
            filter(([selected, fields]) => !!selected && !!fields),
            map(([selected, fields]) => fields.filter(field => !selected.find(elem => field.id === elem.field_id))),
            map(fields => fields.map(field => ({
                field_id: field.id,
                sid: field.sid,
                min_length: 0,
                max_length: field.max_length,
                mandatory: true,
                order: null
            }))),
            takeUntil(this._onDestroy),
            shareReplay(1)
        );

        this.isLoadingOrSaving$ = booleanOrMerge([
            this._attendantsService.attendantFields.loading$()
        ]);
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    writeValue(value: (EventAttendantField[])): void {
        this.value = value || [];
        this.value.forEach((field, index) => {
            field.order = index;
        });
        this._ref.markForCheck();
    }

    registerOnChange(fn: () => void): void {
        this.onChange = fn;
    }

    registerOnTouched(fn: () => void): void {
        this.onTouch = fn;
    }

    setDisabledState(isDisabled: boolean): void {
        this.isDisabled = isDisabled;
        this._ref.markForCheck();
    }

    dropItem(event: CdkDragDrop<any[]>): void {
        const { container, previousContainer, currentIndex, previousIndex } = event;

        if (this.isDisabled) return;

        if (previousContainer === container) {
            moveItemInArray((container.data as []), previousIndex, currentIndex);
        } else {
            transferArrayItem(previousContainer.data, container.data, previousIndex, currentIndex);
        }

        let value = null;
        if (container.id === 'selected-list') {
            value = container.data;
            previousContainer !== container ? value[currentIndex].mandatory = true : null;
        }
        if (container.id === 'available-list' && previousContainer !== container) {
            value = previousContainer.data;
        }
        if (value) {
            this.writeValue(value);
            this.onChange(value);
            this.onTouch();
        }
    }

    toggleMandatory(field: EventAttendantField): void {
        if (this.isDisabled) return;
        field.mandatory = !field.mandatory;
        this.writeValue(this.value);
        this.onChange(this.value);
        this.onTouch();
    }

}
