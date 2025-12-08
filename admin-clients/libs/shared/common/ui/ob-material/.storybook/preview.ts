import { ReactiveFormsModule } from '@angular/forms';
import { provideAnimations } from '@angular/platform-browser/animations';
import { Preview, applicationConfig, moduleMetadata } from '@storybook/angular';

const preview: Preview = {
    decorators: [
        applicationConfig({
            providers: [provideAnimations()]
        }),
        moduleMetadata({
            imports: [ReactiveFormsModule]
        })
    ],

    tags: ['autodocs']
};

export default preview;
