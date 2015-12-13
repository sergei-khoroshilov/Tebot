package shenry.tebot.annotation;

import java.lang.annotation.*;

/**
 * Indicates that an annotated method handles telegram updates.
 * Method can have multiple {@link TebotMapping @TebotMapping} annotations.
 */
@Repeatable(TebotMappings.class)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TebotMapping {
    String value();
}
