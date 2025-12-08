import { FEVER_ZONE_URL, FeverService } from "@admin-clients/cpanel-fever-data-access";
import { AuthenticationService } from "@admin-clients/cpanel/core/data-access";
import { ChangeDetectionStrategy, Component, computed, inject, input } from "@angular/core";
import { toSignal } from "@angular/core/rxjs-interop";
import { MatIcon } from "@angular/material/icon";
import { TranslatePipe } from "@ngx-translate/core";

const FEVER_PLAN_DASHBOARD_PATH = 'plans/dashboard';

type LinkType = 'session' | 'event' | 'sessionPack';

@Component({
    selector: 'app-fever-zone-plan-link',
    template: `
        <a class="ob-link flex items-center gap-1"
            [href]="$url()"
            target="_blank"
        >
            <span>{{$linkLabel() | translate}}</span>
            <mat-icon class="ob-icon xxsmall" iconPositionEnd fontIcon="launch" />
        </a>
    `,
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [TranslatePipe, MatIcon],
})
export class FeverZonePlanLinkComponent {
    readonly #auth = inject(AuthenticationService);
    readonly #feverService = inject(FeverService);

    readonly $planId = input.required<string>({ alias: 'planId' });
    readonly $entityId = input.required<number>({ alias: 'entityId' });
    readonly $type = input.required<LinkType>({ alias: 'type' });

    readonly #$token = toSignal(this.#auth.getToken$());
    readonly $redirectTo = computed(() => `/${FEVER_PLAN_DASHBOARD_PATH}?planId=${this.$planId()}`);
    readonly $url = computed(() => this.#feverService.loginUrl(this.$entityId(), this.#$token(), this.$redirectTo()));
    readonly $linkLabel = computed(() => {
        switch (this.$type()) {
            case 'session':
                return 'FEVER.FEVER_ZONE.SESSION_LINK';
            case 'sessionPack':
                return 'FEVER.FEVER_ZONE.SESSION_PACK_LINK';
            default:
                return 'FEVER.FEVER_ZONE.EVENT_LINK';
        }
    });
}
