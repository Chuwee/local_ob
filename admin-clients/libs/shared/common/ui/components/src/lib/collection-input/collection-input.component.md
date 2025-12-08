CollectionInputComponent
=
Component for adding elements to an array. It acts as a form control with value type string[].
If the component is a required form control, will check if there is any element in the array and if not, display an error on the internal matInput.
The component accepts a projected content for displaying the array as needed

* CollectionInputComponent @Input Properties

| Name                                  | Description                       |
| --------------------------------------| --------------------------------- |
| @Input() readonly placeholder: string | Placeholder for the input         |
| @Input() readonly buttonLabel: string | Text for the add element button   |
