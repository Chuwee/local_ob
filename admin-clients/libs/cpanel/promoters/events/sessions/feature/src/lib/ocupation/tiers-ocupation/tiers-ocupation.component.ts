import {
    EventSessionsService, SessionTiersAvailability
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { VmSessionTiersAvailability } from '../models/vm-session-tiers-availability.model';

@Component({
    selector: 'app-tiers-ocupation',
    templateUrl: './tiers-ocupation.component.html',
    styleUrls: ['./tiers-ocupation.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class TiersOcupationComponent implements OnInit {
    private _triggerNodesChanges = new BehaviorSubject<VmSessionTiersAvailability[]>(null);
    private _triggerNodesChanges$ = this._triggerNodesChanges.asObservable();
    private _tableCopy: VmSessionTiersAvailability[];

    ocupationTable$: Observable<VmSessionTiersAvailability[]>;
    openedRows = new Map<string, string>();
    tableHead = ['price_zone', 'stock_limit', 'sold', 'refunded'];

    constructor(
        private _sessionsService: EventSessionsService
    ) { }

    ngOnInit(): void {
        this.ocupationTable$ = combineLatest([
            this._sessionsService.getSessionTiersAvailability$(),
            this._triggerNodesChanges$
        ])
            .pipe(
                filter(([tiers]) => !!tiers),
                map(([tiers, nodes]) => {
                    if (nodes) {
                        return this.sortByKeyName(this.sortByKeyName(this.sortByLevel(nodes), 'tier'), 'price_type');
                    } else {
                        const nodesTier = this.resetSoldRefund(
                            this.addNodeToKey(this.filterByKeyId(tiers, 'tier'), this.setVmTiers(tiers), 'tier', 2)
                        );
                        const nodesPriceType = this.resetSoldRefund(
                            this.addNodeToKey(this.filterByKeyId(tiers, 'price_type'), nodesTier, 'price_type', 1)
                        );
                        this._tableCopy = nodesPriceType;
                        return this.sortByKeyName(nodesPriceType, 'price_type');
                    }
                })
            );
    }

    toggleTier(tier: VmSessionTiersAvailability): void {
        if (this.openedRows.has(tier.id)) {
            this.openedRows.delete(tier.id);
            this.closeNodes(tier.id);
        } else {
            this.openedRows.set(tier.id, tier.id);
            this.openRow(tier);
        }
    }

    sumAllKey(vmTier: VmSessionTiersAvailability, key: string): number {
        const getAllNodes = (tiers: VmSessionTiersAvailability): any =>
            tiers.node.map(tier => tier.node.length > 0 ? [tier[key], getAllNodes(tier)] : tier[key]);
        const nodesKey = this.flat(getAllNodes(vmTier)) as number[];
        return nodesKey.reduce((acc, cv) => acc + cv, 0);
    }

    private openRow(tier: VmSessionTiersAvailability): void {
        this._triggerNodesChanges.next([...this._tableCopy, ...tier.node]);
        this._tableCopy = [...this._tableCopy, ...tier.node];
    }

    private closeNodes(tierId: string): void {
        const getAllNodesId = (tiers: VmSessionTiersAvailability[]): any =>
            tiers.map(tier => tier.node.length > 0 ?
                this.openedRows.has(tier.id) ? [tier.id, getAllNodesId(tier.node)] : tier.id : tier.id);
        const nodesId = this.flat(getAllNodesId(this._tableCopy.find(tier => tier.id === tierId).node)) as string[];
        nodesId.map(id => this.openedRows.delete(id));
        const newVmSessionTiersAvailability = this._tableCopy.filter(copyTier => !nodesId.includes(copyTier.id));

        this._triggerNodesChanges.next([...newVmSessionTiersAvailability]);
        this._tableCopy = [...newVmSessionTiersAvailability];
    }

    private setVmTiers(tiers: SessionTiersAvailability[]): VmSessionTiersAvailability[] {
        return tiers.map(tier => ({ ...tier, node: [], id: this.guidGenerator(), level: 3 }));
    }

    private sortByKeyName(tiers: VmSessionTiersAvailability[], key: string): VmSessionTiersAvailability[] {
        return tiers.sort((a, b) => a[key].name < b[key].name ? -1 : a[key].name > b[key].name ? 1 : 0);
    }

    private sortByLevel(tiers: VmSessionTiersAvailability[]): VmSessionTiersAvailability[] {
        return tiers.sort((a, b) => a.level < b.level ? -1 : a.level > b.level ? 1 : 0);
    }

    private filterByKeyId(tiers: SessionTiersAvailability[], key: string): SessionTiersAvailability[] {
        return tiers.filter((tier, index) => tiers.findIndex(t => t[key].id === tier[key].id) === index);
    }

    private resetSoldRefund(tiers: VmSessionTiersAvailability[]): VmSessionTiersAvailability[] {
        return tiers.map(tier => ({ ...tier, sold: 0, refunded: 0 }));
    }

    private addNodeToKey(
        groupedTiers: SessionTiersAvailability[],
        vmTiers: VmSessionTiersAvailability[],
        key: string, level: number): VmSessionTiersAvailability[] {
        return groupedTiers.map(groupedTier => ({
            ...groupedTier,
            node: [...vmTiers.filter(tier => tier[key].id === groupedTier[key].id)],
            id: this.guidGenerator(),
            level
        }));
    }

    private guidGenerator(): string {
        return Math.random().toString(36).replace(/[^a-z]+/g, '').substr(2, 10);
    }

    private flat(arr: string[]): string[] | number[] {
        return arr.reduce((flat, toFlatten) => flat.concat(Array.isArray(toFlatten) ? this.flat(toFlatten) : toFlatten), []);
    }
}
