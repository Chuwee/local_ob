export interface FilterOption {
    label?: string;
    value: string | number | boolean;
    isSelected?: boolean;
}

export interface Filter {
    target: 'tickets' | 'events' | 'transactions' | 'sessions' | 'currency';
    key: string;
    filterName: string;
    filterType: 'checkboxlist' | 'picker' | 'buttons' | 'checkbox' | 'hour_range';
    filterTitle: string;
    filterplaceHolder?: string;
    filterOptions: FilterOption[];
    textToDisplay?: string;
    checked?: boolean;
    isMultiple: boolean;
    value?: any;
}
