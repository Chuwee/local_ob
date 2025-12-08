import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { filter, map } from 'rxjs/operators';
import { ShellComponent } from '@admin-clients/shared/common/ui/components';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { User, UserPermissions, UserRoles } from '@admin-clients/shi-panel/utility-models';
import { SectionList } from './models/nav.model';
import { TopbarComponent } from './topbar/topbar.component';

@Component({
    selector: 'app-nav',
    templateUrl: './nav.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    styles: `
        :host {
            display: contents;
        }
    `,
    imports: [ShellComponent, TopbarComponent]
})
export class NavComponent {
    readonly #auth = inject(AuthenticationService);
    readonly #user$ = this.#auth.getLoggedUser$();

    readonly sectionList$ = this.#user$.pipe(
        filter(user => user !== null),
        map(user => this.initSectionListByUser(user))
    );

    // Creates and return the initial SectionList (conditioned by user permissions),
    // or returns a copy of the last one available (new list with the same items, set a new list forces the view binding)
    private initSectionListByUser(user: User): SectionList {
        const sectionList = new SectionList();
        sectionList.forEach(section => {
            section.visible = section.visible && this.isAllowed(user, section.permissions, null);
            section.isActive = false;
            if (section.subsections?.length) {
                section.subsections.forEach(subsection => {
                    subsection.role ? subsection.visible = subsection.visible && section.visible &&
                        this.isAllowed(user, subsection.permissions, subsection.role) :
                        subsection.visible = subsection.visible && section.visible &&
                        this.isAllowed(user, subsection.permissions, null);
                });
            }
        });
        return sectionList;
    }

    private isAllowed(
        user: User, permissions: UserPermissions[], roles?: UserRoles[]
    ): boolean {
        if (roles?.length > 0) {
            return (
                AuthenticationService.doesUserHaveSomeRole(user, roles)
                && AuthenticationService.doesUserHaveSomePermission(user, permissions)
            );
        } else {
            return !permissions || (AuthenticationService.doesUserHaveSomePermission(user, permissions));
        }
    }
}
