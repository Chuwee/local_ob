Language Bar
============

Component to display a list selectable of languages.

Design:
https://app.zeplin.io/project/5cf648fc07ad921abf11e980/screen/5e8c1d89720d0dbbe0178fb2

* Properties

|Name                                              |Description                                                          |Default|
|---                                               |---                                                                  |---    |
|@Input()<br>languages: string[]                   |List of languages to show                                            |*none* |
|@Input()<br>selected: string                      |Language initially selected                                          |*none* |
|@Input()<br>changeGuard: () => Observable<boolean>|Function to validate if is possible to change selected language      |*none* |
|@Output()<br>changed: EventEmitter<string>        |It will be executed after change selected language if guard allows it|       |
