import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, ContentChild, Input, OnDestroy, TemplateRef, ViewChild, inject } from '@angular/core';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatFormField, MatOption, MatSelect, MatSelectTrigger } from '@angular/material/select';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, finalize, Observable, Subject } from 'rxjs';
import { EphemeralMessageService } from '../ephemeral-message/ephemeral-message.service';
import { GenericElementStatus } from './status-select.model';

@Component({
    imports: [
        CommonModule, MatFormField, MatSelect, MatOption, MatSelectTrigger, MatProgressSpinner, TranslatePipe
    ],
    selector: 'app-status-select',
    templateUrl: './status-select.component.html',
    styleUrls: ['./status-select.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class StatusSelectComponent implements OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    private readonly _ephemeralMsg = inject(EphemeralMessageService);
    private readonly _translate = inject(TranslateService);
    private _statusChangeRequested = new BehaviorSubject(false);

    @ViewChild(MatSelect) selector: MatSelect;

    @Input() label?: string;
    @Input() statusFieldName?: string;
    @Input() idFieldName?: string;
    @Input() element: GenericElementStatus;
    @Input() disabled = false;
    @Input() statusType: string;
    @Input() statusList: Record<string, string>;
    @Input() hiddenStatusList: string[] = [];
    @Input() updateStatus: (id: number, status: string) => Observable<unknown>;
    @ContentChild('optionTemplate') optionTemplateRef: TemplateRef<unknown>;

    readonly statusChangeRequested$: Observable<boolean> = this._statusChangeRequested.asObservable();

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    updateElementStatus(id: number, status: string): void {
        this._statusChangeRequested.next(true);
        this.updateStatus(id, status)
            .pipe(finalize(() => this._statusChangeRequested.next(false)))
            .subscribe({
                error: () => this.selector.value = this.element[this.statusFieldName ?? 'status'],
                next: () => {
                    this.element[this.statusFieldName ?? 'status'] = status;
                    this._ephemeralMsg.showSuccess({
                        msgKey: `${this.statusType}.CHANGE_STATUS_SUCCESS`,
                        msgParams: {
                            status: this._translate.instant(`${this.statusType}.STATUS_OPTS.${this.element[this.statusFieldName ?? 'status']}`)
                        }
                    });
                }
            });
    }
}
