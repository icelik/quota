package icelik.quota.apigw;


import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Limited.class)
public @interface Limit {
	String key();

	String treshold();

	String blockDurationInMilis();
}

