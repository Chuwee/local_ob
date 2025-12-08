import { WsMsgStatus } from '@admin-clients/shared/core/data-access';
import { ChangeDetectorRef, Injectable } from '@angular/core';
import { Observable, take } from 'rxjs';
import { SessionGenerationStatus } from './models/session-generation-status.enum';
import { SessionGroupStatus } from './models/session-group-status.enum';
import { SessionWrapper } from './models/session-wrapper.model';
import { GroupsGenerationStatus } from './models/sessions-list/groups-generation-status.model';
import { VmSessionsGroup } from './models/sessions-list/vm-sessions-group.model';

@Injectable()
export class SessionsListCountersService {
    private _groupsGenerationStatus: GroupsGenerationStatus = {};

    constructor() { }

    resetGroupsStatusCounters(): void {
        this._groupsGenerationStatus = {};
    }

    getGroupsStatusCounters(): GroupsGenerationStatus {
        return this._groupsGenerationStatus;
    }

    generateGroupedStatusCounters(groupKey: string, sessionId: number, wsMsgStatus: WsMsgStatus): void {
        if (!this._groupsGenerationStatus[groupKey]) {
            this._groupsGenerationStatus[groupKey] = {
                inProgress: [],
                success: [],
                error: []
            };
        }

        switch (wsMsgStatus) {
            case WsMsgStatus.inProgress:
                this._groupsGenerationStatus[groupKey].inProgress.push(sessionId);
                break;
            case WsMsgStatus.done:
                this._groupsGenerationStatus[groupKey].inProgress = this._groupsGenerationStatus[groupKey].inProgress
                    .filter(id => id !== sessionId);
                this._groupsGenerationStatus[groupKey].error = this._groupsGenerationStatus[groupKey].error
                    .filter(id => id !== sessionId);
                this._groupsGenerationStatus[groupKey].success.push(sessionId);
                break;
            case WsMsgStatus.error:
                this._groupsGenerationStatus[groupKey].inProgress = this._groupsGenerationStatus[groupKey].inProgress
                    .filter(id => id !== sessionId);
                this._groupsGenerationStatus[groupKey].error.push(sessionId);
                break;
        }
    }

    processGroupStatusCounters(
        sessionGroup: VmSessionsGroup,
        startDate: string
    ): void {
        const generationCounters = this._groupsGenerationStatus[startDate];
        if (generationCounters?.inProgress.length > 0) {
            sessionGroup.status = SessionGroupStatus.inProgress;
        } else if (generationCounters?.error.length > 0 && generationCounters?.error.length === sessionGroup.sessions.length) {
            sessionGroup.status = SessionGroupStatus.error;
        } else if (generationCounters?.error.length > 0) {
            sessionGroup.status = SessionGroupStatus.partialError;
        } else if (generationCounters?.success.length > 0) {
            sessionGroup.status = SessionGroupStatus.success;
        }
    }

    processGroupSessionsStatusCounters(sessionsGroup: VmSessionsGroup): void {
        const inProgress = this._groupsGenerationStatus[sessionsGroup.startDate].inProgress;
        const success = this._groupsGenerationStatus[sessionsGroup.startDate].success;
        const error = this._groupsGenerationStatus[sessionsGroup.startDate].error;
        sessionsGroup.sessions.forEach(sw => {
            if (inProgress.includes(sw.session.id)) {
                sw.session.generation_status = SessionGenerationStatus.inProgress;
                sw.isActiveFromInProgress = false;
            } else if (success.includes(sw.session.id)) {
                sw.session.generation_status = SessionGenerationStatus.active;
                sw.isActiveFromInProgress = true;
            } else if (error.includes(sw.session.id)) {
                sw.session.generation_status = SessionGenerationStatus.error;
            }
        });
    }

    getTotalCounters(total: number): { inProgress: number; success: number; error: number; total: number } {
        const totalCounters = {
            inProgress: 0,
            success: 0,
            error: 0,
            total
        };
        Object.values(this._groupsGenerationStatus).forEach(groupCounters => {
            totalCounters.inProgress += groupCounters.inProgress.length;
            totalCounters.success += groupCounters.success.length;
            totalCounters.error += groupCounters.error.length;
        });

        return totalCounters;
    }

    // Desde aquí todo son helpers para los contadores de sesiones seleccionadas:

    updateGroupsSelectedSessionsCounters(
        sessionsGroups$: Observable<VmSessionsGroup[]>, selectAll: boolean, ref: ChangeDetectorRef, groupKey?: string
    ): void {
        sessionsGroups$
            .pipe(take(1))
            .subscribe(sessionsGroups => {
                if (selectAll) {
                    sessionsGroups.forEach(sessionsGroup => sessionsGroup.selectedSessions = sessionsGroup.totalSessions);
                } else {
                    sessionsGroups.forEach(sessionsGroup => {
                        if (sessionsGroup.startDate === groupKey) {
                            sessionsGroup.selectedSessions = 1;
                        } else {
                            sessionsGroup.selectedSessions = 0;
                        }
                    });
                }
                ref.markForCheck();
            });
    }

    updateIndividualSelectedSessionsCounter(
        sessionsGroups$: Observable<VmSessionsGroup[]>, added: boolean, ref: ChangeDetectorRef, groupKey: string
    ): void {
        sessionsGroups$
            .pipe(take(1))
            .subscribe(sessionsGroups => {
                const sessionsGroup = sessionsGroups.find(sessionsGroup => sessionsGroup.startDate === groupKey);
                if (!sessionsGroup) return;
                if (added) {
                    sessionsGroup.selectedSessions++;
                } else {
                    sessionsGroup.selectedSessions--;
                }
                ref.markForCheck();
            });
    }

    updateGroupsSelectedSessionsCountersFromList(
        sessionsGroups$: Observable<VmSessionsGroup[]>,
        selectedSessions: SessionWrapper[],
        getGroupKey: (date: string) => string,
        ref: ChangeDetectorRef
    ): void {
        sessionsGroups$
            .pipe(take(1))
            .subscribe(sessionsGroups => {
                const selectedSessionsGroupCounters = new Map<string, number>();
                selectedSessions.forEach(sw => {
                    const groupKey = getGroupKey(sw.session.start_date);
                    if (selectedSessionsGroupCounters.has(groupKey)) {
                        selectedSessionsGroupCounters.set(groupKey, selectedSessionsGroupCounters.get(groupKey) + 1);
                    } else {
                        selectedSessionsGroupCounters.set(groupKey, 1);
                    }
                });

                sessionsGroups.forEach(sessionsGroup => {
                    if (selectedSessionsGroupCounters.has(sessionsGroup.startDate)) {
                        sessionsGroup.selectedSessions = selectedSessionsGroupCounters.get(sessionsGroup.startDate);
                    } else {
                        sessionsGroup.selectedSessions = 0;
                    }
                });

                ref.markForCheck();
            });
    }
}
