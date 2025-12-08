import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatTooltipModule } from '@angular/material/tooltip';
import { SatPopoverModule } from '@ncstate/sat-popover';
import { Meta, StoryObj, componentWrapperDecorator, moduleMetadata } from '@storybook/angular';
import { MenuModeDirective } from './menu-mode.directive';

type StoryType = {
    expandedMenu: boolean;
    listMenu: ListMenu[];
};

type ListMenu = {
    title: string;
    active: boolean;
    icon: string;
    submenu: string[];
};

const listMenuMock = [
    {
        title: 'Eventos', active: false, icon: 'house',
        submenu: ['Mis eventos', 'Tickets de temporada', 'Configuraciones', 'Plantillas de ticket',
            'Plantillas de Passbook', 'Plantillas de promoción', 'Agrupación de eventos', 'Productores']
    },
    { title: 'Ventas', active: false, icon: 'trending_up', submenu: ['Transacciones', 'Entradas', 'Abonados Fútbol', 'Tarjetas Regalo'] },
    { title: 'Espectadores', active: false, icon: 'chair', submenu: ['Clientes', 'Suscriptores', 'Compradores'] },
    { title: 'Emails', active: false, icon: 'email', submenu: ['Mis envios', 'Configuración email'] },
    { title: 'Colectivos', active: false, icon: 'workspaces', submenu: ['Colectivos'] },
    { title: 'Recintos', active: false, icon: 'festival', submenu: ['Mis recintos', 'Configuración de recinto'] },
    { title: 'Canales', active: false, icon: 'cable', submenu: ['Canales propios', 'Solicitudes de venta', 'Códigos con saldo'] },
    { title: 'Profesionales', active: false, icon: 'work', submenu: ['Clientes profesionales', 'Condiciones'] },
    { title: 'Mi cuenta', active: false, icon: 'person', submenu: ['Mi entidad', 'Entidades', 'Mi perfil', 'Usuarios'] }
];

const meta: Meta<StoryType> = {
    title: 'OB Material/Sidenav Component',
    decorators: [
        moduleMetadata({
            imports: [
                MatListModule, MatIconModule, MatButtonModule, MatButtonToggleModule,
                MatSidenavModule, MatExpansionModule, MatDividerModule, MatTooltipModule,
                SatPopoverModule, FlexLayoutModule, MenuModeDirective
            ]
        }),
        componentWrapperDecorator(story => `
            <div>
                ${story}
            </div>
        `)
    ],
    parameters: {
        layout: 'centered'
    },
    argTypes: {
        expandedMenu: {
            name: 'Expanded menu',
            description: 'Change between expanded and collapesed menu',
            control: 'boolean'
        },
        listMenu: {
            name: 'Menu data',
            description: 'Example of data of the menu',
            table: { disable: true }
        }
    },
    args: {
        expandedMenu: false,
        listMenu: listMenuMock
    }
};

export default meta;

const templateExpandedMenu = (): string => `
    <div class="logo-ob" style=" padding-top: 24px;
    padding-bottom: 16px; text-align: center; z-index: 2; width: 255px; background-color: $ob-menu-bg;">
        <img style="width: 22px; height: 24px" [src]="logo" />
    </div>
    <div>
        <mat-accordion fxLayout="column" displayMode="flat" role="list">
            <ng-container *ngFor="let section of listMenu">
                <nav class="nav-list" *ngIf="section.active">
                    <a class="list-item">
                        <mat-icon fontSet="material-icons">{{section.icon}}</mat-icon>{{section.title}}
                    </a>
                </nav>
                <mat-expansion-panel>
                    <mat-expansion-panel-header collapsedHeight="66px">
                        <mat-panel-title>
                            <div class="list">
                                <div class="list-item" role="listitem">
                                    <mat-icon fontSet="material-icons">{{section.icon}}</mat-icon>
                                    {{section.title}}
                                </div>
                            </div>
                        </mat-panel-title>
                    </mat-expansion-panel-header>
                    <nav class="nav-list">
                        <div *ngFor="let sec of section.submenu">
                            <a class="list-item">
                                {{sec}}
                            </a>
                        </div>
                    </nav>
                </mat-expansion-panel>
            </ng-container>
        </mat-accordion>
    </div>
`;

const templateCollapsedMenu = (): string => `
    <div class="logo-ob" style=" padding-top: 24px; padding-bottom: 34px; text-align: center;">
        <img style="width: 22px; height: 24px" [src]="logo" />
    </div>
    <div class="sections-list">
        <nav class="nav-list" *ngFor="let section of listMenu">
            <a class="list-item" *ngIf="section.active"
            matTooltipPosition="right" matTooltipShowDelay="50">
                <mat-icon fontSet="material-icons">
                    {{section.icon}}
                </mat-icon>
                <span class="list-title">{{section.title}}</span>
            </a>
            <div class="list-item" matTooltipPosition="right" matTooltipShowDelay="50">
                <button mat-icon-button  class="popover-menu-btn" [satPopoverAnchor]="popoverMenu"
                [class.active-popover]="popoverMenu.isOpen()" (click)="popoverMenu.toggle()">
                    <mat-icon fontSet="material-icons">
                        {{section.icon}}
                    </mat-icon>
                    <span class="list-title">{{section.title}}</span>
                </button>
                <sat-popover #popoverMenu hasBackdrop horizontalAlign="after" verticalAlign="center"
                    >
                    <div class="sidenav-popover-menu">
                        <h3 class="menu-title">{{section.title}}</h3>
                        <mat-divider></mat-divider>
                        <nav class="nav-list">
                            <ng-container *ngFor="let sub of section.submenu">
                                <a class="list-item">
                                    {{sub}}
                                </a>
                            </ng-container>
                        </nav>
                    </div>
                </sat-popover>
            </div>
        </nav>
    </div>
`;

const container = ({
    expandedMenu
}: Partial<StoryType> = {}, title = ''): string => `
    <h2>${title}</h2>
    <mat-sidenav-container class="ob-sidenav-container">
        <mat-sidenav style="position:inherit"
            class="ob-sidenav" fixedInViewport role="navigation" appMenuMode [rail]="true" [isInitiallyOpen]="${expandedMenu}">
            ${expandedMenu ? templateExpandedMenu() : templateCollapsedMenu()}
        </mat-sidenav>
    </mat-sidenav-container>
`;

export const Basic: StoryObj<StoryType> = {
    render: ({ ...args }) => ({
        props: args,
        template: container(args)
    }),
    args: {
        ...meta.args,
        expandedMenu: false
    }
};

export const Snapshot: StoryObj<StoryType> = {
    tags: ['snapshot'],
    render: ({ ...args }) => ({
        props: args,
        template: `<div style="display:flex; gap: 4rem 2rem; flex-direction: column; flex-wrap: wrap; justify-content: space-evenly;">
            ${container({ ...args, expandedMenu: false }, 'Menu collapsed (default)')}
            ${container({ ...args, expandedMenu: true }, 'Menu expanded')}
        </div>`
    })
};

