const lowEnvs = new Set(['dev-env']);

let env = process.env['APP_ENV'] || 'pre';

const targets = {
    'local': 'http://localhost:8080',
    'pre': 'https://api.oneboxtds.net',
    'pre01': 'https://api01.oneboxtds.net',
    'pro': 'https://api.oneboxtds.com',
    'default': 'https://api.oneboxtds.net'
};

let target = targets.default;

if (lowEnvs.has(env)) {
    target = targets['low'];
} else if (targets[env]) {
    target = targets[env];
}

const PROXY_CONFIG = {
    '/api': {
        'target': target,
        'changeOrigin': true,
        'secure': false,
        'pathRewrite': {
            '^/api': ''
        }
    }
};

module.exports = PROXY_CONFIG;
