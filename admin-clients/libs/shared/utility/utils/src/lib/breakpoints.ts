import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { inject } from '@angular/core';
import { map, Observable } from 'rxjs';

export const isHandsetOrTablet$ = (): Observable<boolean> => inject(BreakpointObserver)
    .observe([Breakpoints.Handset, Breakpoints.Tablet, Breakpoints.Small])
    .pipe(map(result => result.matches));

export const isHandset$ = (): Observable<boolean> => inject(BreakpointObserver)
    .observe([Breakpoints.Handset])
    .pipe(map(result => result.matches));

export const isHandset = (): boolean => inject(BreakpointObserver).isMatched(Breakpoints.Handset);

