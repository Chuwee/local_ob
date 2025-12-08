import { CustomerFriendRelation } from '@admin-clients/cpanel-viewers-customers-data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { ChangeDetectionStrategy, Component, computed, inject, input } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormGroup } from '@angular/forms';
import { MatCard, MatCardContent } from '@angular/material/card';
import { MatDialogContent } from '@angular/material/dialog';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map } from 'rxjs/operators';
import {
    AddCustomerType, AddWizardFriendFamilyForm, NewFriendFamilyForm, VmCustomersToBeFriend, VmCustomerToBeFriendConfirmation
} from '../../models/add-friend-family-form.model';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-add-friend-family-confirmation',
    styleUrls: ['./add-friend-family-confirmation.component.scss'],
    imports: [MatDialogContent, MatCard, MatCardContent, TranslatePipe, EllipsifyDirective, MatTooltip],
    templateUrl: './add-friend-family-confirmation.component.html'
})
export class AddFriendFamilyConfirmationComponent {
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly $form = input.required<FormGroup<AddWizardFriendFamilyForm>>({ alias: 'form' });
    readonly $customerToBeFriend = computed(() => this.#getCustomerToBeFriend(this.$form()));

    readonly $entityMemberIdGeneration = toSignal(this.#entitiesSrv.getEntity$().pipe(filter(Boolean),
        map(entity => entity.settings?.member_id_generation || 'DEFAULT')));

    #getCustomerToBeFriend(form: FormGroup<AddWizardFriendFamilyForm>): VmCustomerToBeFriendConfirmation {
        let customerToBeFriend: VmCustomerToBeFriendConfirmation;
        const addFriendFamilyForm = form.controls.addFriendFamilyForm.controls;
        const relation = form.controls.relation.value;
        if (relation.addType === AddCustomerType.existing) {
            customerToBeFriend = this.#mapFromExistingCustomer(addFriendFamilyForm.existingCustomerCtrl.value[0], relation.type);
        } else {
            customerToBeFriend = this.#mapFromNewCustomer(addFriendFamilyForm.newFriendFamilyForm, relation.type);
        }
        return customerToBeFriend;
    }

    #mapFromExistingCustomer(existingCustomer: VmCustomersToBeFriend, relation: CustomerFriendRelation):
        VmCustomerToBeFriendConfirmation {
        return {
            email: existingCustomer.email,
            identification: existingCustomer.id_card,
            member_id: existingCustomer.member_data?.id,
            name: existingCustomer.name,
            surname: existingCustomer.surname,
            relation
        };
    }

    #mapFromNewCustomer({ value }: FormGroup<NewFriendFamilyForm>, relation: CustomerFriendRelation):
        VmCustomerToBeFriendConfirmation {
        return {
            email: value.email,
            identification: value.identification,
            member_id: value.memberId,
            surname: value.surname,
            name: value.name,
            relation
        };
    }
}
