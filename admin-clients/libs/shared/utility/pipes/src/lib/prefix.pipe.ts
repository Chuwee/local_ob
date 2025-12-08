import { InjectionToken, Pipe, PipeTransform, Provider, inject } from '@angular/core';

@Pipe({
    name: 'prefix',
    pure: true,
    standalone: true
})
export class PrefixPipe implements PipeTransform {

    private _prefix = inject(PrefixPipe.PREFIX) || '';

    static readonly PREFIX = new InjectionToken<string>('PrefixPipeProvider');

    static readonly provider = (prefix: string): Provider => ({
        provide: PrefixPipe.PREFIX,
        useValue: prefix
    });

    transform(value: string): string {
        return this._prefix + value;
    }
}