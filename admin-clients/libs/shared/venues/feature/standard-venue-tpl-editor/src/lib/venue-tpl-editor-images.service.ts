import { StdVenueTplsApi, VenueTemplateImage } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { inject, Injectable } from '@angular/core';
import { finalize, Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';
import { VenueTplEditorImage } from './models/venue-tpl-editor-image.model';
import { VenueTplEditorState } from './state/venue-tpl-editor.state';

@Injectable()
export class VenueTplEditorImagesService {

    private readonly _stdVenueTplsApi = inject(StdVenueTplsApi);
    private readonly _venueTplEdState = inject(VenueTplEditorState);

    constructor() {
    }

    loadImages(tplId: number): void {
        this._venueTplEdState.images.setInProgress(true);
        this._stdVenueTplsApi.getVenueTplImages(tplId)
            .pipe(finalize(() => this._venueTplEdState.images.setInProgress(false)))
            .subscribe(images => this._venueTplEdState.images.setValue(images));
    }

    getImages$(): Observable<VenueTplEditorImage[]> {
        return this._venueTplEdState.images.getValue$();
    }

    isInProgress$(): Observable<boolean> {
        return this._venueTplEdState.images.isInProgress$();
    }

    isDirty$(): Observable<boolean> {
        return this._venueTplEdState.images.getValue$()
            .pipe(map(images =>
                images?.some(image => image.create || image.delete)
            ));
    }

    addImage(image: VenueTplEditorImage, undo = false): void {
        this._venueTplEdState.images.getValue$()
            .pipe(take(1))
            .subscribe(images => {
                image.create = !undo;
                if (!images.includes(image)) {
                    images.push(image);
                }
                this._venueTplEdState.images.setValue(images.concat());
            });
    }

    deleteImage(image: VenueTplEditorImage, undo = false): void {
        this._venueTplEdState.images.getValue$()
            .pipe(take(1))
            .subscribe(images => {
                images.find(i => i.id === image.id).delete = !undo;
                this._venueTplEdState.images.setValue(images.concat());
            });
    }

    uploadTemporaryImage(tplId: number, fileName: string, imageBinary: string): Observable<VenueTemplateImage> {
        this._venueTplEdState.images.setInProgress(true);
        return this._stdVenueTplsApi.postVenueTplImage(tplId, {
            filename: fileName, // api case error
            temporary: true,
            image_binary: imageBinary
        })
            .pipe(finalize(() => this._venueTplEdState.images.setInProgress(false)));
    }

    getNewImageName(name: string): Observable<string> {
        return this._venueTplEdState.images.getValue$()
            .pipe(
                take(1),
                map(images => {
                    const extension = this.getFilenameExtension(name);
                    name = this.removeFilenameExtension(name);
                    const imageNames = images.map(image => this.removeFilenameExtension(decodeURIComponent(image.url.split('/').pop())));
                    const startName = name;
                    let index = 0;
                    while (imageNames.includes(name)) {
                        index ++;
                        name = startName + '_' + index;
                    }
                    return name + '.' + extension;
                })
            );
    }

    private getFilenameExtension(filename: string): string {
        return filename.split('.').pop();
    }

    private removeFilenameExtension(filename: string): string {
        const format = this.getFilenameExtension(filename);
        if (format?.length) {
            filename = filename.substring(0, filename.length - format.length - 1);
        }
        return filename;
    }
}
