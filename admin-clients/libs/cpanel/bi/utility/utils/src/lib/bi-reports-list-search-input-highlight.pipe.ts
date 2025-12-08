import { inject, Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Pipe({
    standalone: true,
    name: 'biReportsListSearchInputHighlight'
})
export class BiReportsListSearchInputHighlightPipe implements PipeTransform {
    private readonly _sanitizer = inject(DomSanitizer);

    transform(value: string, search: string): SafeHtml {
        const regex = new RegExp(search, 'gi');
        const highlighted = value.replace(regex, match => `<strong>${match}</strong>`);
        return this._sanitizer.bypassSecurityTrustHtml(highlighted);
    }
}
