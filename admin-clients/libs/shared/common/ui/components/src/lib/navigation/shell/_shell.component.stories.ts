import { APP_NAME } from '@admin-clients/shared/core/data-access';
import { APP_INITIALIZER, ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Router, provideRouter, withHashLocation } from '@angular/router';
import { Meta, StoryObj, applicationConfig, componentWrapperDecorator } from '@storybook/angular';
import { of } from 'rxjs';
import { BreadcrumbsService } from '../../breadcrumbs/breadcrumbs.service';
import { Section } from '../section-list.models';
import { ShellComponent } from './shell.component';

type ShellStory = StoryObj<ShellComponent>;

export class SectionList extends Array<Section> {
    constructor() {
        super({
            id: 'section1',
            icon: 'grade',
            label: 'TITLES.EVENTS',
            visible: true,
            subsections: [
                {
                    id: 'subsection1',
                    label: 'TITLES.MY_EVENTS',
                    visible: true
                },
                {
                    id: 'subsection2',
                    label: 'TITLES.SEASON_TICKETS',
                    visible: true
                }
            ]
        }, {
            id: 'section2',
            icon: 'trending_up',
            label: 'TITLES.SALES',
            visible: true,
            subsections: [
                {
                    id: 'subsection3',
                    label: 'TITLES.ORDERS',
                    visible: true
                },
                {
                    id: 'subsection4',
                    label: 'TITLES.TICKETS',
                    visible: true
                }
            ]
        });
    }
}

@Component({ standalone: true, selector: 'app-mock1', template: 'Mock section 1', changeDetection: ChangeDetectionStrategy.OnPush })
class Mock1Component {
    private readonly _router = inject(Router);
    constructor() { this._router.navigate(['mock1']); }
}

const meta: Meta<ShellComponent> = {
    title: 'components/ShellComponent',
    component: ShellComponent,
    decorators: [
        applicationConfig({
            providers: [
                {
                    provide: APP_INITIALIZER,
                    useFactory: (breadcrumbsSrv: BreadcrumbsService) => () => breadcrumbsSrv.startListener(),
                    deps: [BreadcrumbsService],
                    multi: true
                },
                provideRouter([
                    { path: '**', redirectTo: 'mock1' },
                    { path: 'mock1', component: Mock1Component, data: { breadcrumb: 'Mock section 1' } }
                ], withHashLocation()),
                {
                    provide: APP_NAME,
                    useValue: 'Onebox Panel'
                }
            ]
        }),
        componentWrapperDecorator(story => `
            <p style="color: red; font-size: 16px; font-weight: bold">... WORK IN PROGRESS ...</p>
            ${story}
        `)
    ],
    args: {
        sectionList$: of(new SectionList())
    }
};

const shellTemplate = (): string => `
        <div style="height: 500px; transform: translateZ(0);">
            <app-shell [sectionList$]="sectionList$" [user$]="user$">
            </app-shell>
        </div>`;

export default meta;

export const Primary: ShellStory = {
    args: {
        ...meta.args
    },
    render: args => ({
        props: args,
        template: shellTemplate()
    })
};

export const Snapshot: ShellStory = {
    tags: ['snapshot'],
    args: {
        ...meta.args
    },
    render: args => ({
        props: args,
        template: shellTemplate()
    })
};
