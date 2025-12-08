/**
 * Google Analytics (analytics.js)
 *
 * Usage instructions:
 * https://github.com/angulartics/angulartics2/tree/master/src/lib/providers/ga
 *
 * In root module injection:
 *  Angulartics2Module.forRoot(
 *      ga: {
 *          additionalAccountNames: string[];   // array of additional account names
 *          userId: any;                        // see https://support.google.com/analytics/answer/3123662
 *          transport: string;    // see https://developers.google.com/analytics/devguides/collection/analyticsjs/field-reference#transport
 *          anonymizeIp: boolean;
 *      }
 *  )
 */

declare global {
    interface Window {
        ['GoogleAnalyticsObject']: string;
        ga: any;
        grecaptcha: any;
        reCAPTCHACallback: any;
        grecaptchaOnLoad: () => void;
    }
}
declare let ga: (...args: unknown[]) => void;

export abstract class UaLoaderService {
    constructor() { }

    static loadLib(trackingID: string): Promise<any> {
        return new Promise((resolve, reject) => {
            window.GoogleAnalyticsObject = 'ga';
            window.ga = window.ga || ((...args: any[]) => {
                (window.ga.q = window.ga.q || []).push(args);
            });
            window.ga.l = new Date().valueOf();
            const a = document.createElement('script');
            const m = document.getElementsByTagName('script')[0];
            a.async = true;
            a.src = '//www.google-analytics.com/analytics.js';
            a.onload = resolve;
            a.onerror = reject;
            m.parentNode.insertBefore(a, m);
            ga('create', trackingID, 'auto');
        });
    }

}
