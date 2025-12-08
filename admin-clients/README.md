# Repository targets (cli commands)

## Development server (outdated section)

Run `npm start` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.
By default `npm start` will point to **PRE environment API**. If you want to set up a different API proxy for the dev server you should prepend
`APP_ENV` environment variable to the command, assigning a valid local environment name to it.
Command examples:
`APP_ENV=local_dev-ned npm start`
`APP_ENV=local_pro npm start`
`...`

If you want to proxy a local API you must set the environment variable `LOCAL_API`to true
Command examples:
`LOCAL_API=true APP_ENV=local_dev-ned npm start`

## Development server with docker (outdated section)

First, build the docker image from the main repo directory for testing purposes

`docker build -f Dockerfile-QA . -t cpanel-client-localtest`

Then, run the image, passing the `ENVIRONMENT` variable (equals the `APP_ENV` variable)

`docker run -ti --rm -e ENVIRONMENT=dev-ned -p 4200:4200 -v ${PWD}:/usr/src/app cpanel-client-localtest`

Open your favorite browser pointing to `http://localhost:4200/`
If you desire to exit the docker image, just press Control+C key combination

## Build (outdated section)

Run `ng build` to build the project for localhost.
Run `APP_ENV={env} npm run build:low`, where `{env}` must be a valid low environment, to **build** the project for low envs (dev-xxx / qa-xxx).
Run `APP_ENV={env} npm run build:high`, where `{env}` must be a valid high environment, to **build** the project for high envs (pre / pro). This command will build the project with the `--prod` flag.
The build artifacts will be stored in the `dist/` directory.

Command examples:

-   Low env: `APP_ENV=dev-ned npm run build:low`
-   High env: `APP_ENV=pre npm run build:high`

## Deploy (outdated section)

Run `APP_BRANCH={branch} APP_ENV={env} npx nx run cpanel-client:deploy`, where `{env}` must be a valid high environment and `{branch}` must be a valid destination folder, like 'default', to **build and deploy** the project to S3 for high envs (pre / pro). This command will build the project with the `--prod` flag and will deploy it including a **Cloudfront invalidation** action.
The build artifacts will be stored in the `dist/` directory.

Command examples:

-   Low env: `APP_BRANCH=default APP_ENV=dev-ned npm run deploy:low`
-   High env: `APP_BRANCH=default APP_ENV=pre npm run deploy:high`

## Running unit tests (outdated section)

Run `nx test` to execute the unit tests via Jest.

## Running Cypress end-to-end tests

Run `APP_ENV=pre npx nx e2e-remote cpanel-client-e2e --ui` to execute the end-to-end tests via [Cypress] in pre environment (some environment is required) and with the Cypress test runner which lets you see the execution and choose the features to test. To execute in snapbox envs simply change pre with the desired low env.

For Windows users you can pass vars if you have NX installed locally
`nx e2e-remote cpanel-client-e2e --ui --env.env=pre`

Remove the `--ui` flag to execute the test in headless mode (no browser will launch).
Also use global properties to pass branch or sp (service preview) params
`APP_BRANCH=feature-OB-12345 APP_ENV=pre SP=feature-ob-54321 npx nx e2e-remote cpanel-client-e2e --ui`

Besides, if you want to run only a set of tests you can do it by passing the feature you want to execute tests on, like this
`SPECS=**/login_spec.js APP_ENV=pre npx nx e2e-remote cpanel-client-e2e`

Another way of executing some tests is using the tags and the grep plugin, such as this
`TAGS=@pgl APP_ENV=pre npx nx e2e-remote cpanel-client-e2e`

Also, Electron is the default browser, but you can run the tests in Chrome, Firefox or MS Edge (they have to be installed in the local system) by setting the parameter like this
`BROWSER=firefox APP_ENV=pre npx nx e2e-remote cpanel-client-e2e`

Learn more about Cypress in their API docs (https://docs.cypress.io/api/api/table-of-contents.html).

## Repository projects dependency graph

Run `nx graph` to see a diagram of the dependencies of your projects.

# Admin-clients version

`info.json` is generated at build time with information about the git commit used for the build

`info.json` is only accessible from the `client-dists` s3 buckets:

PRE: [http://client-dists-pre.s3.amazonaws.com/{app}/{branch}/info.json](http://client-dists-pre.s3.amazonaws.com/cpanel-client/default/info.json)  
PRO: [http://client-dists.s3.amazonaws.com/{app}/{branch}/info.json](http://client-dists.s3.amazonaws.com/cpanel-client/default/info.json)  
where:

-   app: `cpanel-client`, `shi-panel`
-   branch: `default`, `feature-OB-XXXXX`, `bugfix-OB-XXXXX`

# Workspace scaffolding

-   Generate a new lib:
    `npx nx g lib <my-lib-name> --directory=<workspace/directory> --tags=<dimension1,dimension2>`
    -   e.g. `npx nx g lib sales-requests --directory=cpanel-client/features --tags=scope:cpanel,type:feature`

# Configuration HowTo's

## Initial settings when creating a new angular app/lib

-   In `<project path>/project.json` of **apps** add the `deploy`, `info` and `environment` targets inside `targets` object.

    ```
    "deploy": {
      "executor": "@OneboxTM/tools:deploy",
      "options": {
        "environments": "apps/cpanel-client/environments.json"
      }
    },
    "remove": {
      "executor": "@OneboxTM/tools:remove",
      "options": {
        "environments": "apps/cpanel-client/environments.json"
      }
    },
    "info": {
      "executor": "@OneboxTM/tools:info"
    },
    "environment": {
      "executor": "@OneboxTM/tools:env"
    }
    ```

-   To create a shared library using the Nx generator you can run `nx g lib <lib name> --directory=<lib path> --tags=scope:shared,type:<lib type>`

-   If you generate a **buildable/publishable lib** and want to change its name in its `package.json`, make sure both the **path** added
    in the main `tsconfig.base.json` and the **name** in the `package.json` file are **exactly the same**, otherwise
    you'll have TS compiler issues when compiling a lib which depends on this buildable lib.

-   If you want to configure a storybook instance for a specific angular project you have to execute `nx g @nx/angular:storybook-configuration <my-angular-project> --storybook7Configuration` generator and then answer the CLI questions. After project is configured make sure you rename the `.storybook/*.js` files to `.storybook/*.ts`, add the `preview-head.html` and `preview-body.html` as needed, and adjust the `tsconfig.json` `includes` property accordingly. You may also need to add a `styles.scss` file to the project as an entry point for CSS styles of the storybook instance and configure it in `project.json`.

## Working with Onebox Libraries

### Configuring the workspace

First we have to add our onebox repository to the registry

`npm login --scope=@OneboxTM --registry=https://npm.pkg.github.com/`

This will generate automatically a `~/.npmrc ` file like:

```
access=public
@oneboxtds:registry=https://npm.pkg.github.com/
//https://npm.pkg.github.com/:_authToken=ghp_here-goes-your-token
```

To get your token you should go to https://github.com/settings/tokens and generate a new token (classic). Add read, write and delete packages in token scopes.
Then use this token in your npm login

### Import an external library

Just run `npm i @OneboxTM/lib-example` and it will automatically fetch and install our library from our private registry and repository on to your project

### Publish an external library

Simply run `nx lib-example:publish`, this will run the target publish, this target usually uses an Onebox Executor for building and publishing the library automatically.

# Additional documentation (outdated section)

-   ### [Styleguide documentation](libs/shared/common/ui/ob-material/src/lib/README.md)

-   ### [Custom component documentation](libs/shared/common/ui/components/README.md)

-   ### [i18n documentation](docs/i18n.md)
