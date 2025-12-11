package es.onebox.event.report.annotation;

import es.onebox.core.file.exporter.generator.annotation.FieldBinder;
import es.onebox.event.report.enums.SeasonTicketRenewalsField;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@FieldBinder
@Target(ElementType.FIELD)
public @interface SeasonTicketRenewalFieldBinder {
    SeasonTicketRenewalsField value();
}