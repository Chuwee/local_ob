import { APP_NAME } from '@admin-clients/shared/core/data-access';
import { APP_INITIALIZER, ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Router, provideRouter, withHashLocation } from '@angular/router';
import { Meta, StoryObj, applicationConfig, componentWrapperDecorator, moduleMetadata } from '@storybook/angular';
import { BreadcrumbsComponent } from './breadcrumbs.component';
import { BreadcrumbsService } from './breadcrumbs.service';

@Component({ standalone: true, selector: 'app-mock1', template: 'Mock section 1', changeDetection: ChangeDetectionStrategy.OnPush })
class Mock1Component {
    private readonly _router = inject(Router);
    constructor() { this._router.navigate(['mock1']); }
}

@Component({ standalone: true, selector: 'app-mock2', template: 'Mock section 2', changeDetection: ChangeDetectionStrategy.OnPush })
class Mock2Component {
    private readonly _router = inject(Router);
    constructor() { this._router.navigate(['mock1/mock2']); }
}

const meta: Meta<BreadcrumbsComponent> = {
    title: 'components/Breadcrumbs Component',
    component: BreadcrumbsComponent,
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
                    {
                        path: 'mock1', component: Mock1Component, data: { breadcrumb: 'Mock section 1' },
                        children: [
                            { path: 'mock2', component: Mock2Component, data: { breadcrumb: 'Mock section 2' } }
                        ]
                    }
                ], withHashLocation()),
                { provide: APP_NAME, useValue: 'Onebox Panel' }
            ]
        }),
        moduleMetadata({
            imports: [Mock1Component, Mock2Component]
        })
    ]
};

export default meta;

export const With1Level: StoryObj<BreadcrumbsComponent> = {
    decorators: [
        componentWrapperDecorator(story => `
            ${story}
            <app-mock1 style="margin:16px 50px; display: block;"></app-mock1>
        `)
    ],
    render: () => ({})
};

export const With2Levels: StoryObj<BreadcrumbsComponent> = {
    decorators: [
        componentWrapperDecorator(story => `
            ${story}
            <app-mock2 style="margin:16px 50px; display: block;"></app-mock2>
        `)
    ],
    render: () => ({})
};
