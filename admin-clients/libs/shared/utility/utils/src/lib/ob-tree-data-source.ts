import { FlatTreeControl } from '@angular/cdk/tree';
import { MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { filter, Observable, OperatorFunction, pipe, take } from 'rxjs';
import { map, tap } from 'rxjs/operators';

/**
 * T: InputDataType, the type that arrives to the transformFunction
 * F: transform function result type
 * K: trackBy result type
 */
export class ObTreeDataSource<T, F, K> extends MatTreeFlatDataSource<T, F, K> {

    private readonly _control: FlatTreeControl<F, K>;

    constructor({ transformFunction, trackBy, getLevel, isExpandable, getChildren }: {
        transformFunction?: (node: T, level: number) => F;
        trackBy: (node: F) => K;
        getLevel: (node: F) => number;
        isExpandable: (node: F) => boolean;
        getChildren: (node: T) => (Observable<T[]> | T[] | undefined | null);
    }) {
        if (!transformFunction) {
            transformFunction = node => node as never;
        }
        const control = new FlatTreeControl(getLevel, isExpandable, { trackBy });
        const flattener = new MatTreeFlattener<T, F, K>(transformFunction, getLevel, isExpandable, getChildren);
        super(control, flattener);
        this._control = control;
    }

    get dataNodes(): F[] {
        return  this._control.dataNodes;
    }

    override disconnect(): void {
        super.disconnect();
    }

    isExpandable(node: F): boolean {
        return this._control.isExpandable(node);
    }

    isExpanded(node: F): boolean {
        return this._control.isExpanded(node);
    }

    expand(node: F): void {
        this._control.expand(node);
    }

    expandAll(): void {
        this._control.expandAll();
    }

    collapse(node: F): void {
        this._control.collapse(node);
    }

    collapseAll(): void {
        this._control.collapseAll();
    }

    toggle(node: F): void {
        this._control.toggle(node);
    }
}

export function mapToTreeDataSource<T, F, K>(
    constructorParam: {
        transformFunction?: (node: T, level: number) => F;
        trackBy: (node: F) => K;
        getLevel: (node: F) => number;
        isExpandable: (node: F) => boolean;
        getChildren: (node: T) => (Observable<T[]> | T[] | undefined | null);
        openRootNodesOnStart?: boolean;
    }
): OperatorFunction<T[], ObTreeDataSource<T, F, K>> {
    const dataSource = new ObTreeDataSource({
        ...constructorParam
    });
    let firstEmission = true;
    return pipe(
        tap(data => dataSource.data = data),
        filter(() => firstEmission),
        tap(() => {
            if (constructorParam.openRootNodesOnStart) {
                dataSource.dataNodes
                    .filter(dataNode => constructorParam.getLevel(dataNode) === 0)
                    .forEach(dataNode => dataSource.expand(dataNode));
            }
            firstEmission = false;
        }),
        map(() => dataSource)
    );
}
