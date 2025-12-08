import { ChannelMemberExternalService } from '@admin-clients/cpanel-channels-member-external-data-access';
import { Component, ChangeDetectionStrategy, OnInit, inject } from '@angular/core';
import { UntypedFormGroup, FormGroupDirective, FormGroupName } from '@angular/forms';

@Component({
    selector: 'app-subscription-mode-general-settings',
    templateUrl: './general-settings.component.html',
    styleUrls: ['./general-settings.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SubscriptionModeGeneralSettingsComponent implements OnInit {
    readonly #channelMemberSrv = inject(ChannelMemberExternalService);
    #parentForm = inject(FormGroupDirective);
    #parentName = inject(FormGroupName);

    form: UntypedFormGroup;
    capacities$ = this.#channelMemberSrv.channelCapacities.get$();
    roles$ = this.#channelMemberSrv.roles.get$();
    periodicities$ = this.#channelMemberSrv.periodicities.get$();

    ngOnInit(): void {
        this.form = this.#parentForm?.form?.get([this.#parentName?.name]) as UntypedFormGroup;
    }
}
