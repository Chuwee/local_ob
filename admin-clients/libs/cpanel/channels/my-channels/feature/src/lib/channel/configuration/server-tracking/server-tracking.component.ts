import { ChannelExternalToolName, ChannelsExtendedService, ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { ChangeDetectionStrategy, Component, effect, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatDivider } from '@angular/material/divider';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { TranslatePipe } from '@ngx-translate/core';
import { forkJoin, Observable, of, throwError } from 'rxjs';
import { tap } from 'rxjs/operators';
import { TrackingCredential, TrackingCredentialsComponent } from './credentials/tracking-credentials.component';
import { GA_CONFIG, META_CONFIG } from './server-tracking';

const GA = ChannelExternalToolName.sgtmGoogleAnalytics;
const META = ChannelExternalToolName.sgtmMeta;
const SGTM = ChannelExternalToolName.sgtm;

@Component({
    selector: 'app-channel-server-tracking',
    templateUrl: './server-tracking.component.html',
    styleUrls: ['./server-tracking.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, FormContainerComponent, TranslatePipe,
        MatProgressSpinner, MatSlideToggle, TrackingCredentialsComponent,
        MatDivider
    ]
})
export class ChannelServerTrackingComponent {

    readonly #channelsExtSrv = inject(ChannelsExtendedService);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #msgEphemeralSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);

    readonly $channel = toSignal(this.#channelsSrv.getChannel$());
    readonly $loading = toSignal(this.#channelsExtSrv.externalTools.loading$());
    readonly $tools = toSignal(this.#channelsExtSrv.externalTools.get$());

    readonly metaConfig = META_CONFIG;
    readonly gaConfig = GA_CONFIG;

    readonly form = this.#fb.group({
        meta: this.#fb.group({
            enabled: this.#fb.control(false),
            credentials: this.#fb.control<TrackingCredential[]>([])
        }),
        google_analytics: this.#fb.group({
            enabled: this.#fb.control(false),
            credentials: this.#fb.control<TrackingCredential[]>([])
        }),
        gateway: this.#fb.group({
            enabled: this.#fb.control(false)
        })
    });

    constructor() {

        effect(() => {
            const channel = this.$channel();
            if (channel) {
                this.#loadData();
            }
        });

        effect(() => {
            const tools = this.$tools();
            const meta = tools?.find(tool => tool.name === META);
            const googleAnalytics = tools?.find(tool => tool.name === GA);
            const sgtm = tools?.find(tool => tool.name === SGTM);

            const metaCredentials = meta?.sgtm_facebook_credentials?.map(credential => ({
                pixel_id: credential.pixelId, api_secret: credential.schema['api_secret']
            }));

            const googleAnalyticsCredentials = googleAnalytics?.sgtm_google_credentials?.map(credential => ({
                measurement_id: credential.measurementId, api_secret: credential.schema['api_secret']
            }));

            if (!tools) return;

            this.form.reset({
                meta: {
                    enabled: meta?.enabled || false,
                    credentials: metaCredentials || []
                },
                google_analytics: {
                    enabled: googleAnalytics?.enabled || false,
                    credentials: googleAnalyticsCredentials || []
                },
                gateway: {
                    enabled: sgtm?.enabled || false
                }
            }, { emitEvent: false, onlySelf: true });

        });

    }

    save(): void {
        this.save$().subscribe(() => this.#loadData());
    }

    save$(): Observable<void[]> {
        const metaValid = this.#isValid(this.form.controls.meta);
        const googleAnalyticsValid = this.#isValid(this.form.controls.google_analytics);
        if (this.form.valid && this.form.dirty && metaValid && googleAnalyticsValid) {
            const saveMeta$ = this.#updateMetaTool();
            const saveGoogleAnalytics$ = this.#updateGoogleAnalyticsTool();
            const saveGateway$ = this.#updateGatewayTool();
            const actions$ = [saveMeta$, saveGoogleAnalytics$, saveGateway$];
            const showSuccess = (): void => this.#msgEphemeralSrv.showSaveSuccess();
            return forkJoin(actions$).pipe(tap(() => showSuccess()));
        } else {
            this.form.markAllAsTouched();
            if (!metaValid) {
                return throwError(() => 'invalid meta form');
            } else if (!googleAnalyticsValid) {
                return throwError(() => 'invalid ga form');
            } else if (!this.form.valid) {
                return throwError(() => 'invalid form');
            } else {
                return throwError(() => 'form not dirty');
            }
        }
    }

    cancel(): void {
        this.#loadData();
    }

    #isValid(form: FormGroup<{ enabled: FormControl<boolean>; credentials: FormControl<TrackingCredential[]> }>): boolean {
        const enabled = form.get('enabled')?.value;
        const credentials = form.get('credentials')?.value;
        const filteredCredentials = this.#filterCredentials(credentials);
        const credentialsValid = filteredCredentials.length > 0;
        return !enabled || credentialsValid;
    }

    #filterCredentials(credentials: TrackingCredential[]): TrackingCredential[] {
        return credentials.filter(credential =>
            Object.values(credential).every(value =>
                value !== null && value !== undefined && value.trim() !== ''
            ));
    }

    #updateGoogleAnalyticsTool(): Observable<void> {
        const googleAnalyticsForm = this.form.controls.google_analytics;
        const googleAnalyticsConfig = googleAnalyticsForm.value;
        const googleAnalyticsCredentials = this.#filterCredentials(googleAnalyticsConfig.credentials);

        const channel = this.$channel();

        if (googleAnalyticsForm.dirty) {
            const config = {
                enabled: googleAnalyticsConfig.enabled,
                sgtm_google_credentials: googleAnalyticsCredentials.map(credential => ({
                    measurementId: credential['measurement_id'],
                    schema: {
                        api_secret: credential['api_secret']
                    }
                }))
            };
            return this.#channelsExtSrv.externalTools.update(channel.id, GA, config);
        } else {
            return of(null);
        }
    }

    #updateMetaTool(): Observable<void> {
        const metaForm = this.form.controls.meta;
        const metaConfig = metaForm.value;
        const metaCredentials = this.#filterCredentials(metaConfig.credentials);
        const channel = this.$channel();

        if (metaForm.dirty) {
            const config = {
                enabled: metaConfig.enabled,
                sgtm_facebook_credentials: metaCredentials.map(credential => ({
                    pixelId: credential['pixel_id'],
                    schema: {
                        api_secret: credential['api_secret']
                    }
                }))
            };
            return this.#channelsExtSrv.externalTools.update(channel.id, META, config);
        } else {
            return of(null);
        }
    }

    #updateGatewayTool(): Observable<void> {
        const gatewayForm = this.form.controls.gateway;
        const gatewayConfig = gatewayForm.value;
        const channel = this.$channel();

        if (gatewayForm.dirty) {
            const config = {
                enabled: gatewayConfig.enabled
            };
            return this.#channelsExtSrv.externalTools.update(channel.id, SGTM, config);
        } else {
            return of(null);
        }
    }

    #loadData(): void {
        this.#channelsExtSrv.externalTools.load(this.$channel().id);
    }
}
