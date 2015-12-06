package shenry.tebot.annotation;

import java.lang.annotation.*;

@Repeatable(TebotMappings.class)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TebotMapping {
    String value();
}
