import { inject, Pipe, PipeTransform } from '@angular/core';
import { map, Observable } from 'rxjs';
import { CustomResourcesService } from './custom-resources.service';

@Pipe({
    standalone: true,
    name: 'customResourcesPipe$'
})
export class CustomResourcesPipe implements PipeTransform {
    private readonly _customResourcesSrv = inject(CustomResourcesService);

    transform(resource: string): Observable<string> {
        return this._customResourcesSrv.customResources$
            .pipe(map(resourcesPath => {
                if (resource === 'logo') {
                    return resourcesPath.logo_url ?? 'assets/logo-ob.svg';
                }

                if (resource === 'tiny') {
                    return resourcesPath.tiny_url ?? 'assets/logo-ob-tiny.svg';
                }
                return '';
            }));
    }
}
