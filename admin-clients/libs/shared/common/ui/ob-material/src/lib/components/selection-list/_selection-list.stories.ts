import { MatListModule } from '@angular/material/list';
import { Meta, StoryObj, componentWrapperDecorator, moduleMetadata } from '@storybook/angular';

type StoryType = {
    listMode: 'basic' | 'grouped';
    listElements: ListElement[];
    listGroups: ListGroup[];
};

type ListGroup = {
    groupName: string;
    listElements: ListElement[];
};

type ListElement = {
    elementName: string;
};

const listElementsMock = [
    { elementName: 'Element 1' },
    { elementName: 'Element 2' },
    { elementName: 'Element 3' },
    { elementName: 'Element 4' },
    { elementName: 'Element 5' },
    { elementName: 'Element 6' },
    { elementName: 'Element 7' },
    { elementName: 'Element 8' },
    { elementName: 'Element 9' },
    { elementName: 'Element 10' }
];

const listGroupsMock = [
    {
        groupName: 'Group 1',
        listElements: [
            { elementName: 'Element 1a' },
            { elementName: 'Element 1b' },
            { elementName: 'Element 1c' },
            { elementName: 'Element 1d' }
        ]
    },
    {
        groupName: 'Group 2',
        listElements: [
            { elementName: 'Element 2a' },
            { elementName: 'Element 2b' }
        ]
    },
    {
        groupName: 'Group 3',
        listElements: [
            { elementName: 'Element 3a' },
            { elementName: 'Element 3b' },
            { elementName: 'Element 3c' },
            { elementName: 'Element 3d' }
        ]
    },
    {
        groupName: 'Group 4',
        listElements: [
            { elementName: 'Element 4a' },
            { elementName: 'Element 4b' },
            { elementName: 'Element 4c' }
        ]
    }
];

const meta: Meta<StoryType> = {
    title: 'OB Material/Selection List Component',
    decorators: [
        moduleMetadata({
            imports: [MatListModule]
        }),
        componentWrapperDecorator(story => `
            <p style="color: red; font-size: 16px; font-weight: bold">... WORK IN PROGRESS ...</p>
            <div>
                ${story}
            </div>
        `)
    ],
    parameters: {
        layout: 'centered'
    },
    argTypes: {
        listMode: {
            name: 'List mode',
            description: 'Change between list modes',
            control: {
                type: 'select',
                labels: {
                    basic: 'Basic list',
                    grouped: 'Grouped list'
                }
            },
            options: ['basic', 'grouped']
        },
        listElements: {
            name: 'List data',
            description: 'Example of data structure for not grouped lists',
            table: { disable: true }
        },
        listGroups: {
            name: 'Grouped list data',
            description: 'Example of data structure for grouped lists',
            table: { disable: true }
        }
    },
    args: {
        listElements: listElementsMock,
        listGroups: listGroupsMock
    }
};

export default meta;

const template = ({ listMode }: Partial<StoryType> = {}): string => {
    let template = '';
    switch (listMode) {
        case 'basic':
            template = `
                <mat-list-option *ngFor="let listElement of listElements" color="primary" checkboxPosition="before">
                    {{listElement.elementName}}
                </mat-list-option>
            `;
            break;
        case 'grouped':
            template = `
                <div *ngFor="let group of listGroups; let first = first">
                    <div matSubheader [ngClass]="{'first-subheader': first}">
                        {{group.groupName}}
                    </div>
                    <mat-list-option *ngFor="let listElement of group.listElements" color="primary" checkboxPosition="before">
                        {{listElement.elementName}}
                    </mat-list-option>
                </div>
            `;
            break;
    }
    return `
        <div fxFlex="1 1 800px">
            <mat-selection-list class="ob-list">
                <div style="max-height: 400px; overflow-y: auto;">
                    ${template}
                </div>
            </mat-selection-list>
        </div>
    `;
};

export const Basic: StoryObj<StoryType> = {
    render: ({ ...args }) => ({
        props: args,
        template: template(args)
    }),
    args: {
        ...meta.args,
        listMode: 'basic'
    }
};

export const Snapshot: StoryObj<StoryType> = {
    tags: ['snapshot'],
    render: ({ ...args }) => ({
        props: args,
        template: `
            <div style="margin-bottom:56px;">
                ${template({
            ...args,
            listMode: 'basic'
        })}
            </div>
            <div style="margin-bottom:56px;">
                ${template({
            ...args,
            listMode: 'grouped'
        })}
            </div>
        `
    }),
    args: { ...meta.args }
};
