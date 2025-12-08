import { BooleanInput, coerceBooleanProperty } from '@angular/cdk/coercion';
import { CommonModule } from '@angular/common';
import {
    AfterContentInit, AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef,
    Component, computed, ContentChildren, EventEmitter, inject,
    Input, OnDestroy, Output, QueryList, signal, ViewChild
} from '@angular/core';
import { MatTabGroup, MatTabsModule } from '@angular/material/tabs';
import { Subject, takeUntil } from 'rxjs';
import { TabDirective } from './tab.directive';

type TabChange = {
    key: string;
    index: number;
};
type TabsMenuType = 'navigation' | 'language' | 'invisible';
const tabsMenuClassMap: Record<TabsMenuType, string> = {
    navigation: 'ob-navigation-tabs',
    language: 'ob-language-tabs',
    invisible: 'invisible-tabs'
} as const;

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-tabs-menu',
    imports: [CommonModule, MatTabsModule],
    templateUrl: './tabs-menu.component.html'
})
export class TabsMenuComponent implements OnDestroy, AfterContentInit, AfterViewInit {
    private readonly _ref = inject(ChangeDetectorRef);
    private readonly _onDestroy = new Subject<void>();
    private readonly $tabsMenuClasses = signal<{ typeClass: string; overrideClasses: string }>(
        { typeClass: '', overrideClasses: '' }
    );

    private _selectedIndex = 0;
    private _isDynamicHeight: boolean;

    @ViewChild(MatTabGroup) private readonly _matTabGroup: MatTabGroup;

    readonly $tabsMenuStringClasses = computed(() => {
        const { typeClass, overrideClasses } = this.$tabsMenuClasses();
        return `${typeClass} ${overrideClasses}`;
    });

    readonly $disablePagination = computed(() => {
        const { overrideClasses } = this.$tabsMenuClasses();
        return overrideClasses.includes('vertical');
    });

    @ContentChildren(TabDirective) readonly tabs: QueryList<TabDirective>;

    @Input() set type(value: TabsMenuType) {
        this.$tabsMenuClasses.update(values => ({
            ...values,
            typeClass: tabsMenuClassMap[value]
        }));
    }

    @Input() set overrideClasses(value: string) {
        this.$tabsMenuClasses.update(values => ({
            ...values,
            overrideClasses: value
        }));
    }

    @Input() set selectedIndex(value: number) {
        this._selectedIndex = value;
        if (!this._matTabGroup || !this.tabs?.length) { return; }
        this._matTabGroup.selectedIndex = this._selectedIndex;
        this._ref.detectChanges();
    }

    get selectedIndex(): number {
        return this._selectedIndex;
    }

    @Input()
    set dynamicHeight(value: BooleanInput) { this._isDynamicHeight = coerceBooleanProperty(value); }

    get isDynamicHeight(): boolean { return this._isDynamicHeight; }

    @Output() changeEmitter = new EventEmitter<TabChange>();

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    ngAfterContentInit(): void {
        this.tabs.changes
            .pipe(takeUntil(this._onDestroy))
            .subscribe(() => {
                this._matTabGroup.selectedIndex = this._selectedIndex;
                this._ref.detectChanges();
            });
    }

    ngAfterViewInit(): void {
        this.setFirstSelectedIndex();
        this.listenSelectedIndexChange();
    }

    goToInvalidCtrlTab(): boolean {
        return !!this.tabs.find((tab, index) => {
            const isInvalid = tab.ctrl.invalid;
            if (isInvalid) {
                this.selectedIndex = index;
            }
            return isInvalid;
        });
    }

    goToKeyTab(key: string): void {
        this.tabs.find((tab, index) => {
            const found = key === tab.key;
            if (found) {
                this.selectedIndex = index;
            }
            return found;
        });
    }

    private setFirstSelectedIndex(): void {
        if (!this.tabs.length) { return; }
        this._matTabGroup.selectedIndex = this._selectedIndex;
    }

    private listenSelectedIndexChange(): void {
        this._matTabGroup.selectedIndexChange
            .pipe(takeUntil(this._onDestroy))
            .subscribe(selected => {
                this.tabs.forEach((tab, index) => {
                    tab.isActive = index === selected;
                    if (tab.isActive) {
                        this.changeEmitter.emit({
                            index,
                            key: tab.key
                        });
                    }
                });
                this._ref.detectChanges();
            });
    }
}
