import { customerFriendRelation } from '@admin-clients/cpanel-viewers-customers-data-access';
import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatDialogContent } from '@angular/material/dialog';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { TranslatePipe } from '@ngx-translate/core';
import { AddCustomerType, AddWizardFriendFamilyForm } from '../../models/add-friend-family-form.model';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-add-friend-family-relation',
    imports: [MatDialogContent, TranslatePipe, MatRadioGroup, ReactiveFormsModule, MatRadioButton],
    templateUrl: './add-friend-family-relation.component.html'
})
export class AddFriendFamilyRelationComponent {
    readonly $form = input<FormGroup<AddWizardFriendFamilyForm>>(null, { alias: 'form' });
    // TODO: uncomment when implemented in customers project
    //readonly $friendsRelationType = input.required<EntityFriends['friends_relation_mode']>({ alias: 'friendsRelationType' });
    readonly $relationForm = computed(() => this.$form().controls.relation);
    readonly customerFriendRelation = customerFriendRelation;
    readonly customerType = AddCustomerType;
}
