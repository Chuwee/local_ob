# Selection list

The CPanel selection list is composed of 1 angular-material component:

-   [list option](https://material.angular.io/components/list/api#MatListOption)

**Selection list**

-   Selection list

```html
<mat-selection-list fxLayout="row wrap" fxLayoutAlign="space-between" class="ob-grid-select-list">
    <mat-list-option color="primary" [disabled]="disabled">
        <div fxLayout="row" fxLayoutAlign="start center">
            <img *ngIf="image" [src]="src" />
            <mat-icon *ngIf="!image" class="material-icons"> image </mat-icon>
            <span fxFlex>Name</span>
            <mat-icon *ngIf="disabled">done</mat-icon>
        </div>
    </mat-list-option>
    <mat-list-option color="primary" [disabled]="disabled">
        <div fxLayout="row" fxLayoutAlign="start center">
            <img *ngIf="image" [src]="src" />
            <mat-icon *ngIf="!image" class="material-icons"> image </mat-icon>
            <span fxFlex>Name</span>
            <mat-icon *ngIf="disabled">done</mat-icon>
        </div>
    </mat-list-option>
</mat-selection-list>
```

-   Lateral selection list

```html
<mat-selection-list class="ob-lateral-select-list">
    <mat-list-option>
        <span>Name</span>
        <span>Second name</span>
    </mat-list-option>
    <mat-list-option>
        <span>Name 2</span>
        <span>Second name 2</span>
    </mat-list-option>
</mat-selection-list>
```
