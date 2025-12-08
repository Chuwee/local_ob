import { ChangeDetectionStrategy, Component, Input, Output, EventEmitter } from '@angular/core';
import { ProgressBarMode } from '@angular/material/progress-bar';
import { TranslateService } from '@ngx-translate/core';
import { FileStatusState } from './models/file-status.model';

type IconClassName = 'info' | 'success' | 'warn' | 'empty';
type ProgressBarClassName = 'info' | 'success' | 'warn' | 'empty';
type IconName = 'cancel' | 'arrow_upward' | 'change_circle' | 'check_circle';

@Component({
    selector: 'app-file-status',
    templateUrl: './file-status.component.html',
    styleUrls: ['./file-status.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class FileStatusComponent {
    private _isCancelable = true;

    @Output()
    cancelFile = new EventEmitter<void>();

    @Input()
    state: FileStatusState = 'empty';

    @Input()
    name: string;

    @Input()
    size = 0;

    @Input()
    disabled = false;

    @Input()
    set isCancelable(value: boolean) {
        this._isCancelable = value;
    }

    get isCancelable(): boolean {
        return this.state !== 'empty' && this._isCancelable;
    }

    constructor(
        private _translateSrv: TranslateService
    ) {
    }

    getIconClassName(): IconClassName {
        switch (this.state) {
            case 'error':
                return 'warn';
            case 'loading':
                return 'info';
            case 'empty':
                return 'empty';
            case 'success':
                return 'success';
        }
    }

    getProgressBarClassName(): ProgressBarClassName {
        switch (this.state) {
            case 'error':
                return 'warn';
            case 'loading':
                return 'info';
            case 'empty':
                return 'empty';
            case 'success':
                return 'success';
        }
    }

    getProgressMode(): ProgressBarMode {
        switch (this.state) {
            case 'loading':
                return 'indeterminate';
            default:
                return 'determinate';
        }
    }

    getProgressValue(): number {
        if (this.state === 'success') {
            return 100;
        } else {
            return 0;
        }
    }

    getProgressLabel(): string {
        if (this.name) {
            return this.name;
        } else {
            return this._translateSrv.instant('FILE.NOT_SELECTED');
        }
    }

    getIconName(): IconName {
        switch (this.state) {
            case 'error':
                return 'cancel';
            case 'empty':
                return 'arrow_upward';
            case 'loading':
                return 'change_circle';
            case 'success':
                return 'check_circle';
        }
    }

    cancelFileHandler(): void {
        this.cancelFile.emit();
    }
}
