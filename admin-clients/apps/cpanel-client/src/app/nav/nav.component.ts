import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { AuthenticationService, User, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { CustomManagementType, EntityType } from '@admin-clients/shared/common/data-access';
import { ShellComponent } from '@admin-clients/shared/common/ui/components';
import { CustomResourcesPipe } from '@admin-clients/shared/utility/pipes';
import { SectionList } from './models/nav.model';
import { SidenavReportsComponent } from './sidenav-reports/sidenav-reports.component';
import { TopbarComponent } from './topbar/topbar.component';

@Component({
    selector: 'app-nav',
    templateUrl: './nav.component.html',
    imports: [
        AsyncPipe,
        SidenavReportsComponent,
        TopbarComponent,
        ShellComponent,
        CustomResourcesPipe
    ],
    styles: [`
        :host {
            display: contents;
        }
    `],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavComponent {

    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #auth = inject(AuthenticationService);

    readonly user$: Observable<User> = this.#auth.getLoggedUser$();

    readonly isHandset$ = this.#breakpointObserver.observe(Breakpoints.Handset).pipe(map(result => result.matches));

    readonly sectionList$ = this.user$.pipe(
        filter(user => user !== null),
        map(user => this.initSectionListByUser(user))
    );

    // Creates and return the initial SectionList (conditioned by user roles),
    // or returns a copy of the last one available (new list with the same items, set a new list forces the view binding)
    private initSectionListByUser(user: User): SectionList {
        const sectionList = new SectionList(user);
        sectionList.forEach(section => {
            section.visible = section.visible && this.isAllowed(user, section.role, section.roleToHide);
            section.isActive = false;
            if (section.subsections?.length) {
                section.subsections.forEach(subsection => {
                    subsection.visible = subsection.visible && section.visible &&
                        this.isAllowed(user, subsection.role, section.roleToHide, subsection.entityType,
                            subsection.customManagementType);
                });
                if (section.id === 'section_my-bi') {
                    section.subsections.forEach(subsection => {
                        if (subsection.id === 'substn_superset-bi-reports') {
                            let users: string[] = [];
                            try {
                                users = sessionStorage.getItem('supersetUsers')?.split(',');
                            } catch (error) {
                                console.error(error);
                            }
                            subsection.visible = subsection.visible
                                && (users?.includes(user.id.toString())
                                    || AuthenticationService.isInternalUser(user)
                                    || user.reports?.superset
                                );
                        }
                        if (subsection.id === 'substn_my-bi-reports' || subsection.id === 'substn_my-bi-subscriptions') {
                            subsection.visible = subsection.visible && !user.reports?.superset;
                        }
                    });
                }
            }
        });
        return sectionList;
    }

    private isAllowed(
        user: User, roles: UserRoles[], rolesToHide: UserRoles[] = [],
        entityTypes?: EntityType[], customManagements?: CustomManagementType[]
    ): boolean {
        // Assumption: !roles => Section or view not secured
        return !roles ||
            (AuthenticationService.matchSomeRoleAndEntityTypeInUser(user, roles, entityTypes, customManagements) &&
                !AuthenticationService.isSomeRoleInUserRoles(user, rolesToHide));
    }
}
