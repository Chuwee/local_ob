# GooApps - Onebox native app instructions

## Como levantar el proyecto

```
git checkout devel-gooapps
npm login --scope=@OneboxTM --registry=https://npm.pkg.github.com/
    Username: develgooapps
    Password: <GITHUB TOKEN> -> pineado en canal de Slack del proyecto
npm install
nx build native

nx serve native -> Para desplegar en navegador
nx run native:sync:android -> Para sincornizar Android
nx run native:sync:ios -> Para sincronizar iOS
```

Después de esto, abrimos Android Studio o XCode y ya podemos ejecutar el proyecto en el emulador o compilar una APK
