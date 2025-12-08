import { MatExpansionModule } from '@angular/material/expansion';
import { Meta, StoryObj, moduleMetadata } from '@storybook/angular';

const meta: Meta<void> = {
    title: 'OB Material/Expansion Cards',
    decorators: [
        moduleMetadata({
            imports: [MatExpansionModule]
        })
    ],
    parameters: {
        layout: 'centered'
    }
};

export default meta;

const template = (): string => `
<mat-expansion-panel style="width: 400px" class="ob-expansion-card" expanded [togglePosition]="'before'">
    <mat-expansion-panel-header expandedHeight="*" collapsedHeight="*">
        <mat-panel-title>Listado</mat-panel-title>
    </mat-expansion-panel-header>
    <span>Contenido</span>
</mat-expansion-panel>
`;

export const Basic: StoryObj<void> = {
    render: () => ({
        props: null,
        template: template()
    }),
    args: null
};
