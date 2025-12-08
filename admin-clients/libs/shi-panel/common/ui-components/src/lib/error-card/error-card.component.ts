import { CopyTextComponent, ListFiltersService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { CustomScrollDirective } from '@admin-clients/shi-panel/utility-directives';
import { CommonModule } from '@angular/common';
import { Component, ChangeDetectionStrategy, Input, TemplateRef } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    imports: [
        CommonModule, TranslatePipe, CopyTextComponent, MaterialModule, FlexLayoutModule,
        SharedUtilityDirectivesModule, CustomScrollDirective
    ],
    selector: 'app-error-card',
    templateUrl: './error-card.component.html',
    styleUrls: ['./error-card.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ErrorCardComponent {
    @Input() row;
    @Input() unknownErrorDefault: string;
    @Input() actions: TemplateRef<any>;
}
