import { Directive, ElementRef, Input, OnDestroy, OnInit, Optional, Renderer2, Self } from '@angular/core';
import { MatMenuTrigger } from '@angular/material/menu';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Directive({
    selector: '[appMoreActionsButton]',
    standalone: true
})
export class TableMoreActionsButtonDirective implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    @Input('appMoreActionsButton') floatActionsCell: boolean;

    constructor(
        private _renderer: Renderer2,
        private _elementRef: ElementRef<HTMLElement>,
        @Self() @Optional() private _matMenuTrigger: MatMenuTrigger
    ) { }

    ngOnInit(): void {
        const moreActionsButton = this._elementRef.nativeElement;
        const actionsCell = this.getParentMatCell(moreActionsButton);
        let isFloatActionsRemoved = false;

        this._matMenuTrigger.menuOpened
            .pipe(takeUntil(this._onDestroy))
            .subscribe(() => {
                //Add hover class to button
                this._renderer.addClass(moreActionsButton, 'more-actions-opened');
                //Remove float-actions-cell to hide action buttons (if necessary)
                if (this.floatActionsCell && actionsCell.classList.contains('float-actions-cell')) {
                    this._renderer.removeClass(actionsCell, 'float-actions-cell');
                    isFloatActionsRemoved = true;
                }
            });

        this._matMenuTrigger.menuClosed
            .pipe(takeUntil(this._onDestroy))
            .subscribe(() => {
                //Remove hover class to button
                this._renderer.removeClass(moreActionsButton, 'more-actions-opened');
                //Adds float-actions-cell to hide action buttons (if necessary). Only add class when we have removed it
                //(Tables have isHandsetOrTablet$ and we don't want the class on certain breakpoints)
                if (this.floatActionsCell && !actionsCell.classList.contains('float-actions-cell')
                    && isFloatActionsRemoved) {
                    this._renderer.addClass(actionsCell, 'float-actions-cell');
                    isFloatActionsRemoved = false;
                }
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    //Get the mat cell that contains the action buttons
    private getParentMatCell(htmlElement: HTMLElement): HTMLElement {
        return htmlElement.nodeName.toLowerCase() === 'mat-cell' ? htmlElement : this.getParentMatCell(htmlElement.parentElement);
    }

}
