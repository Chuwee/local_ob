/* eslint-disable @typescript-eslint/naming-convention */
import { DialogRef } from '@angular/cdk/dialog';
import { Overlay } from '@angular/cdk/overlay';
import { ComponentPortal } from '@angular/cdk/portal';
import { Injectable, InjectionToken, Injector, inject } from '@angular/core';
import { map, BehaviorSubject, filter, Observable } from 'rxjs';
import { CodeEditorComponent, Options } from './code-editor.component';

declare let monaco: typeof import('monaco-editor');

const cdn = 'https://client-dists-resources.oneboxtds.com/monaco-editor/vs';

export const OB_CODE_EDITOR_CONFIG = new InjectionToken<Options>('OB_CODE_EDITOR_CONFIG');
@Injectable({ providedIn: 'root' })
export class CodeEditorService {

    loaded: boolean = false;
    readonly loadingFinished = new BehaviorSubject<boolean>(false);
    readonly #injector = inject(Injector);
    readonly #overlay = inject(Overlay);

    constructor() {
        this.load();
    }

    init(): Observable<typeof monaco> {
        return this.loadingFinished.asObservable().pipe(
            filter(finished => !!finished),
            map(() => monaco)
        );
    }

    configureMonaco(): void {
        try {
            monaco.languages.css.cssDefaults.setOptions({
                validate: true,
                format: {
                    newlineBetweenSelectors: true
                }
            });
        } catch (error) {
            console.error('Error configuring Monaco:', error);
        }

    }

    openFullScreenEditor(options: Options): DialogRef<CodeEditorComponent> {
        // Create the position strategy
        const positionStrategy = this.#overlay
            .position()
            .global()
            .centerHorizontally()
            .centerVertically();

        // Create the overlay with customizable options
        const overlayRef = this.#overlay.create({
            positionStrategy,
            hasBackdrop: true,
            width: '90vw',
            height: '90vh',
            backdropClass: 'ob-code-editor-overlay-backdrop',
            panelClass: 'ob-code-editor-overlay'
        });

        overlayRef.keydownEvents().subscribe(event => {
            if (event.key === 'Escape') {
                overlayRef.dispose();
            }
        });

        overlayRef.backdropClick().subscribe(() => {
            overlayRef.dispose();
        });

        // Create dialogRef to return
        const dialogRef = new DialogRef<CodeEditorComponent>(overlayRef, {
            disableClose: false,
            closeOnNavigation: true
        });

        // Create the injector
        const injector = Injector.create({
            parent: this.#injector,
            providers: [
                { provide: DialogRef, useValue: dialogRef },
                { provide: OB_CODE_EDITOR_CONFIG, useValue: options }
            ]
        });

        // Create the component portal
        const portal = new ComponentPortal(CodeEditorComponent, null, injector);

        // Attach the portal to the overlay
        overlayRef.attach(portal);

        return dialogRef;
    }

    load(): void {
        if (typeof (window as any).monaco === 'object') {
            this.finishLoading();
            return;
        }

        const onGotAmdLoader: any = () => {
            // load Monaco
            (window as any).require.config({ paths: { vs: `${cdn}` } });
            (window as any).require([
                `vs/editor/editor.main`,
                `vs/editor/editor.main.nls`
            ], () => {
                (window as any).require([
                    `vs/basic-languages/html/html`,
                    `vs/basic-languages/css/css`
                ], () => {
                    this.finishLoading();
                });
            }, () => this.loadingFinished.error('error loading'));
        };

        // load AMD loader, if necessary
        if (!(window as any).require) {
            const loaderScript: HTMLScriptElement = document.createElement('script');
            loaderScript.type = 'text/javascript';
            loaderScript.src = `${cdn}/loader.js`;
            loaderScript.addEventListener('load', onGotAmdLoader);
            loaderScript.addEventListener('error', () => this.loadingFinished.error('error loading'));
            document.body.appendChild(loaderScript);
        } else {
            onGotAmdLoader();
        }
    }

    private finishLoading(): void {
        this.configureMonaco();
        this.loaded = true;
        this.loadingFinished.next(true);
    }

}
