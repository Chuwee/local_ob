/**
 * Google Global Site Tag
 *
 * Usage instructions:
 * https://github.com/angulartics/angulartics2/tree/master/src/lib/providers/gst
 *
 * In root module injection:
 *  Angulartics2Module.forRoot(
 *      gst: {
 *          trackingIds: any;
 *          userId?: any;
 *          anonymizeIp?: boolean;
 *          customMap?: { [key: string]: string };
 *      }
 *  )
 */
export abstract class GstLoaderService {
    constructor() { }

    static loadLib(gaMeasurementId: string, datalayerName = 'dataLayer'): Promise<any> {
        return new Promise((resolve, reject) => {
            const f = document.getElementsByTagName('script')[0];
            const j = document.createElement('script');
            j.async = true;
            j.src = `https://www.googletagmanager.com/gtag/js?id=${gaMeasurementId}`;
            j.onload = resolve;
            j.onerror = reject;
            f.parentNode.insertBefore(j, f);

            window[datalayerName] = window[datalayerName] || [];
            function gtag(...args: unknown[]): void { window[datalayerName].push(args); }
            gtag('js', new Date());

            gtag('config', gaMeasurementId);
        });
    }
}
