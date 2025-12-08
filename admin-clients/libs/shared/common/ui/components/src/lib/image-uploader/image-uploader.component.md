FileUploaderComponent
=========

Simple image upload component.

The accept attribute for the input is set to 'image/*' in order to filter content types.

The form will return a custom object that handles 'data' attribute which contains the file content in a valid Base64 encoded string

* FileUploaderComponent Properties

| Name                           | Description                                                                                                                                                  | Default |
| ------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------- |
| @Input()<br>disabled:boolean   | Enable/Disable the uploader component                                                                                                                        | false   |
| @Input()<br>legend:string      | Additional text information. Commonly used for specify the file size                                                                                         | null    |
| @Input()<br>heightRatio:number | Height ratio to width. If it not exists, you need to declare the CSS like:<br><br>&.heightRatio55:before {<br>&nbsp;&nbsp;&nbsp;&nbsp;padding-top: 55%;<br>} | 55      |
| @Input()<br>previewerScaleFactor:number | Scale factor applied to the preview | 1      |
| @Input()<br>allowSvg:boolean | Allow SVGs to be loaded in this component, skipping the crop phase and without applying dimensions restrictions | false      |

* File return class values

| Name        | Description       |
| ----------- | ----------------- |
| data        | File data         |
| name        | File name         |
| contentType | File content-type |
