package annotations;

import javax.persistence.EnumType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ColumnDescription {
    String name() default "";

    String callFunction() default "";

    boolean ignore() default false;

    Class<?> enumeration() default Object.class;

    EnumType enumType() default EnumType.ORDINAL;


}
