import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { providers } from './app/app.providers';
import { Environment } from './environments/environment.type';

// eslint-disable-next-line @typescript-eslint/naming-convention
declare global { interface Window { __environment: Environment } }

bootstrapApplication(AppComponent, { providers });
