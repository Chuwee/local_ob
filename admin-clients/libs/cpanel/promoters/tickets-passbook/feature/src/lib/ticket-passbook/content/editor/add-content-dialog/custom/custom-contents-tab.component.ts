import { TicketsPassbookService } from '@admin-clients/cpanel-promoters-tickets-passbook-data-access';
import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
    selector: 'app-custom-contents-tab',
    templateUrl: './custom-contents-tab.component.html',
    styleUrls: ['./custom-contents-tab.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class TicketAddCustomContentsTabComponent implements OnInit {

    private get _emptyCustomContentFormGroup(): UntypedFormGroup {
        return this._fb.group({
            title: this._fb.group({
                type: [null, Validators.required],
                value: [{ value: null, disabled: true }, [Validators.required]]
            }),
            description: this._fb.group({
                type: [null, Validators.required],
                value: [{ value: null, disabled: true }, [Validators.required]]
            }),
            key: [null]
        });
    }

    get customContentsForm(): UntypedFormArray {
        return this.form.get('customContents') as UntypedFormArray;
    }

    @Input() form: UntypedFormGroup;
    @Input() maxFields: number;
    @Input() totalFieldsSelected: number;
    customTemplateLiterals$: Observable<string[]>;
    customTemplatePlaceholders$: Observable<string[]>;
    readonly placeholdersNotAllowedInCustomFields = new Set([
        '{event_additional_info_1}', '{event_additional_info_2}', '{event_additional_info_3}',
        '{session_additional_info_1}', '{session_additional_info_2}', '{session_additional_info_3}'
    ]);

    constructor(private _fb: UntypedFormBuilder, private _ticketsPassbookService: TicketsPassbookService) { }

    ngOnInit(): void {
        this.loadData();
    }

    addNewContent(): void {
        this.customContentsForm.push(this._emptyCustomContentFormGroup);
    }

    deleteSelectedContent(index: number): void {
        this.customContentsForm.controls.splice(index, 1);
        this.customContentsForm.patchValue(this.customContentsForm.getRawValue());
        this.customContentsForm.updateValueAndValidity();
    }

    shouldDisableAddButton(): boolean {
        return this.totalFieldsSelected >= this.maxFields;
    }

    private loadData(): void {
        this.customTemplateLiterals$ = this._ticketsPassbookService.getTicketPassbookAvailableCustomLiterals$();
        this.customTemplatePlaceholders$ = this._ticketsPassbookService.getTicketPassbookAvailableCustomPlaceholders$().pipe(
            map(placeholders => placeholders.filter(value => !this.placeholdersNotAllowedInCustomFields.has(value))));
    }
}
