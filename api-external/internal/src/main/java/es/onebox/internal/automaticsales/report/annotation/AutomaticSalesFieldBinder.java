package es.onebox.internal.automaticsales.report.annotation;

import es.onebox.internal.automaticsales.report.enums.AutomaticSalesFields;
import es.onebox.core.file.exporter.generator.annotation.FieldBinder;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@FieldBinder
@Target(ElementType.FIELD)
public @interface AutomaticSalesFieldBinder {
    AutomaticSalesFields value();
}
