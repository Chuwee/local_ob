import { User } from '@admin-clients/cpanel/core/data-access';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Observable, filter, map } from 'rxjs';
import { AuthService } from '../auth/services/auth.service';
import { SectionList } from './models/tab.model';

@Component({
    selector: 'admin-clients-tabs',
    templateUrl: 'tabs.page.html',
    styleUrls: ['tabs.page.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class TabsPage {
    readonly #auth = inject(AuthService);
    readonly user$: Observable<User> = this.#auth.getLoggedUser$();
    readonly sectionList$: Observable<SectionList> = this.user$
        .pipe(
            filter(user => user !== null),
            map(user => this.initSectionListByUser(user))
        );

    private initSectionListByUser(user: User): SectionList {
        const sectionList = new SectionList();
        sectionList.forEach(section => {
            if (section.role.length !== 0) { section.visible = section.visible && AuthService.isSomeRoleInUserRoles(user, section.role); }
        });
        return sectionList;
    }
}

