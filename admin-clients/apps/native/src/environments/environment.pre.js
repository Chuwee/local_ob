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

window.__environment = {
    production: false,
    env,
    branch,
    version,
    gatewayUrl: gatewayUrl[env] ?? gatewayUrl.pre,
    oauthUrl: oauthUrl[env] ?? oauthUrl.pre,
    firebase: {
        apiKey: 'AIzaSyD2PeRorIrD8b8h1HR1lIbasKKlpHuK6BE',
        authDomain: 'pre-ob-panel-app.firebaseapp.com',
        projectId: 'pre-ob-panel-app',
        storageBucket: 'pre-ob-panel-app.appspot.com',
        messagingSenderId: '984739019762',
        appId: '1:984739019762:web:1c5a1da09df762807cc96b',
        measurementId: 'G-G2MST7BX9V'
    }
};
