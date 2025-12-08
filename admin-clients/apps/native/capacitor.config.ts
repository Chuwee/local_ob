/* eslint-disable @typescript-eslint/naming-convention */
import { CapacitorConfig } from '@capacitor/cli';
import { KeyboardResize, KeyboardStyle } from '@capacitor/keyboard';

let config: CapacitorConfig;

const baseConfig: CapacitorConfig = {
    appId: 'com.onebox.panel',
    appName: 'Onebox',
    webDir: '../../dist/apps/native/browser',
    server: {
        androidScheme: 'https'
    },
    plugins: {
        Keyboard: {
            resize: KeyboardResize.Native,
            style: KeyboardStyle.Dark,
            resizeOnFullScreen: true
        },
        Share: {}
    }
};

switch (process.env.APP_ENV) {
    case 'pre':
        config = {
            ...baseConfig,
            server: {
                hostname: 'mobile-panel.oneboxtds.net'
            }
        };
        break;
    default:
        config = {
            ...baseConfig,
            server: {
                hostname: 'mobile-panel.oneboxtds.com'
            }
        };
        break;
}

export default config;
