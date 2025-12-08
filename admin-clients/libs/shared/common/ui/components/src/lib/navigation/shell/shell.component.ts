import { MenuModeDirective } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyInstancesService } from '@admin-clients/shared/utility/directives';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe, NgTemplateOutlet } from '@angular/common';
import {
    AfterViewInit, ChangeDetectionStrategy, Component, ContentChild, ElementRef, EventEmitter, inject, Input, OnDestroy,
    OnInit, TemplateRef, ViewChild
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatSidenav, MatSidenavContainer, MatSidenavContent } from '@angular/material/sidenav';
import {
    Event, NavigationEnd, NavigationStart, Router,
    RouteConfigLoadEnd, RouteConfigLoadStart, Scroll,
    RouterOutlet
} from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { filter, map, takeUntil, withLatestFrom, debounceTime } from 'rxjs/operators';
import { BreadcrumbsComponent } from '../../breadcrumbs/breadcrumbs.component';
import { BreadcrumbsService } from '../../breadcrumbs/breadcrumbs.service';
import { CollapsedMenuComponent } from '../collapsed-menu/collapsed-menu.component';
import { ExpandedMenuComponent } from '../expanded-menu/expanded-menu.component';
import { Section, SectionList } from '../section-list.models';

const SIDENAV_OPENED_NAME = 'ob-sidenav-opened';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        NgTemplateOutlet, AsyncPipe,
        MatSidenav, MatSidenavContainer, MatSidenavContent,
        RouterOutlet, ExpandedMenuComponent, CollapsedMenuComponent,
        BreadcrumbsComponent, MenuModeDirective
    ],
    selector: 'app-shell',
    styleUrls: ['./shell.component.css'],
    templateUrl: './shell.component.html'
})
export class ShellComponent implements OnInit, AfterViewInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _sectionListRefreshed = new Subject<void>();
    private readonly _breakpointObserver = inject(BreakpointObserver);
    private readonly _router = inject(Router);
    private readonly _ellipsifySrv = inject(EllipsifyInstancesService);
    private readonly _breadcrumbsSrv = inject(BreadcrumbsService);
    private _sidenav: MatSidenav;
    @ViewChild(MatSidenav)
    set sidenav(value: MatSidenav) {
        this._sidenav = value;
        this.manageSidenavOpenedState();
    }

    get sidenav(): MatSidenav {
        return this._sidenav;
    }

    readonly $loading = toSignal(
        this._router.events.pipe(
            filter(Boolean),
            map(event => {
                if (event instanceof RouteConfigLoadStart) {
                    return true;
                } else if (event instanceof RouteConfigLoadEnd) {
                    return false;
                }
                return undefined;
            }),
            filter(e => e !== undefined),
            debounceTime(200)
        )
    );

    @ViewChild('content') content: ElementRef;
    @Input() sectionList$: Observable<SectionList>;
    @Input() expandedMenuLogo: string;
    @Input() collapsedMenuLogo: string;

    readonly isHandset$ = this._breakpointObserver.observe(Breakpoints.Handset)
        .pipe(map(result => result.matches));

    sectionList: SectionList;
    isSidenavInitiallyOpen = localStorage.getItem(SIDENAV_OPENED_NAME) === 'true';

    @ContentChild('topbarTemplate') topbarTemplateRef?: TemplateRef<unknown>;
    sidenavToggled = new EventEmitter<boolean>();
    @ContentChild('reportsTemplate') reportsTemplateRef?: TemplateRef<unknown>;

    constructor() {
        this._breadcrumbsSrv.startListener();
    }

    ngOnInit(): void {
        this.sectionList$
            .pipe(filter(sectionList => !!sectionList))
            .subscribe(sectionList => {
                this.sectionList = sectionList;
                this._sectionListRefreshed.next();
                this.setNavigationSubscription(sectionList);
            });

        this.sidenavToggled
            .pipe(takeUntil(this._onDestroy))
            .subscribe(() => {
                this._sidenav.toggle();
            });
    }

    ngAfterViewInit(): void {
        const contentElement = this.content.nativeElement;
        this._router.events
            .pipe(filter((e: Event): e is Scroll => e instanceof Scroll))
            .subscribe(e => {
                if (e.position) {
                    // backward navigation (trigger on browser navigation backwards)
                    contentElement.scrollTo(0, 0);
                } else if (e.anchor) {
                    // anchor navigation TODO if necessary
                } else {
                    // forward navigation
                    contentElement.scrollTo(0, 0);
                }
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    private manageSidenavOpenedState(): void {
        if (this._sidenav) {
            this._sidenav.openedChange.subscribe(isOpened => {
                localStorage.setItem(SIDENAV_OPENED_NAME, isOpened.toString());
                this.isSidenavInitiallyOpen = isOpened;
                this._ellipsifySrv.refreshInstances();
            });
        }
    }

    private setNavigationSubscription(sectionList: SectionList): void {
        this._router.events
            .pipe(
                map(event => {
                    if (event instanceof NavigationEnd) {
                        if (this.updateActiveSection(sectionList, event.urlAfterRedirects)) {
                            // To update in the view it remakes the array with the same content
                            this.sectionList = Array.from(sectionList);
                        }
                    }
                    return event;
                }),
                withLatestFrom(this.isHandset$),
                takeUntil(this._sectionListRefreshed)
            )
            .subscribe(([event, isHandset]) => {
                if (isHandset && event instanceof NavigationStart && this.sidenav) {
                    this._sidenav.close();
                }
            });
    }

    /** updates isActive section flag if required,
     * returns a boolean that indicates if active section has changed
     */
    private updateActiveSection(sectionList: SectionList, url: string): boolean {
        // If current active section by flag and current section by url are the same, skips the update process
        const currentActiveSection: Section = sectionList.find(section => section.isActive);
        if (currentActiveSection && this.isActiveSection(currentActiveSection, url)) {
            return false;
        } else {
            sectionList.forEach(section => section.isActive = this.isActiveSection(section, url));
            return true;
        }
    }

    private isActiveSection(section: Section, url: string): boolean {
        return this.isCurrentActiveLink(section.link, url) ||
            (section.subsections?.some(subsection => this.isCurrentActiveLink(subsection.link, url)));
    }

    private isCurrentActiveLink(sectionLink: string[], currentUrl: string): boolean {
        return currentUrl && sectionLink && sectionLink.length > 0
            && currentUrl.indexOf(sectionLink[0]) === 0
            /* Takes only the first part of the path, without query params, and check if matches
            the whole string, not only if it contains it */
            && currentUrl.split(/(?=\/)|(?=\s)|(?=\?)/)[0] === sectionLink[0];
    }
}
