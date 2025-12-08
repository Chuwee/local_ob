import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import { HttpClient } from '@angular/common/http';
import { inject, Provider } from '@angular/core';
import { IModuleTranslationOptions, ModuleTranslateLoader } from '@larscom/ngx-translate-module-loader';
import { TranslateLoader } from '@ngx-translate/core';

export function translateHttpLoaderFactory(): Provider {
    return {
        provide: TranslateLoader,
        useFactory: () => {
            const env = inject(ENVIRONMENT_TOKEN);
            const http = inject(HttpClient);
            const url = 'https://client-dists.oneboxtds.com';
            const baseTranslateUrl = `${url}/cpanel-client-translations`;
            const envFolder = env.production ? 'production' : 'staging';
            const cpanelBaseTranslateUrl = `${baseTranslateUrl}/${envFolder}/cpanel`;

            const options: IModuleTranslationOptions = {
                translateError: (error, path) => {
                    console.error('Cannot load literal files: ', { error: String(error), path });
                },
                modules: [
                    { baseTranslateUrl: cpanelBaseTranslateUrl },
                    { moduleName: 'events', baseTranslateUrl: cpanelBaseTranslateUrl },
                    { moduleName: 'channels', baseTranslateUrl: cpanelBaseTranslateUrl },
                    { moduleName: 'countries', baseTranslateUrl },
                    { moduleName: 'regions', baseTranslateUrl },
                    { moduleName: 'languages', baseTranslateUrl },
                    { moduleName: 'currencies', baseTranslateUrl }
                ]
            };
            return new ModuleTranslateLoader(http, options);
        }
    };
}
