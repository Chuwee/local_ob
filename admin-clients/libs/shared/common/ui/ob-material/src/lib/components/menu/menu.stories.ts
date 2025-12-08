import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatMenu, MatMenuModule } from '@angular/material/menu';
import { Meta, StoryObj, moduleMetadata } from '@storybook/angular';
import { userEvent, waitFor, within, expect } from 'storybook/test';

const meta: Meta<unknown> = {
    component: MatMenu,
    title: 'OB Material/Menu Component',
    decorators: [
        moduleMetadata({
            providers: [],
            imports: [CommonModule, MatButtonModule, MatMenuModule]
        })
    ],
    play: async ({ canvasElement }) => {
        const canvas = within(canvasElement);
        const parent = within(canvasElement.parentNode as HTMLElement);
        await waitFor(() => canvas.getByTestId('menuTestId'));
        await waitFor(() => canvas.getByRole('button'))
            .then(button => userEvent.click(button));
        await expect(parent.getByTestId('menuItemTestId')).toBeInTheDocument();
    },
    parameters: {
        chromatic: {
            pauseAnimationAtEnd: true
        }
    }
};

export default meta;

const template = (): string => `
    <div style="height: 190px; padding: 20px">
        <button [matMenuTriggerFor]="matMenu">MENU</button>
        <mat-menu #matMenu data-testid="menuTestId">
            <div mat-menu-item data-testid="menuItemTestId">
                EXPORT
            </div>
            <div mat-menu-item [disabled]="true">
                DELETE
            </div>
            <div mat-menu-item>
                EXPORT
            </div>
        </mat-menu>
    </div>
`;

export const Basic: StoryObj<unknown> = {
    render: args => ({
        props: args,
        template: template()
    })
};
