import { Provider } from '@angular/core';
import { LOGIN_BACKGROUND_URL } from './login.component';

export const provideLoginBackgroundurl = (factory: () => string): Provider => ({
    provide: LOGIN_BACKGROUND_URL,
    useFactory: factory
});
