
export interface ResponseAggregatedData {
    overall: ResponseAggregationMetric[];
    type?: {
        agg_value: string;
        agg_metric: ResponseAggregationMetric[];
    }[];
}

export interface ResponseAggregationMetric {
    name: string;
    type: AggregationMetricType;
    value: number;
}

export enum AggregationMetricType {
    sum = 'SUM',
    count = 'COUNT',
    countChildren = 'COUNT_CHILDREN'
}

export interface AggregationMetrics {
    [key: string]: {
        addMetrics?: string[];
        value?: number;
        negated?: boolean;
        isCurrency: boolean;
        isOk?: boolean;
        isError?: boolean;
        isFulfilled?: boolean;
        isPercentage?: boolean;
        isTotal?: boolean;
        isTotalPrice?: boolean;
        headerKey: string;
    };
}

export interface AggregatedMetric {
    metric: string;
    aggData: AggregationMetrics;
}

export function combineAggregatedData(
    currencyAggregatedData: ResponseAggregatedData,
    aggregatedData: ResponseAggregatedData,
    aggregatedMetrics: AggregationMetrics
): AggregatedData {
    const overallResult = currencyAggregatedData.overall.concat(aggregatedData.overall);
    const typeMap = Object.fromEntries(currencyAggregatedData.type.map(type => [type.agg_value, type]));
    const typeResult = aggregatedData.type.map(type => {
        if (typeMap[type.agg_value]) {
            return {
                agg_metric: typeMap[type.agg_value].agg_metric.concat(type.agg_metric),
                agg_value: type.agg_value
            };
        } else {
            return type;
        }
    });

    return new AggregatedData({ overall: overallResult, type: typeResult }, aggregatedMetrics);
}

export class AggregatedData {
    overall: AggregatedMetric;
    types: AggregatedMetric[];

    constructor(aggData: ResponseAggregatedData, aggDataCustom: AggregationMetrics) {
        this.types = [] as AggregatedMetric[];
        aggData.type?.forEach(aggType => {
            this.types.push(this.computeAggregationMetric(aggType.agg_value, aggType.agg_metric, aggDataCustom));
        });
        this.overall = this.computeAggregationMetric('overall', aggData.overall, aggDataCustom);
    }

    private computeAggregationMetric(
        metric: string, aggMetrics: ResponseAggregationMetric[], aggDataCustom: AggregationMetrics
    ): AggregatedMetric {
        const aggData = {};
        Object.keys(aggDataCustom).forEach(key => {
            aggData[key] = {
                headerKey: aggDataCustom[key].headerKey,
                isCurrency: aggDataCustom[key].isCurrency,
                value: aggMetrics.filter(aggMetric => aggDataCustom[key].addMetrics.includes(aggMetric.name))
                    .map(ar => ar.value).reduce((val, val2) => val + val2, 0) * (aggDataCustom[key].negated ? -1 : 1)
            };
        });
        return { metric, aggData };
    }
}

