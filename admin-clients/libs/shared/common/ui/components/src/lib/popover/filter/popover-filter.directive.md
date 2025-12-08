Popover Filter Directive
==============

Popover directive used with Popover wrapper for displaying a group of form control filters inside it.

The wrapper itself provides a button with the popover functionality when clicked, and 3 action buttons: Remove all filters, Apply and Cancel.
 
It expects to receive the popover main content inside its component tag in order to do a content projection on the popover element. The content component must be a **FilterComponentWithinPopover**.

This is a **FilterWrapperComponent** that delegate **FilterComponent** functionality to his content component.

* Properties

| Name                                                               | Description                                         | Default                                                                                                             |
|--------------------------------------------------------------------|-----------------------------------------------------|---------------------------------------------------------------------------------------------------------------------|
| @Input()<br>isOnRemoveFiltersBtnClickEnabled$: Observable<boolean> | Sets the popover isOnRemoveBtnClickEnabled property | Listens to activatedRoute queryParams and is enabled if there are query params different than startDate and endDate |
| @Input()<br>canChange: boolean                                     | Whether if apply changes or remove can be performed | true                                                                                                                |
