/**
 * https://www.pingdom.com/real-user-monitoring/#
 * https://www.pingdom.com/tutorial/add-real-user-monitoring-to-your-site/
 */

export abstract class PingdomRumLoaderService {
    constructor() { }

    static loadLib(pingdomCode: string): Promise<any> {
        return new Promise( (resolve, reject) => {
            const f = document.getElementsByTagName('script')[0];
            const j = document.createElement('script');
            j.async = true;
            j.src = `//rum-static.pingdom.net/${pingdomCode}.js`;
            j.onload = resolve;
            j.onerror = reject;
            f.parentNode.insertBefore(j, f);
        });
    }
}
