import { ChangeDetectionStrategy, Component, computed, input, OnInit } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatDialogContent } from '@angular/material/dialog';
import { AddCustomerType, AddWizardFriendFamilyForm } from '../../models/add-friend-family-form.model';
import { AddFriendFamilyExistingComponent } from './existing/add-friend-family-existing.component';
import { AddFriendFamilyNewComponent } from './new/add-friend-family-new.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-add-friend-family-selection',
    styleUrls: ['./add-friend-family-selection.component.scss'],
    imports: [
        MatDialogContent, ReactiveFormsModule, AddFriendFamilyExistingComponent, AddFriendFamilyNewComponent
    ],
    templateUrl: './add-friend-family-selection.component.html'
})
export class AddFriendFamilySelectionComponent implements OnInit {
    readonly customerType = AddCustomerType;
    readonly $form = input.required<FormGroup<AddWizardFriendFamilyForm>>({ alias: 'form' });
    readonly $customerType = computed(() => this.$form().controls.relation.value.addType);

    ngOnInit(): void {
        if (this.$customerType() === AddCustomerType.existing) {
            this.$form().controls.addFriendFamilyForm.controls.existingCustomerCtrl.enable();
            this.$form().controls.addFriendFamilyForm.controls.newFriendFamilyForm.disable();
        } else {
            this.$form().controls.addFriendFamilyForm.controls.existingCustomerCtrl.disable();
            this.$form().controls.addFriendFamilyForm.controls.newFriendFamilyForm.enable();
        }
    }
}
