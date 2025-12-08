import { ChangeDetectionStrategy, Component } from '@angular/core';
import { provideRouter, withHashLocation } from '@angular/router';
import { Meta, StoryObj, moduleMetadata, applicationConfig } from '@storybook/angular';
import { NavTabMenuElement } from './models/nav-tabs-menu.models';
import { NavTabsMenuComponent } from './nav-tabs-menu.component';

@Component({ standalone: true, selector: 'app-mock1', template: 'Mock section 1', changeDetection: ChangeDetectionStrategy.OnPush })
class Mock1Component {
}

const meta: Meta<NavTabsMenuComponent> = {
    title: 'components/Nav Tabs Menu Component',
    component: NavTabsMenuComponent,
    decorators: [
        applicationConfig({
            providers: [
                provideRouter([
                    { path: '**', redirectTo: 'general-data' },
                    { path: 'general-data', component: Mock1Component },
                    { path: 'planning', component: Mock1Component },
                    { path: 'attributes', component: Mock1Component },
                    { path: 'capacity', component: Mock1Component },
                    { path: 'presales', component: Mock1Component },
                    { path: 'seat-status', component: Mock1Component },
                    { path: 'communication', component: Mock1Component },
                    { path: 'access-control', component: Mock1Component },
                    { path: 'other-settings', component: Mock1Component },
                    { path: 'code-configuration', component: Mock1Component }
                ], withHashLocation())
            ]
        }),
        moduleMetadata({
            imports: [
                Mock1Component
            ]
        })
    ],
    render: ({ elements }) => ({
        template: `
            <app-nav-tabs-menu type="horizontalSubtab" [elements]="elements"></app-nav-tabs-menu>
        `,
        props: { elements }
    })
};
export default meta;

const elements = [
    {
        label: 'EVENTS.GENERAL_DATA',
        param: 'general-data'
    },
    {
        label: 'EVENTS.PLANNING',
        param: 'planning'
    },
    {
        label: 'EVENTS.ATTRIBUTES',
        param: 'attributes'
    },
    {
        label: 'EVENTS.CAPACITY',
        param: 'capacity',
        disabled: true,
        tooltip: {
            text: 'EVENTS.ARCHIVED_DISABLED_SECTION_INFO',
            disabled: false
        }
    },
    {
        label: 'EVENTS.PRESALES',
        param: 'presales'
    },
    {
        label: 'EVENTS.OCUPATION.TITLE',
        param: 'seat-status',
        tooltip: {
            text: 'EVENTS.ARCHIVED_DISABLED_SECTION_INFO',
            disabled: true
        }
    },
    {
        label: 'EVENTS.COMMUNICATION.TITLE',
        param: 'communication'
    },
    {
        label: 'EVENTS.ACCESS_CONTROL',
        param: 'access-control'
    },
    {
        label: 'EVENTS.OTHER_SETTINGS',
        param: 'other-settings'
    },
    {
        label: 'EVENTS.SESSION.CONF_CODES',
        param: 'code-configuration'
    }
];

export const Primary: StoryObj<NavTabsMenuComponent> = {
    args: { elements }
};

export const Snapshot: StoryObj<NavTabsMenuComponent & { alternativeElements: NavTabMenuElement[] }> = {
    tags: ['snapshot'],
    render: ({ elements, alternativeElements }) => ({
        props: { elements, alternativeElements },
        template: `
        <style>
            h3 {
                margin-bottom: 16px;
            }
        </style>
        <div fxLayout="column" fxLayoutGap="32px">
            <div>
                <h3>HorizontalSubTab type</h3>
                <div fxLayout="column" fxLayoutGap="5px">
                    <app-nav-tabs-menu type="horizontalSubtab" [elements]="alternativeElements"></app-nav-tabs-menu>
                    <app-nav-tabs-menu type="horizontalSubtab" [elements]="elements"></app-nav-tabs-menu>
                </div>
            </div>
            <div>
                <h3>Navigation type</h3>
                <app-nav-tabs-menu type="navigation" [elements]="alternativeElements"></app-nav-tabs-menu>
            </div>
            <div>
                <h3>tabMenu type</h3>
                <app-nav-tabs-menu type="tabMenu" [elements]="alternativeElements"></app-nav-tabs-menu>
            </div>
        </div>
        `
    }),
    args: {
        elements,
        alternativeElements: [
            {
                label: 'EVENTS.GENERAL_DATA',
                param: 'general-data'
            },
            {
                label: 'EVENTS.PLANNING',
                param: 'planning'
            },
            {
                label: 'EVENTS.ATTRIBUTES',
                param: 'attributes'
            }
        ]
    }
};
