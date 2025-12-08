import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit, Output, QueryList, ViewChildren } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { combineLatest, Observable, Subject } from 'rxjs';
import { map, distinctUntilChanged, shareReplay, take, takeUntil } from 'rxjs/operators';
import { NoteContentFieldsRestrictions } from '../models/note-content-fields-restrictions.enum';
import { Note } from '../models/note.model';

@Component({
    selector: 'app-note-details',
    templateUrl: './note-details.component.html',
    styleUrls: ['./note-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class NoteDetailsComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    textRestrictions = NoteContentFieldsRestrictions;
    isSaveCancelEnabled$: Observable<boolean>;
    readonly dateTimeFormats = DateTimeFormats;

    @Input() form: UntypedFormGroup;
    @Input() totalNotes: number;
    @Input() userCanWrite = true;
    @Input() isLoading$: Observable<boolean>;
    @Input() note$: Observable<Note>;
    @Input() showDateTime = false;
    @Input() showNoteUser = false;
    @Output() addNote = new EventEmitter<void>();
    @Output() saveNote = new EventEmitter<UntypedFormGroup>();
    @Output() cancelNote = new EventEmitter<string>();

    constructor(private _fb: UntypedFormBuilder) { }

    ngOnInit(): void {
        this.initForm();
        this.model();
        this.refreshFormDataHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    cancel(): void {
        this.note$
            .pipe(take(1))
            .subscribe(note => {
                this.cancelNote.emit(note.id);
                this.form.markAsPristine();
                this.form.markAsUntouched();
            });
    }

    save(): void {
        if (!this.form.valid) {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return;
        }
        this.saveNote.emit(this.form);
        this.form.markAsPristine();
        this.form.markAsUntouched();
    }

    private initForm(): void {
        this.form.setControl('title', this._fb.control(
            { value: null, disabled: !this.userCanWrite },
            [Validators.required, Validators.maxLength(NoteContentFieldsRestrictions.titleMaxLength)]
        ));
        this.form.setControl('description', this._fb.control(
            { value: null, disabled: !this.userCanWrite },
            [Validators.required, Validators.maxLength(NoteContentFieldsRestrictions.descriptionMaxLength)]
        ));
        this.form.markAsPristine();
    }

    private model(): void {
        this.isSaveCancelEnabled$ = combineLatest([
            this.isLoading$,
            this.form.valueChanges
        ]).pipe(
            map(([isLoading]) => !isLoading && this.form?.dirty),
            distinctUntilChanged(),
            shareReplay(1)
        );
    }

    private updateNoteContentForm(note: Note): void {
        this.form.patchValue({
            title: note.title,
            description: note.description
        });
        this.form.markAsPristine();
    }

    private refreshFormDataHandler(): void {
        this.note$
            .pipe(takeUntil(this._onDestroy))
            .subscribe(note => this.updateNoteContentForm(note));
    }

}
