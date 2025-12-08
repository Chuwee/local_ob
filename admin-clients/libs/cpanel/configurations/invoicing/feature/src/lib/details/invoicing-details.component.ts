import { NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-invoicing-details',
    templateUrl: './invoicing-details.component.html',
    styleUrls: ['./invoicing-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule, TranslatePipe, NavTabsMenuComponent, RouterOutlet
    ]
})
export class InvoicingDetailsComponent { }
