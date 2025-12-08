import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { Meta, StoryObj, moduleMetadata } from '@storybook/angular';

type StoryType = {
    smallRows: boolean;
    header: 'none' | 'list-header' | 'subheader';
    disabledList: boolean;
    button: boolean;
    icons: boolean;
    hoverableItem: boolean;
    card: boolean;
    subtitle: boolean;
    editableItem: boolean;
    checkDialog: 'none' | 'success' | 'warn' | 'info';
    listElements: ListElement[];
};

type ListElement = {
    label: string;
    checkStar?: boolean;
    restrictiveAccess?: boolean;
    visibility?: boolean;
    errors?: string;
};

const listMock = [
    { label: 'Element 1', checkStar: true, restrictiveAccess: true, visibility: true, errors: 'success' },
    { label: 'Element 2', checkStar: false, restrictiveAccess: false, visibility: false, errors: 'warn' },
    { label: 'Element 3', checkStar: false, restrictiveAccess: true, visibility: true, errors: 'info' }
];

const meta: Meta<StoryType> = {
    title: 'OB Material/List Component',
    decorators: [
        moduleMetadata({
            imports: [MatListModule, MatIconModule, MatButtonModule, MatButtonToggleModule, FlexLayoutModule]
        })
    ],
    parameters: {
        layout: 'centered'
    },
    argTypes: {
        smallRows: {
            name: 'Small rows',
            description: 'Apply small rows',
            control: 'boolean'
        },
        header: {
            name: 'Header',
            description: 'Apply header',
            control: 'select',
            options: ['none', 'list-header', 'subheader']
        },
        disabledList: {
            name: 'Disabled',
            description: 'Disable all the list',
            control: 'boolean'
        },
        button: {
            name: 'Button',
            description: 'Add a button',
            control: 'boolean'
        },
        icons: {
            name: 'Icons',
            description: 'Add icons',
            control: 'boolean'
        },
        hoverableItem: {
            name: 'Hoverable item',
            description: 'Add a background when hover a item',
            control: 'boolean'
        },
        card: {
            name: 'Card',
            description: 'Add a card appareance',
            control: 'boolean'
        },
        subtitle: {
            name: 'Subtitle',
            description: 'Add a subtitle',
            control: 'boolean'
        },
        editableItem: {
            name: 'Editable item',
            description: 'Add a input to the card text to edit',
            control: 'boolean'
        },
        checkDialog: {
            name: 'Check dialog',
            description: 'Add the appearence of the check finish dialog',
            control: 'select',
            options: ['none', 'success', 'warn', 'info']
        },
        listElements: {
            name: 'List data',
            description: 'Example of data structure for not grouped lists',
            table: { disable: true }
        }
    },
    args: {
        listElements: listMock
    }
};

export default meta;

const templateListElement = (
    button,
    hoverable,
    icons,
    card,
    subtitle,
    editable,
    checkDialog,
    le: ListElement
): string => `
    <mat-list-item role="listitem"
    class="${hoverable ? 'hoverable-item' : ''} ${card ? 'card-item with-icon' : ''}
    ${editable ? 'editable-item' : ''}">
    ${icons ? `<div>
        <mat-button-toggle aria-label="Set default rate" #toggle class="ob-icon-button-toggle-simple
            yellow-icon-appearance ob-button medium only-icon" checked="${le.checkStar}">
            <mat-icon [ngClass]="toggle.checked ? 'selected' : 'not-selected'">
                {{toggle.checked ? 'star' : 'star_border'}}
            </mat-icon>
        </mat-button-toggle>
    </div>` : ``}
    ${editable ? `
        <div>
            <mat-button-toggle aria-label="Set default rate" #toggle
                class="ob-icon-button-toggle-simple yellow-icon-appearance ob-button medium only-icon"
                checked="${le.checkStar}">
                <mat-icon [ngClass]="toggle.checked ? 'selected' : 'not-selected'">
                    {{toggle.checked ? 'star' : 'star_border'}}
                </mat-icon>
            </mat-button-toggle>
            <mat-button-toggle aria-label="Set restrictive access" #toggle
                class="ob-icon-button-toggle-simple ob-button medium only-icon"
                checked="${le.restrictiveAccess}">
                <mat-icon [ngClass]="toggle.checked ? 'selected' : 'not-selected'">
                    new_releases
                </mat-icon>
            </mat-button-toggle>
        </div>` : ``}
        ${card ? `<mat-icon mat-list-icon class="drag-icon">drag_indicator</mat-icon>` : ``}
        ${(checkDialog !== 'none') ? `
            <mat-icon class="ob-icon ${checkDialog}">
            ${checkDialog === 'info' ? 'change_circle' : ''}
                ${checkDialog === 'success' ? 'check_circle' : ''}
                ${checkDialog === 'warn' ? 'cancel' : ''}
            </mat-icon>
            <span fxFlex style="padding-left: 16px">
                <div class="result-data" fxLayoutAlign="space-between center">
                    <span class="result-data-text">${le.label}</span>
                    <span class="result-data-value">12</span>
                </div>
            </span>

        ` : ``}
        ${(!subtitle && !editable && checkDialog === 'none') ? `
            <span fxFlex>${le.label}</span>` : ``}
        ${editable ? `
        <div fxFlex class="write-value">
            <input type="text" class="rate-input" value="${le.label}">
        </div>` : ``}
        ${subtitle ? `
        <div fxFlex>
            <p class="card-title">Card title</p>
            <p class="card-text">Subtitle ${le.label}</p>
        </div>` : ``}
        ${button ? `
        <div class="go-link" fxLayoutAlign="end">
            <a mat-button aria-label="Go to link" class="ob-button medium">
                <span>Button</span>
                <mat-icon iconPositionEnd>launch</mat-icon>
            </a>
        </div>` : ``}
        ${(card || editable) ? `
        <div class="delete-action" fxLayout="row" fxLayoutAlign="end center">
            <button type="button" mat-icon-button class="ob-button xsmall only-icon">
                <mat-icon class="delete-icon">delete</mat-icon>
            </button>
        </div>` : ``}
        ${icons ? `
        <div>
            <mat-button-toggle aria-label="Set visible/not visible rate"
            class="ob-icon-button-toggle-simple ob-button small only-icon"
            checked="${le.visibility}">
                <mat-icon>visibility</mat-icon>
            </mat-button-toggle>
        </div>` : ``}
    </mat-list-item>`;

const template = ({
    smallRows,
    header,
    disabledList,
    button,
    hoverableItem,
    icons,
    card,
    subtitle,
    editableItem,
    checkDialog,
    listElements
}: Partial<StoryType> = {}): string => `
<mat-list style="min-width:450px" class="ob-list ${smallRows ? 'small-rows' : ''} ${disabledList ? 'disabled' : ''}">
    ${header === 'subheader' ? '<h3 matSubheader> Subheader </h3>' : ''}
    ${header === 'list-header' ? `<h3 class="list-header"> List-header </h3>` : ``}
    ${listElements.reduce((acc, elem) =>
    (acc += templateListElement(button, hoverableItem, icons, card, subtitle, editableItem, checkDialog, elem), acc)
    , '')}
</mat-list>
`;

const container = (args: Partial<StoryType> = {}, title = ''): string => `
<div style="display:flex; gap: 1rem; flex-direction: column">
    <h2>${title}</h2>
    <div style="max-height: 300px; overflow-y: auto;">
        ${template(args)}
    </div>
</div>
`;

export const Basic: StoryObj<StoryType> = {
    render: ({ ...args }) => ({
        props: args,
        template: template(args)
    }),
    args: {
        ...meta.args,
        checkDialog: 'none',
        smallRows: false,
        header: 'none',
        disabledList: false,
        button: false,
        icons: false,
        hoverableItem: true,
        card: false,
        subtitle: false,
        editableItem: false,
        listElements: listMock
    }
};

export const Snapshot: StoryObj<StoryType> = {
    tags: ['snapshot'],
    render: ({ ...args }) => ({
        props: args,
        template: `<div style="display:flex; gap: 4rem 2rem; flex-direction: row; flex-wrap: wrap; justify-content: space-evenly;">
            ${container({ ...args, smallRows: false, header: 'none', checkDialog: 'none' }, 'Normal list')}
            ${container({ ...args, smallRows: true, header: 'none', checkDialog: 'none' }, 'Small row list')}
            ${container({ ...args, icons: true, header: 'none', checkDialog: 'none' }, 'List with icons')}
            ${container({ ...args, header: 'subheader', checkDialog: 'none' }, 'List with subheader')}
            ${container({ ...args, header: 'list-header', checkDialog: 'none' }, 'List with header')}
            ${container({ ...args, header: 'none', disabledList: true, checkDialog: 'none' }, 'Disabled')}
            ${container({ ...args, header: 'none', button: true, checkDialog: 'none' }, 'List with buttons')}
            ${container({ ...args, header: 'none', button: true, subtitle: true, checkDialog: 'none' }, 'List with buttons & subtitle')}
            ${container({ ...args, header: 'none', card: true, subtitle: false, checkDialog: 'none' }, 'Card list')}
            ${container({ ...args, header: 'none', card: true, subtitle: true, checkDialog: 'none' }, 'Card list with subtitle')}
            ${container({ ...args, header: 'none', editableItem: true, checkDialog: 'none' }, 'List with editable inputs')}
            ${container({ ...args, header: 'none', checkDialog: 'warn' }, 'List with result icons')}
        </div>`
    })
};
