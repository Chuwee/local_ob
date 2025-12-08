SelectSearch
=========

mat-option content component to give word search functionality to a mat-select.

It uses ngx-mat-select-search that implements the search input, this one applies the filter to the received collection.

needs to receive an observable with the entire collection to filter, giving another observable with the filtered collection.

Adds a null element at the begining of the filtered collection when search input is blank, is the element to clear the selection, can be disabled with requireSelection flag.

* Properties

| Name                                             | Description                                                                                               |
| ------------------------------------------------ | --------------------------------------------------------------------------------------------------------- |
| @Input() options$: Observable<any[]>             | Complete Select collection                                                                                |
| @Input() placeHolderLabel: string                | place holder label                                                                                        |
| @Input() noEntriesFoundLabel: string             | empty filtered list label                                                                                 |
| @Input() searchField: string                     | (optional)The field where the filter must be applied, if not defined will check the item against the word |
| @Input() requireSelection: boolean               | Disables null element to clear the selection                                                              |
| @Output() getFilteredOptions$: Observable<any[]> | the result collection after aplying the filter                                                            |
| @Input() serverSideFetch: boolean                | Allows the server side filtering                                                                          |
| @Output() keyTrigger: EventEmitter               | Emits the keyword selection to the parent component                                                       |
