# Styleguide documentation

-   ### Predefined angular-material theme keys

```
    $theme
    |- primary
    |- accent
    |- warn
    |- foreground
    |   |- base
    |   |- divider
    |   |- dividers
    |   |- disabled
    |   |- disabled-button
    |   |- disabled-text
    |   |- hint-text
    |   |- secondary-text
    |   |- icon
    |   |- icons
    |   |- text
    |   |- slider-min
    |   |- slider-off
    |   `- slider-off-active
    |- background
    |   |- status-bar
    |   |- app-bar
    |   |- background
    |   |- hover
    |   |- card
    |   |- dialog
    |   |- disabled-button
    |   |- raised-button
    |   |- focused-button
    |   |- selected-button
    |   |- selected-disabled-button
    |   |- disabled-button-toggle
    |   |- unselected-chip
    |   `- disabled-list-option
    `- is-dark         // bool, whether dark theme or not
```

-   ### [CPanel Color Palettes](palettes/cpanel-palettes.png)

-   ### [Components mapping](components/components-mapping.md)

-   ### Tipography mapping:

| Material name | CSS name                          | Material description                                                                  | Cpanel name     | Style                                                                                                                     |
| ------------- | --------------------------------- | ------------------------------------------------------------------------------------- | --------------- | ------------------------------------------------------------------------------------------------------------------------- |
| display-4     | .mat-display-4                    | Large, one-off header,<br>usually at the top of <br>the page<br>(e.g. a hero header). | Big1            | <span>font-family: Montserrat</span><br>font-size: 45px<br>font-height: 56px<br>font-weight: 700<br>letter-spacing: null  |
| display-3     | .mat-display-3                    | Large, one-off header,<br>usually at the top of <br>the page<br>(e.g. a hero header). | Big2            | <span>font-family: Montserrat</span><br>font-size: 37px<br>font-height: 48px<br>font-weight: 700<br>letter-spacing: null  |
| display-2     | .mat-display-2                    | Large, one-off header,<br>usually at the top of <br>the page<br>(e.g. a hero header). | Big3            | <span>font-family: Montserrat</span><br>font-size: 31px<br>font-height: 42px<br>font-weight: 700<br>letter-spacing: null  |
| display-1     | .mat-display-1                    | Large, one-off header,<br>usually at the top of <br>the page<br>(e.g. a hero header). | Big4            | <span>font-family: Montserrat</span><br>font-size: 26px<br>font-height: 35px<br>font-weight: 700<br>letter-spacing: null  |
| headline      | .mat-h1<br>.mat-headline          | Section heading<br>corresponding to<br>the &lt;h1> tag.                               | H1 Heading      | <span>font-family: Montserrat</span><br>font-size: 22px<br>font-height: 31px<br>font-weight: 700<br>letter-spacing: null  |
| title         | .mat-h2<br>.mat-title             | Section heading<br>corresponding to<br>the &lt;h2> tag.                               | H2 Heading      | <span>font-family: Montserrat</span><br>font-size: 16px<br>font-height: 34px<br>font-weight: 700<br>letter-spacing: null  |
| subheading-2  | .mat-h3<br>.mat-subheading-2      | Section heading<br>corresponding to<br>the &lt;h3> tag.                               | &nbsp;          | <span>font-family: Montserrat</span><br>font-size: 16px<br>font-height: 34px<br>font-weight: 700<br>letter-spacing: null  |
| subheading-1  | .mat-h4<br>.mat-subheading-1      | Section heading<br>corresponding to<br>the &lt;h4> tag.                               | &nbsp;          | <span>font-family: Montserrat</span><br>font-size: 16px<br>font-height: 34px<br>font-weight: 700<br>letter-spacing: null  |
| body-1        | .mat-body<br>.mat-body-1          | Base body text.                                                                       | Body1           | <span>font-family: Nunito Sans</span><br>font-size: 16px<br>font-height: 30px<br>font-weight: 400<br>letter-spacing: null |
| body-2        | .mat-body-strong<br>.mat-body-2   | Bolder body text.                                                                     | Body1(bold-700) | <span>font-family: Nunito Sans</span><br>font-size: 16px<br>font-height: 30px<br>font-weight: 700<br>letter-spacing: null |
| &nbsp;        | .mat-tiny                         | &nbsp;                                                                                | tiny            | <span>font-family: Nunito Sans</span><br>font-size: 10px<br>font-height: 16px<br>font-weight: 400<br>letter-spacing: null |
| caption       | .mat-small<br>.mat-caption        | Smaller body<br>and hint text.                                                        | Caption         | <span>font-family: Nunito Sans</span><br>font-size: 12px<br>font-height: 18px<br>font-weight: 400<br>letter-spacing: null |
| button        | None. Used only<br>in components. | Buttons and anchors.                                                                  | &nbsp;          | <span>font-family: Montserrat</span><br>font-size: 12px<br>font-height: 24px<br>font-weight: 700<br>letter-spacing: null  |
| input         | None. Used only<br>in components. | Form input fields.                                                                    | Body1           | <span>font-family: Nunito Sans</span><br>font-size: 16px<br>font-height: 30px<br>font-weight: 400<br>letter-spacing: null |

MDC migration

| Material name | MDC name        |
| ------------- |-----------------|
| display-4     | mat-headline-1  |
| display-3     | mat-headline-2  |
| display-2     | mat-headline-3  |
| display-1     | mat-headline-4  |
| headline      | mat-headline-5  |
| title         | mat-headline-6  |
| subheading-2  | mat-subtitle-1  |
| subheading-1  | mat-body-1      |
| body-1        | mat-body-2      |
| body-2        | mat-subtitle-2  |
| caption       | mat-caption     |
| button        | mat-button      |
