import { NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
    selector: 'app-entity-customer-content',
    templateUrl: './entity-customer-content.component.html',
    imports: [RouterModule, MaterialModule, NavTabsMenuComponent],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntityCustomerContentComponent {

}
