import { Meta, moduleMetadata, StoryObj } from '@storybook/angular';
import { TabDirective } from './tab.directive';
import { TabsMenuComponent } from './tabs-menu.component';

type TabsStory = StoryObj<TabDirective & TabsMenuComponent>;

const meta: Meta<TabsMenuComponent & TabDirective> = {
    title: 'components/Tabs Menu Component',
    decorators: [
        moduleMetadata({
            imports: [
                TabsMenuComponent,
                TabDirective
            ]
        })
    ],
    args: {
        type: 'navigation',
        dynamicHeight: false,
        lazyLoad: false,
        selectedIndex: 0,
        overrideClasses: ''
    }
};

const tabsTemplate = (args): string => {
    const { type, dynamicHeight, lazyLoad, selectedIndex, overrideClasses } = args;
    return `
    <app-tabs-menu [type]="'${type}'" [dynamicHeight]="${dynamicHeight}" [selectedIndex]="${selectedIndex}"
        [overrideClasses]="'${overrideClasses}'">
        <ng-template appTab [lazyLoad]="${lazyLoad}" [label]="'Title Tab 1'">
            <div style="height: 100px; border: 1px solid red">
                Content Tab 1
            </div>
        </ng-template>
        <ng-template appTab [lazyLoad]="${lazyLoad}" [label]="'Title Tab 2'">
            <div style="height: 200px; border: 1px solid blue">
                Content Tab 2
            </div>
        </ng-template>
    </app-tabs-menu>`;
};

export default meta;

export const Navigation: TabsStory = {
    args: {
        ...meta.args
    },
    render: args => ({
        props: args,
        template: tabsTemplate(args)
    })
};

export const Snapshot: TabsStory = {
    tags: ['snapshot'],
    args: {
        ...meta.args
    },
    render: args => ({
        props: args,
        template: `
                <style>
                    h3 {
                        margin-bottom: 16px;
                    }
                </style>
                <div style="display:flex; flex-direction:column; gap:32px;">
                    <div>
                        <h3>Navigation type</h3>
                        ${tabsTemplate({ ...args, type: 'navigation' })}
                    </div>
                    <div>
                        <h3>Language type</h3>
                        ${tabsTemplate({ ...args, type: 'language' })}
                    </div>
                    <div>
                        <h3>Invisible type</h3>
                        ${tabsTemplate({ ...args, type: 'invisible' })}
                    </div>
                    <div>
                        <h3>Dynamic height</h3>
                        ${tabsTemplate({ ...args, dynamicHeight: true })}
                    </div>
                    <div>
                         <h3>Lazy load tabs</h3>
                        ${tabsTemplate({ ...args, lazyLoad: true })}
                    </div>
                    <div>
                        <h3>Selected index 1</h3>
                        ${tabsTemplate({ ...args, selectedIndex: 1 })}
                    </div>
                    <div>
                        <h3>Language with no padding left</h3>
                        ${tabsTemplate({ ...args, type: 'language', overrideClasses: 'no-left-padding' })}
                    </div>
                    <div>
                        <h3>Language vertical</h3>
                        ${tabsTemplate({ ...args, type: 'language', overrideClasses: 'vertical' })}
                    </div>
                </div>
            `
    })
};
