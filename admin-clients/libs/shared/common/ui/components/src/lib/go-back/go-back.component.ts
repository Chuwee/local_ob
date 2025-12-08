/* eslint-disable @typescript-eslint/naming-convention */
import { RoutingState } from '@admin-clients/shared/utility/state';
import { ChangeDetectionStrategy, Component, inject, Input, OnInit } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

const DEFAULT_PREVIOUS_COPY = 'DETAILS.BACK_TO_LIST';

@Component({
    imports: [RouterLink, MatTooltip, MatIcon, TranslatePipe],
    selector: 'app-go-back',
    templateUrl: './go-back.component.html',
    styleUrls: ['./go-back.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class GoBackComponent implements OnInit {
    private readonly _routingState = inject(RoutingState);
    previousPath: string;
    previousQueryParams: Record<string, string>;
    previousCopy = DEFAULT_PREVIOUS_COPY;

    @Input() defaultPath: string;
    @Input() neverPath: string = null;
    @Input() customCopy: { copy: string; rule?: string } = null;

    @Input()
    set redoPath(_: unknown) {
        this.doPath();
    }

    ngOnInit(): void {
        this.doPath();
    }

    private doPath(): void {
        this.previousPath = this._routingState.getPreviousPath(this.defaultPath, this.neverPath);
        this.previousQueryParams = this._routingState.getPreviousQueryParams(this.defaultPath, this.neverPath);
        if (this.customCopy?.copy && this.customCopy?.rule) {
            this.previousCopy = this.previousPath?.includes(this.customCopy.rule) ? this.customCopy.copy : DEFAULT_PREVIOUS_COPY;
        } else if (this.customCopy?.copy) {
            this.previousCopy = this.customCopy.copy;
        } else {
            this.previousCopy = DEFAULT_PREVIOUS_COPY;
        }
    }
}
