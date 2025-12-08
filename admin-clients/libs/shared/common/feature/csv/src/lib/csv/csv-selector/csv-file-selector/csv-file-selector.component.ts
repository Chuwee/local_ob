import { MessageDialogService, DialogSize, FileStatusState } from '@admin-clients/shared/common/ui/components';
import {
    ChangeDetectionStrategy, ChangeDetectorRef, Component, HostBinding, Inject, OnDestroy, OnInit, Self
} from '@angular/core';
import { ControlValueAccessor, NgControl } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, Observable, Subject } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';
import { CSV_FILE_PROCESSOR, CsvFileProcessor } from '../../models/csv-file-processor.token';
import { CsvFile } from '../../models/csv-file.model';
import { CsvErrorEnum } from '../../validators/csv-validators';

type CsvFileSelectorState = 'csvError' | 'loading' | 'success' | 'empty';

@Component({
    selector: 'app-csv-file-selector',
    templateUrl: './csv-file-selector.component.html',
    styleUrls: ['./csv-file-selector.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class CsvFileSelectorComponent implements OnInit, OnDestroy, ControlValueAccessor {
    private _onDestroy = new Subject<void>();
    private _onTouch: () => void;
    private _onChange: (value: CsvFile) => void;
    private _csvFile: CsvFile;
    private _file: File;
    private _fileBS = new BehaviorSubject<File>(null);
    private _processedFile: unknown;
    private _processedFileBS = new BehaviorSubject<unknown>(null);
    private _isLoadingBS = new BehaviorSubject<boolean>(false);
    private _isProcessorError: boolean;
    private _isProcessorErrorBS = new BehaviorSubject<boolean>(false);
    private _isFileTypeError: boolean;
    private _isFileTypeErrorBS = new BehaviorSubject<boolean>(false);
    private _isInitiated = false;
    private _csvFileSelectorState: Observable<CsvFileSelectorState>;
    @HostBinding('class.with-file') private _withFile = false;

    fileStatusState$: Observable<FileStatusState>;
    fileSelectedText$: Observable<string>;
    isButtonDisabled$: Observable<boolean>;
    fileSize$: Observable<number>;
    fileName$: Observable<string>;
    readonly isDisabled$ = new BehaviorSubject(false);

    constructor(
        private _translateSrv: TranslateService,
        private _ref: ChangeDetectorRef,
        private _msgDialogSrv: MessageDialogService,
        @Self() public ngControl: NgControl,
        @Inject(CSV_FILE_PROCESSOR) private _fileProcessor: CsvFileProcessor
    ) {
        if (this.ngControl != null) {
            this.ngControl.valueAccessor = this;
        }
    }

    ngOnInit(): void {
        this.initControl();
        this.setCsvFileState();
        this.setFileStatus();
        this.setWithFile();
        this.setCsvFileViewModel();
        this._isInitiated = true;
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    fileInputHandler(fileList: FileList): void {
        this.updateControl(fileList[0]);
    }

    clickHandler(): void {
        this.onTouch();
    }

    cancelFileHandler(): void {
        this._msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'CSV.IMPORT.DELETE_SELECTED_FILE',
            message: 'CSV.IMPORT.DELETE_SELECTED_FILE_MESSAGE',
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe(shouldDelete => {
                if (shouldDelete) {
                    this.resetControl();
                }
            });
    }

    // CONTROL VALUE ACCESSOR
    registerOnChange(fn: (csvFile: CsvFile) => void): void {
        this._onChange = fn;
    }

    registerOnTouched(fn: () => void): void {
        this._onTouch = fn;
    }

    writeValue(csvFile: CsvFile): void {
        this._csvFile = csvFile;
        // Not really in use.
        // In case the control is manipulated via form from the parent formGroup,
        // check the implementation and delete this comment
        if (this._isInitiated) {
            this.initControlValue();
        }
    }

    setDisabledState(isDisabled: boolean): void {
        this.isDisabled$.next(isDisabled);
    }

    private initControl(): void {
        this.initControlValue();
        //TODO: if processor file error is wanted for all implementations
        // this.setControlValidators();
    }

    private initControlValue(): void {
        if (this._csvFile?.file) {
            const { file, processedFile } = this._csvFile;
            this.updateControl(file, processedFile);
        } else {
            this._csvFile = { file: null, processedFile: null };
            this.resetControl();
        }
    }

    private updateControl(file: File, processedFile: unknown = null): void {
        if (file && processedFile) {
            this.setFile(file);
            this.setProcessedFile(processedFile);
        } else if (file) {
            this.setFile(file);
            if (!this._isFileTypeError) {
                this.setLoading(true);
                this._fileProcessor.processFile(
                    file,
                    processedFile => {
                        this.setProcessedFile(processedFile);
                        this.setLoading(false);
                        this.onChange();
                    },
                    () => {
                        this.setLoading(false);
                        this.setProcessorError(true);
                    }
                );
            }
        }
    }

    private onChange(): void {
        if (this._onChange) {
            this._onChange({
                file: this._file,
                processedFile: this._processedFile
            });
        }
    }

    private onTouch(): void {
        if (this._onTouch) {
            this._onTouch();
        }
    }

    private setFile(file: File): void {
        this._file = file;
        this._fileBS.next(file);
    }

    private setProcessedFile(processedFile: unknown): void {
        this._processedFile = processedFile;
        this._processedFileBS.next(this._processedFile);
    }

    private setProcessorError(isProcessorError: boolean): void {
        this._isProcessorError = isProcessorError;
        if (this._isProcessorError) {
            this.ngControl.control.setErrors({ ...this.ngControl.control.errors, [CsvErrorEnum.csvProcessorFileError]: true });
        } else if (this.ngControl.control.errors?.[CsvErrorEnum.csvProcessorFileError]) {
            this.ngControl.control.setErrors({ ...this.ngControl.control.errors, [CsvErrorEnum.csvProcessorFileError]: null });
        }
        this._isProcessorErrorBS.next(this._isProcessorError);
    }

    private resetControl(): void {
        this.setFile(null);
        this.setProcessedFile(null);
        this.setProcessorError(false);
        this.onChange();
    }

    // Component
    private setLoading(isLoading: boolean): void {
        this._isLoadingBS.next(isLoading);
    }

    private setWithFile(): void {
        this._csvFileSelectorState
            .pipe(takeUntil(this._onDestroy))
            .subscribe(state => {
                this._withFile = state !== 'empty';
                this._ref.markForCheck();
            });
    }

    private setCsvFileState(): void {
        this._csvFileSelectorState = combineLatest([
            this._fileBS.asObservable(),
            this._processedFileBS.asObservable(),
            this._isLoadingBS.asObservable(),
            this._isProcessorErrorBS.asObservable(),
            this._isFileTypeErrorBS.asObservable()
        ]).pipe(
            map(([file, processedFile, isLoading, isProcessorError, isFileTypeError]) => {
                if (isProcessorError || isFileTypeError) {
                    return 'csvError';
                } else if (isLoading) {
                    return 'loading';
                } else if (file && processedFile) {
                    return 'success';
                } else {
                    return 'empty';
                }
            })
        );
    }

    private setFileStatus(): void {
        this.fileStatusState$ = this._csvFileSelectorState.pipe(
            map(state => {
                switch (state) {
                    case 'csvError':
                        return 'error';
                    case 'loading':
                        return 'loading';
                    case 'empty':
                        return 'empty';
                    case 'success':
                        return 'success';
                }
            })
        );
    }

    private setCsvFileViewModel(): void {
        const file$ = this._fileBS.asObservable();
        this.fileSize$ = file$.pipe(map(file => file?.size));
        this.fileName$ = file$.pipe(map(file => file?.name));

        this.fileSelectedText$ = this._csvFileSelectorState
            .pipe(
                map(state =>
                    state === 'empty' ?
                        this._translateSrv.instant('CSV.IMPORT.SELECTION_LIMIT') :
                        this._translateSrv.instant('CSV.IMPORT.SELECTED_NUMBER', ({ value: 1, total: 1 })))
            );

        this.isButtonDisabled$ = this._csvFileSelectorState
            .pipe(
                map(state => state !== 'empty')
            );
    }
}
