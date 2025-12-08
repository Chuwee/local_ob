const env = '[[ENV]]'; // replaced by build tool
const branch = '[[BRANCH]]'; // replaced by build tool
const version = '[[VERSION]]'; // replaced by build tool

window.__environment = {
    production: true,
    env,
    branch,
    version,
    gatewayUrl: `https://api.oneboxtds.com`,
    oauthUrl: `https://auth.oneboxtds.com`,
    firebase: {
        apiKey: 'AIzaSyDBF9igF1bTft8MKeCw26x83Ga9eWCURvo',
        authDomain: 'pro-ob-panel-app.firebaseapp.com',
        projectId: 'pro-ob-panel-app',
        storageBucket: 'pro-ob-panel-app.appspot.com',
        messagingSenderId: '1039777004307',
        appId: '1:1039777004307:web:089feea55f71b16b4e38e6',
        measurementId: 'G-XJE1MGL8C0'
    }
};
