import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { BreadcrumbsService } from './breadcrumbs.service';

/**
 * This Bredcrumbs automatically detects de activated components tree of the current route (url)
 * and adds a link to every component that's paired with a `data: { breadcrumb: 'Section Title' }`
 * in it's route definition
 */
@Component({
    selector: 'app-breadcrumbs',
    templateUrl: './breadcrumbs.component.html',
    styleUrls: ['./breadcrumbs.component.scss'],
    imports: [NgIf, AsyncPipe, TranslatePipe, RouterLink],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class BreadcrumbsComponent {
    readonly breadcrumbs$ = inject(BreadcrumbsService).getBreadcrumbs$();
}
