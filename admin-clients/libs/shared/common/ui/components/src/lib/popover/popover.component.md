Popover Component
==============

Popover directive used with Popover wrapper for displaying a group of form control filters inside it.

The wrapper itself provides a button with the popover functionality when clicked, and 3 action buttons: Remove all filters, Apply and Cancel.

It expects to receive the popover main content inside its component tag in order to do a content projection on the popover element. The content component must be a **FilterComponentWithinPopover**.

This is a **FilterWrapperComponent** that delegate **FilterComponent** functionality to his content component.

* Properties

| Name                                             | Description                                                    | Default               |
|--------------------------------------------------|----------------------------------------------------------------|-----------------------|
| @Input()<br>contentNoPadding: string             | Class to be applied to <mat-card> element                      | *none*                |
| @Output()<br>opened: boolean                     | Emits when popup was opened/closed                             |                       |
| @Input()<br>isTextButtonRange: boolean           | To apply text-button-range class in mat-button-toggle          | false                 |
| @Input()<br>buttonText: string                   | Text showed in the button that opens popup                     | FORMS.OPEN_FILTER_BTN |
| @Input()<br>buttonIconStart: string              | Icon showed at start of button that opens popup                | *none*                |
| @Input()<br>buttonIconEnd: string                | Icon showed at end of button that opens popup                  | filter_list           |
| @Input()<br>textButtonTemplateRef: boolean       | Custom template for button that opens popup                    | *none*                |
| @Output()<br>changesEmitter: EventEmitter<void>  | Emits when apply button is pressed                             |                       |
| @Input()<br>removeButtonText: string             | Text showed in the remove link                                 | *none*                |
| @Input()<br>removeButtonTooltipText: string      | Text showed as tooltip in the remove button outside the popup  | *none*                |
| @Input()<br>removeButton: boolean                | Whether the remove button outside the popup is present         | *none*                |
| @Input()<br>hideRemoveBtn: boolean               | Whether the remove link is showed                              | false                 |
| @Input()<br>isOnRemoveBtnClickEnabled: boolean   | Whether the remove button outside the popup is enabled         | true                  |
| @Input()<br>removeEmitter: EventEmitter<void>    | Emits when remove link or remove button are pressed            |                       |
