import { DOCUMENT } from '@angular/common';
import { Directive, EventEmitter, HostListener, Inject, Input, Output } from '@angular/core';

@Directive({
    standalone: true,
    selector: '[appUploadFile]'
})
export class UploadFileDirective {
    @Input() accept?: string;
    @Output() fileChange = new EventEmitter<FileList>();

    constructor(@Inject(DOCUMENT) private _document: Document) { }

    @HostListener('click', ['$event.target'])
    onClick(): void {
        const input = this._document.createElement('input');
        input.style.display = 'none';
        input.type = 'file';
        if (this.accept) {
            input.accept = this.accept;
        }
        input.addEventListener('change', (event: Event): void => {
            const files = (event.target as HTMLInputElement).files;
            this.fileChange.emit(files);
        });
        input.addEventListener('click', (): void => {
            input.remove();
        });
        this._document.body.appendChild(input);
        input.click();
    }
}
