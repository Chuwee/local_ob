import { roleEvent, roleHome, roleSales } from '../../auth/services/role-constant';
import { UserRoles } from '../../auth/services/user-roles.model';

export interface AppSection {
    tab: string;
    icon: string;
    label: string;
    role?: UserRoles[];
    visible: boolean;
}

export class SectionList extends Array<AppSection> {
    constructor() {
        super(
            {
                tab: 'home',
                icon: './assets/media/icons/home-icon.svg',
                label: 'TABS.HOME',
                role: roleHome,
                visible: true
            },
            {
                tab: 'events',
                icon: './assets/media/icons/events-icon.svg',
                label: 'TABS.EVENTS',
                role: roleEvent,
                visible: true
            },
            {
                tab: 'sales',
                icon: './assets/media/icons/sells-icon.svg',
                label: 'TABS.SALES',
                role: roleSales,
                visible: true
            }
        );
    }
}
