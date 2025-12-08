/* eslint-disable */
export default {
    displayName: 'shared-venues-data-access-venue-tpls',
    preset: '../../../../jest.preset.js',
    setupFilesAfterEnv: ['<rootDir>/src/test-setup.ts'],
    globals: {  },
    coverageDirectory: '../../../../coverage/libs/shared/venues/data-access/venue-tpls',
    transform: {
        '^.+\\.(ts|mjs|js|html)$': ['jest-preset-angular', {
            tsconfig: '<rootDir>/tsconfig.spec.json',
            stringifyContentPathRegex: '\\.(html|svg)$',
        }]
    },
    transformIgnorePatterns: ['node_modules/(?!.*\\.mjs$)'],
    snapshotSerializers: [
        'jest-preset-angular/build/serializers/no-ng-attributes',
        'jest-preset-angular/build/serializers/ng-snapshot',
        'jest-preset-angular/build/serializers/html-comment',
    ]
};
