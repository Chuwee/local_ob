/**
 * Google Tag Manager
 *
 * Usage instructions:
 * https://github.com/angulartics/angulartics2/tree/master/src/lib/providers/gtm
 *
 * In root module injection:
 *  Angulartics2Module.forRoot(
 *      gtm: {
 *          userId: any;    // see https://support.google.com/analytics/answer/3123662
 *      }
 *  )
 */

export abstract class GtmLoaderService {
    constructor() { }

    static loadLib(gtmCode: string, datalayerName = 'dataLayer'): Promise<any> {
        return new Promise((resolve, reject) => {
            window[datalayerName] = window[datalayerName] || [];
            window[datalayerName].push({
                originalLocation: document.location.protocol + '//' +
                    document.location.hostname +
                    document.location.pathname +
                    document.location.search,
                ['gtm.start']: new Date().getTime(),
                event: 'gtm.js'
            });
            const f = document.getElementsByTagName('script')[0];
            const j = document.createElement('script');
            const dl = datalayerName !== 'dataLayer' ? `&l=${datalayerName}` : '';
            j.async = true;
            j.src = `//www.googletagmanager.com/gtm.js?id=${gtmCode}${dl}`;
            j.onload = resolve;
            j.onerror = reject;
            f.parentNode.insertBefore(j, f);
        });
    }
}
