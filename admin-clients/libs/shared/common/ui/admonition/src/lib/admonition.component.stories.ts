/* eslint-disable max-len */
import { Meta, StoryFn } from '@storybook/angular';
import { AdmonitionComponent } from './admonition.component';

const comment = `
Use admonitions to give users *important information* in a visually distinct way.
## Use cases: 
- **Important information**:  Use an admonition to highlight important information that the user should be aware of.
- **Warnings**: Use an admonition to warn the user about potential problems or issues.
- **Errors**: Use an admonition to indicate that an error has occurred.
- **Tips**: Use an admonition to provide helpful tips or advice to the user.
- **Notes**: Use an admonition to provide additional information or context.

## Design Tokens 
Use the CSS Variables specified below to customize the look of your admonitions
`;

export default {
    title: 'Components/Admonition',
    parameters: {
        docs: {
            description: {
                component: comment
            }
        }
    },
    component: AdmonitionComponent,
    argTypes: {
        title: { control: 'text', description: 'Admonition title - supports HTML text', name: 'Title', type: 'string' },
        body: { control: 'text', description: 'Admonition body  - supports HTML text', name: 'Body', type: 'string' },
        icon: {
            control: { type: 'select', labels: { null: 'None' } }, options: [null, 'info', 'warning', 'error', 'lightbulb'],
            description: 'Admonition Icon - could be any material icon', name: 'Icon', type: 'string'
        }
    }
} as Meta;

const Template: StoryFn<AdmonitionComponent> = (props: AdmonitionComponent) => ({ props });

export const Default = Template.bind({});

Default.parameters = {
    docs: {
        description: {
            story: 'The **default variant** uses the a light primary color for the background an a darker primary for the text'
        }
    }
};

Default.args = {
    title: 'Admonition Title',
    body: `Lorem ipsum dolor sit amet, consectetur <em>adipiscing</em> elit, sed do eiusmod tempor <b>incididunt ut labore</b> et dolore magna aliqua.`,
    icon: 'lightbulb'
};

