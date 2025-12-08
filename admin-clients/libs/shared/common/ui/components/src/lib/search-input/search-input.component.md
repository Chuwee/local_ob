Search Input
============

Text input with search and cancel buttons wrapped in a custom component. It reacts to search button click or input Enter key press by emitting an output valueChanged event, with an associated debounce time of 300ms

This is a **FilterComponent** that interacts with **FiltersListService** to apply filters modifications.

* Properties

|Name                                               |Description                                                                                            |
|---                                                |---                                                                                                    |
|@Input() initValue: string                         |Property for setting an external value to search input (according to URL search criteria)              |
|@Input() placeholder: string                       |Property for setting an external placeholder to search input                                           |
|@Output()<br>valueChanged: EventEmitter\<string>   |Event that is emitted when search button is clicked or Enter key is pressed inside the input field.    |
