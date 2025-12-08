const env = '[[ENV]]'; // replaced by build tool
const branch = '[[BRANCH]]'; // replaced by build tool
const version = '[[VERSION]]'; // replaced by build tool

const oauthUrl = {
    ['pre']: 'https://auth.oneboxtds.net',
    ['pre01']: 'https://auth01.oneboxtds.net',
}

const gatewayUrl = {
    ['pre']: 'https://api.oneboxtds.net',
    ['pre01']: 'https://api01.oneboxtds.net',
}

const wssUrl = {
    ['pre']: 'wss://mgmt-stream.oneboxtds.net:443',
    ['pre01']: 'wss://mgmt-stream01.oneboxtds.net:443',
}

window.__environment = {
    production: false,
    hjid: '',
    env,
    branch,
    version,
    uaTrackingId: 'UA-16191346-22',
    gtmId: 'GTM-KHZQ3F3',
    pingdomCode: 'pa-5f719b95e24f920015000044',
    captchaSiteKey: '6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI',
    wsHost: wssUrl[env] ?? wssUrl.pre,
    gatewayUrl: gatewayUrl[env] ?? gatewayUrl.pre,
    oauthUrl: oauthUrl[env] ?? oauthUrl.pre,
    feverZoneUrl: 'https://partners-dev.feverup.com',
    googleCloudApiKey: 'AIzaSyAS75dU2prT8F0majRquJFNiKeqZonxKNE'
};
