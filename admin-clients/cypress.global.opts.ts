
export const globalOpts = {
    video: false,
    reporter: '../../node_modules/mochawesome',
    reporterOptions: {
        overwrite: false,
        html: false,
        json: true,
        timestamp: 'mmddyyyy_HHMMss'
    },
    watchForFileChanges: true,
    chromeWebSecurity: false,
    defaultCommandTimeout: 6000,
    retries: {
        runMode: 2,
        openMode: 0
    },
    viewportHeight: 774,
    viewportWidth: 1440
};
