Paginator
=========

Simple previous/next button component to manage pagination, disables prev button on first page and next button on last one.

This is a **FilterComponent** that interacts with **FiltersListService** to apply filters modifications.  

* Properties

|Name|Description|
|---|---|
|@Input()<br>length: number| Total colection elements, used to determine total number of pages with pageSize|
|@Input()<br>pageSize: number| Page size, used to determine total number of pages with length|
|@Output()<br>pageChange: EventEmitter&lt;number&gt;| Object to subscribe to control the page change action, directly or with the firstPage() method| 
|@Output()<br>pageIndex: number| Current paginator page|

* Methods

|firstPage()|
|---|
|Sets and emmit a change page event|

