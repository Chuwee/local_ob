ContextNotificationComponent
=
Context message to include in any other component.
By default it has no text or action buttons, only contains size logic, and state icon.
Has a size of 600px or 80% on fxFlex.lt-md devices. 

* MessageDialogConfig Properties

| Name                                    | Description  |
| --------------------------------------- | ------------ |
| @Input()contextType:MessageType(string) | Icon to show |

* MessageType enum values

| Name    | Material icon | Description                                                                 |
| ------- | ------------- | --------------------------------------------------------------------------- |
| warn    | cancel        | For error messages in orange-red color, cross symbol                        |
| alert   | warning       | Warnings, would need a second button to cancel operation, yellow ! triangle |
| info    | info          | Info messages, blue "i" in circle                                           |
| success | check_circle  | Success messages, green check in circle                                     |
