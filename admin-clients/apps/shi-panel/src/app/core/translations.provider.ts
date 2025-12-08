import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import { HttpClient } from '@angular/common/http';
import { inject, Provider } from '@angular/core';
import { ModuleTranslateLoader, type IModuleTranslationOptions } from '@larscom/ngx-translate-module-loader';
import { TranslateLoader } from '@ngx-translate/core';

export function moduleHttpLoaderFactory(): Provider {
    return {
        provide: TranslateLoader,
        useFactory: () => {
            const http = inject(HttpClient);
            const env = inject(ENVIRONMENT_TOKEN);
            const url = 'https://client-dists.shi.events';
            const baseTranslateUrl = `${url}/shipanel-client-translations`;
            const envFolder = env.production ? 'production' : 'staging';

            const options: IModuleTranslationOptions = {
                translateError: (error, path) => {
                    console.log('Cannot load literal files: ', { error: String(error), path });
                },
                modules: [
                    { baseTranslateUrl: baseTranslateUrl + '/' + envFolder }
                ]
            };
            return new ModuleTranslateLoader(http, options);
        }
    };
}