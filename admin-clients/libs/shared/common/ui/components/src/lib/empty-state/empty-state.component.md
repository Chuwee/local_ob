EmptyStateComponent
=
Container that can be used in some pages when we have an empty state
We can send two strings, one as title and one as body or description
We have a ng-container for optionally add a button in the parent to satisfy an specific behavior
Has fxFlex 100% by default and is align to the center

* Box container text Properties

| Name                                      | Description                           |
| ---------------------------------------    -------------------------------------- |
| @Input()title:string                      | Text for the title                    |
| @Input()description:string                | Text for the description       |
| @Input()accesibilityText:string           | Accesibility text for for the image   |
