type CheckboxCounterSummary = {
    total: number;
    selected: number;
    checked: boolean;
    indeterminate: boolean;
};

export class Counter {

    selected: number;
    total: number;
    ids?: Set<number>;

    constructor(props: { total: number; ids: Set<number> }) {
        Object.assign(this, props);
        this.selected = this.ids.size;
    }

    add(id: number): Counter {
        this.ids.add(id);
        this.selected = this.ids.size;
        return this;
    }

    remove(id: number): Counter {
        this.ids.delete(id);
        this.selected = this.ids.size;
        return this;
    }

    filter(ids: number[]): Counter {
        const values = [...ids, ...this.ids];
        const filtered = values.filter((id, index) => values.indexOf(id) !== index);
        return new Counter({ ids: new Set(filtered), total: ids.length });
    }

    checkbox(): CheckboxCounterSummary {
        return {
            total: this.total,
            selected: this.selected,
            checked: this.selected > 0 && this.total === this.selected,
            indeterminate: this.selected > 0 && this.selected < this.total
        };
    }

}
