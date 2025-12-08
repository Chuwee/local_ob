import { createRequire } from "node:module";
import { dirname, join } from "node:path";
import { StorybookConfig } from "@storybook/angular";

const require = createRequire(import.meta.url);

const config: StorybookConfig = {
    stories: ["../**/src/lib/**/!(_).@(mdx|stories.@(js|jsx|ts|tsx))"],
    addons: [getAbsolutePath("@storybook/addon-docs")],

    framework: {
        name: getAbsolutePath("@storybook/angular"),
        options: {}
    },

    staticDirs: [{ from: '../../../../assets', to: 'assets' }]
};

export default config;

// To customize your webpack configuration you can use the webpackFinal field.
// Check https://storybook.js.org/docs/react/builders/webpack#extending-storybooks-webpack-config
// and https://nx.dev/packages/storybook/documents/custom-builder-configs

function getAbsolutePath(value: string): any {
    return dirname(require.resolve(join(value, "package.json")));
}
