import { Directive, HostListener, Input } from '@angular/core';

@Directive({
    standalone: true,
    selector: '[appDownloadFile]'
})
export class DownloadFileDirective {
    @Input() appDownloadFile: { name: string; data: string };

    constructor() { }

    @HostListener('click', ['$event.target'])
    onClick(): void {
        const url = window.URL.createObjectURL(new Blob([this.appDownloadFile.data]));
        const a = document.createElement('a');
        a.style.display = 'none';
        a.href = url;
        // the filename you want
        a.download = this.appDownloadFile.name;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        a.remove();
    }
}
