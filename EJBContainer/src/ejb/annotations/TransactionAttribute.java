package ejb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD,ElementType.TYPE})
public @interface TransactionAttribute {
	enum Type {
		REQUIRED,
		REQUIRES_NEW,
		NEVER
	};
	Type value();
}
