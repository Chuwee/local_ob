/* eslint-disable */
export abstract class HjLoaderService {

    constructor() { }

    static loadLib(hjid: string): Promise<void> {
        if (hjid) {
            return new Promise((resolve, reject) => {
                let h, o, t, j, a, r;
                h = window;
                o = document;
                t = 'https://static.hotjar.com/c/hotjar-';
                j = '.js?sv=';
                h.hj = h.hj || function (...args: unknown[]) {
                    (h.hj.q = h.hj.q || []).push(args);
                };
                h._hjSettings = { hjid, hjsv: 6 };
                a = o.getElementsByTagName('body')[0];
                r = o.createElement('script');
                r.async = 1;
                r.src = t + h._hjSettings.hjid + j + h._hjSettings.hjsv;
                r.onload = resolve;
                r.onerror = reject;
                a.appendChild(r);
            });
        } else {
            return Promise.resolve();
        }
    }
}
