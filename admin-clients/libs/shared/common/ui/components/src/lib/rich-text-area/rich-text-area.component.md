Rich Text Area
============

Form Component to introduce rich text in HTML format.

It uses TinyMCE: https://www.tiny.cloud/docs/
It is a Material Form Input with all characteristics.

* Properties

|Name                                                           |Description                                                      |Default|
|---                                                            |---                                                              |---    |
|@Input()<br>placeholder: string                                |Descriptive text to show when no input was introduced            |*none* |
|@Input()<br>required: boolean                                  |To indicate if field is required                                 |false  |
|@Input()<br>disabled: boolean                                  |To indicate if it is disabled                                    |false  |
|@Input()<br>viewCode: boolean                                  |To indicate if we need the option button to show source HTML code|true   |
|@Input()<br>autoresize: boolean &#124; { min_height?: number; max_height?: number } |Enables to autoresize the control based on contents total height. It can receive an object containing min and max height range allowed for auto resize |false  |
|@Input()<br>height: boolean                                    |To indicate an exact height for the control (it must be a number without unit and will be parsed as px) |200px  |
|@Input()<br>toolbar: string &#124; string[] &#124; undefined   |Toolbar that is used in the component                            | 'bold italic underline alignleft aligncenter alignright alignjustify bullist link' + (this._viewCode ? '&#124; code' : '');  |
|@Output()<br>onBlur():  EventEmitter&lt;void&gt;               |To indicate blur event in component                           |
