const { createGlobPatternsForDependencies } = require("@nx/angular/tailwind");
const { join } = require("path");
const { safeList } = require("../../tailwind-safelist");

/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
        join(__dirname, "src/**/!(*.stories|*.spec).{ts,html}"),
        ...createGlobPatternsForDependencies(__dirname),
    ],
    safelist: safeList,
    theme: {},
    plugins: [],
    corePlugins: [
        'display', 'flex', 'flexBasis', 'flexDirection', 'flexGrow', 'flexShrink', 'flexWrap',
        'order', 'gap', 'justifyContent', 'alignContent', 'alignItems', 'alignSelf',
        'placeContent', 'placeItems', 'placeSelf', 'gridColumn', 'gridColumnStart',
        'gridColumnEnd', 'gridTemplateRows', 'gridTemplateColumns', 'gridRow',
        'gridRowStart', 'gridRowEnd', 'gridAutoColumns', 'gridAutoFlow', 'gridAutoRows'
    ]
};
