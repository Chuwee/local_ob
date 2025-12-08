import { StorybookI18nModule } from '@admin-clients/shared/core/data-access';
import { importProvidersFrom } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { provideAnimations } from '@angular/platform-browser/animations';
import { Preview, applicationConfig, moduleMetadata } from '@storybook/angular';

const preview: Preview = {
    decorators: [
        applicationConfig({
            providers: [
                provideAnimations(),
                importProvidersFrom(StorybookI18nModule)
            ]
        }),
        moduleMetadata({
            imports: [FlexLayoutModule, ReactiveFormsModule]
        })
    ],

    tags: ['autodocs']
};
export default preview;
