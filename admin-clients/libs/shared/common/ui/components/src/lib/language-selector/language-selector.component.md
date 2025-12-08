Language-selector
=========

List of checkbox and icon stars. Only one of the stars can be selected, which is the default language. The checkboxes represent
the languages selected.

* Properties

| Name                                                | Description                                                                            |
|-----------------------------------------------------|----------------------------------------------------------------------------------------|
| @Input()<br>data: LanguageSelector                  | Input that sets the list of languages to select, the selected ones and the default one |
| @Input()<br>description: string                     | Text as description of the component. It is a help for the final user                  |
| @Input()<br>enableDefaultSelection: boolean = true  | Flag that allows to modify the default language to the final user                      |

* Methods

| getSelectedLanguages()         |
| ------------------------------ |
| returns the selected languages |

| getDefaultLanguage()         |
| ---------------------------- |
| returns the default language |


| scrollIntoView()                    |
|-------------------------------------|
| returns scroll to language selector |
