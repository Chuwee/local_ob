import { MessageDialogService, DialogSize, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { StarIconComponent } from '@admin-clients/shared-common-ui-star-icon';
import { AsyncPipe, NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, finalize, Observable, Subject } from 'rxjs';

export interface DefaultIconComponentLiterals {
    warningDialogTitle?: string;
    warningDialogMessage?: string;
    successMessageRemove?: string;
    successMessageUpdate?: string;
    tooltipDisabledReason?: string;
    tooltipInfo?: string;
}

@Component({
    selector: 'app-default-icon',
    templateUrl: './default-icon.component.html',
    styleUrls: ['./default-icon.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatProgressSpinner, MatIconButton, MatButton, MatIcon, MatTooltip,
        TranslatePipe, AsyncPipe, NgTemplateOutlet, StarIconComponent
    ]
})
export class DefaultIconComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _defaultChangeRequested = new BehaviorSubject(false);
    private _defaultValue = new BehaviorSubject(false);

    @Input() onlyIcon = false;
    @Input() enabled = true;
    @Input() updateDefault: (id: unknown, isDefault: boolean) => Observable<boolean>;
    @Input() showWarning = false;
    @Input() tplName: string;
    @Input() tplId: unknown;
    @Input() literals: DefaultIconComponentLiterals;

    @Input()
    set checked(value: boolean) {
        this._defaultValue.next(value);
    }

    defaultChangeRequested$: Observable<boolean>;
    defaultValue$: Observable<boolean>;

    constructor(
        private _ephemeralMessageService: EphemeralMessageService,
        private _msgDialogService: MessageDialogService
    ) {
    }

    ngOnInit(): void {
        this.defaultChangeRequested$ = this._defaultChangeRequested.asObservable();
        this.defaultValue$ = this._defaultValue.asObservable();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    updateElementDefault(id: unknown, isChecked: boolean): void {
        this._defaultChangeRequested.next(true);
        if (this.showWarning) {
            this._msgDialogService.showWarn({
                size: DialogSize.SMALL,
                title: this.literals.warningDialogTitle,
                message: this.literals.warningDialogMessage,
                messageParams: { name: this.tplName }
            })
                .subscribe(success => {
                    if (success) {
                        this.doUpdateElement(id, isChecked);
                    } else {
                        this._defaultChangeRequested.next(false);
                    }
                });
        } else {
            this.doUpdateElement(id, isChecked);
        }
    }

    doUpdateElement(id: unknown, isChecked: boolean): void {
        this.updateDefault(id, isChecked).pipe(
            finalize(() => this._defaultChangeRequested.next(false))
        ).subscribe(result => {
            if (result) {
                this._defaultValue.next(isChecked);
                this._ephemeralMessageService.showSuccess({
                    msgKey: isChecked ? this.literals.successMessageUpdate : this.literals.successMessageRemove,
                    msgParams: { name: this.tplName }
                });
            }
        });
    }
}
