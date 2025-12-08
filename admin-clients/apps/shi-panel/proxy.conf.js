const env = process.env['APP_ENV'] || 'pre01';

const targets = {
    'local': 'http://localhost:8080',
    'pre': 'https://api-shi.oneboxtds.net/',
    'pre01': 'https://api-shi01.oneboxtds.net',
    'pro': 'https://api-shi-mgmt-pro.shi.events/',
};

const target = targets[env];

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
