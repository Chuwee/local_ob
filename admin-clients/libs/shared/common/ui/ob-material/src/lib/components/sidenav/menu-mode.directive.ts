import { getNodeMutations$ } from '@admin-clients/shared/utility/utils';
import { Directionality } from '@angular/cdk/bidi';
import { Directive, ElementRef, forwardRef, HostBinding, inject, Input, OnDestroy, OnInit, Renderer2 } from '@angular/core';
import { MatDrawerMode, MatSidenav, MatSidenavContainer } from '@angular/material/sidenav';
import { Subject, takeUntil } from 'rxjs';

/** The two modes possibles with this directive for the menu: rail and over. Rail is not present in the angular material
 * sidenav component */
type MenuMode = 'rail' | 'over';

/**
 * Expands the mat-sidenav functionality adding the rail mode. Adds to the element mat-sidenav the classes 'rail' when in
 * rail mode, and also 'expanded' and 'collapsed' when the sidenav is opened or closed.
 */
@Directive({
    standalone: true,
    selector: '[appMenuMode]'
})
export class MenuModeDirective implements OnInit, OnDestroy {
    private readonly _renderer2 = inject(Renderer2);
    private readonly _el = inject<ElementRef<HTMLElement>>(ElementRef);
    private readonly _container = inject(forwardRef(() => MatSidenavContainer));
    private readonly _drawer = inject(MatSidenav);
    private readonly _dir = inject(Directionality);

    private readonly _onDestroy = new Subject<void>();
    // It has its correspondent value in the stylesheet of _sidenav*/
    private readonly _expandedWidthRail = '255px';
    // It has its correspondent value in the stylesheet of _sidenav*/
    private readonly _closedWidthRail = '64px';
    private readonly _closedWidthOver = '0';
    private _newMode: MenuMode;
    private _mode: MenuMode;
    private _isInitiallyOpen = false;
    private _containerContent: HTMLElement;

    /** In order to have the class mat-drawer-side when the mode rail is used. The mode rail doesn't exist in angular material
     * at the moment, so the class has to be added this way when this mode is used. If we try to add the class with Renderer2,
     * it won't work. This way is hacky, because we force the class to be present with every change detection in the app.
     */
    @HostBinding('class.mat-drawer-side') setDrawerSide(): boolean {
        return this._mode === 'rail';
    }

    /** Menu mode. When is set, some classes and styles are added and other removed, and the container content margin is corrected */
    @Input() set rail(value: boolean) {
        this._newMode = value ? 'rail' : 'over';
        const lastMode = this._mode;

        // initialization or same mode guard
        if (!lastMode || lastMode === this._newMode) {return;}

        this.setMenuMode(this._newMode);
        this.setMenu(this._newMode);
    }

    /** Only taken into account when the component is initialized */
    @Input() set isInitiallyOpen(value: boolean) {
        this._isInitiallyOpen = value;
    }

    ngOnInit(): void {
        const containerHtml = this._el.nativeElement.parentElement;
        this._containerContent = containerHtml.querySelector('.mat-drawer-content');
        this.initListeners();
        this.initMenuMode(this._newMode, this._isInitiallyOpen, containerHtml);
        this.initMenu(this._newMode, this._isInitiallyOpen);
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    private setMenuMode(mode: MenuMode): void {
        if (mode === 'rail') {
            this._renderer2.addClass(this._el.nativeElement, 'rail');
            if (this._drawer.opened) {
                this.correctContentMargin(this._expandedWidthRail);
            } else {
                this.correctContentMargin(this._closedWidthRail);
                this._renderer2.setStyle(this._el.nativeElement, 'visibility', 'visible');
            }
        } else {
            this._renderer2.removeClass(this._el.nativeElement, 'rail');
            this.correctContentMargin(this._closedWidthOver);
        }
    }

    private setMenu(mode: MenuMode): void {
        this._mode = mode;
        this._drawer.mode = mode as MatDrawerMode;
        this._container.hasBackdrop = mode !== 'rail';
    }

    /** Sets listeners to listen drawer's close and open events when the component is initialized */
    private initListeners(): void {
        this._drawer.closedStart
            .pipe(takeUntil(this._onDestroy))
            .subscribe(() => {
                this._renderer2.removeClass(this._el.nativeElement, 'expanded');
                this._renderer2.addClass(this._el.nativeElement, 'collapsed');
                if (this._mode === 'rail') {
                    this.correctContentMargin(this._closedWidthRail);
                }
            });

        this._drawer.openedStart
            .pipe(takeUntil(this._onDestroy))
            .subscribe(() => {
                this._renderer2.removeClass(this._el.nativeElement, 'collapsed');
                this._renderer2.addClass(this._el.nativeElement, 'expanded');
                if (this._mode === 'rail') {
                    this.correctContentMargin(this._expandedWidthRail);
                }
            });

        // Hack in order to use mat sidenav with rail mode or over mode, in order to show the sidenav when in rail mode
        // and collapsed.
        getNodeMutations$(this._el.nativeElement, { attributes: true, attributeFilter: ['style'] })
            .pipe(takeUntil(this._onDestroy))
            .subscribe(mutationsList => {
                for (const mutation of mutationsList) {
                    if (mutation.target instanceof HTMLElement) {
                        const visibilityValue = window.getComputedStyle(mutation.target as Element).visibility;
                        if (visibilityValue !== 'visible' && this._mode === 'rail') {
                            mutation.target.style.visibility = 'visible';
                        }
                    }
                }
            });
    }

    /** Corrects the container content margin in order to be aligned with the menu width in the actual menu mode */
    private correctContentMargin(width: string): void {
        if (!this._containerContent) {return;}

        if (this.isMarginLeft()) {
            this._renderer2.setStyle(this._containerContent, 'marginLeft',  width);
        } else {
            this._renderer2.setStyle(this._containerContent, 'marginRight',  width);
        }
    }

    private isMarginLeft(): boolean {
        return (this._drawer.position !== 'end' && this._dir && this._dir.value !== 'rtl')
            || (this._drawer.position === 'end' && this._dir && this._dir.value === 'rtl');
    }

    private initMenu(mode: MenuMode, isInitiallyOpen: boolean): void {
        this._mode = mode;
        this._drawer.mode = mode as MatDrawerMode;
        this._container.hasBackdrop = mode !== 'rail';
        // If initially closed, it doesn't emit event to closedStart, and the class collapsed has to be added;
        if (!isInitiallyOpen) {
            this._renderer2.addClass(this._el.nativeElement, 'collapsed');
        }
        this._drawer.opened = isInitiallyOpen;
    }

    private initMenuMode(mode: MenuMode, isInitiallyOpen: boolean, containerHtml: HTMLElement): void {
        if (mode !== 'rail') {return;}

        this._renderer2.addClass(this._el.nativeElement, 'rail');

        if (!containerHtml) {return;}
        // Hack in order to set transition to none in the initialization. In order to avoid animation transitions in the first load in
        // the rail mode, we add a class initial-transition to the mat-sidenav-container, which have transition: none
        this._renderer2.addClass(containerHtml, 'initial-transition');
        // A tick after it sets again the normal state, without the no-transition class, and setting again the margin when it's closed.
        if (isInitiallyOpen) {
            setTimeout(() => this._renderer2.removeClass(containerHtml, 'initial-transition'));
        } else {
            setTimeout(() => {
                this._renderer2.removeClass(containerHtml, 'initial-transition');
                this.correctContentMargin(this._closedWidthRail);
            });
        }
    }
}
