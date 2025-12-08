import { Metadata } from '@OneboxTM/utils-state';
import { ContentLink, ContentLinkType } from '@admin-clients/cpanel/shared/data-access';
import { CopyTextComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, input, output } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        MaterialModule,
        TranslatePipe,
        DateTimePipe,
        CopyTextComponent
    ],
    selector: 'app-link-list',
    templateUrl: './link-list.component.html',
    styleUrls: ['./link-list.component.scss']
})
export class LinkListComponent {
    readonly dateTimeFormats = DateTimeFormats;

    readonly $title = input.required<string>({ alias: 'title' });
    @Input() links$: Observable<ContentLink[]>;
    @Input() linksMetadata$: Observable<Metadata>;
    @Input() type: ContentLinkType;
    @Input() pageSize: number;
    @Input() expanded: boolean = true;

    readonly $loadMore = output<PageEvent>();

}
