import { RangeElement } from '@admin-clients/shared-utility-models';

export function cleanRangesBeforeSave(ranges: RangeElement[]): RangeElement[] {
    return ranges.map((range => {
        range.values = {
            fixed: range.values.fixed ? range.values.fixed : 0,
            percentage: range.values.percentage ? range.values.percentage : 0,
            min: range.values.min ? range.values.min : null,
            max: range.values.max ? range.values.max : null
        };
        return range;
    }));
}

export function resolveRanges(data: { ranges: RangeElement[] }): { from: number; to: number; value: number }[] {
    return data.ranges.map((range: RangeElement, index: number, { length }) => {
        let value;
        if (index === 0) {
            value = {
                from: 0,
                value: range.values.fixed,
                to: range.from !== 0 ? range.from : (data.ranges[index + 1] ? data.ranges[index + 1].from : 0)
            };
        } else {
            value = {
                from: range.from,
                value: range.values.fixed,
                to: index + 1 === length ? 0 : data.ranges[index + 1].from
            };
        }
        return value;
    });
}
