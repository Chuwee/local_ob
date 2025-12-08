MessageDialogComponent
=
Simple message dialog, works with 4 input params joined in an object (MessageDialogConfig).
To use it only needs...
matDialog.open(MessageDialogComponent, [MessageDialogConfig]);
This return a refence to handle the closure of the dialog (beforeClosed() observer).
Dialog is composed by tittle, content(only text) and footer (ok button for the moment).

* MessageDialogConfig Properties

|Name|Description|
|---|---|
|@Input()type:MessageDialogType| Style and tittle style|
|@Input()size:MessageDialogSize| Width of the dialog|
|@Input()title:string| Dialog tittle, uses mat-body-2| 
|@Input()message:string| Content message, body|
|@Input()actionLabel?:string| Optional label for main action button|
|@Input()showCancelButton?:boolean| Optional show cancel button, default hide|

* MessageType enum values

|Name|Material icon|Description|
|---|---|---|
|warn|cancel|For error messages in orange-red color, cross symbol|
|alert|warning|Warnings, would need a second button to cancel operation, yellow ! triangle|
|info|info|Info messages, blue "i" in circle|
|success|check_circle|Success messages, green check in circle|

* MessageDialogSize enum values

|Name|Value|
|---|---|
|EXTRA_SMALL (xs)|260px|
|SMALL (s)|360px|
|MEDIUM (m)||
|LARGE (l)||
|EXTRA_LARGE (xl)||
