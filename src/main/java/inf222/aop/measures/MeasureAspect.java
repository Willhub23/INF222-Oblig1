package inf222.aop.measures;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* TODO: annotation */

// Alle klasser som inneholder advices må være merket med @Aspect
@Aspect()
public class MeasureAspect {
    private final String regex;
    private final Pattern pattern;

    private final Map<String, Double> toMeter = new HashMap<String, Double>(Map.of(
            "m", 1d,
            "ft", 0.3048d,
            "in", 0.0254d,
            "cm", 0.01d,
            "yd", 0.9144d));

    public MeasureAspect() {
        String elems = String.join("|", toMeter.keySet());
        regex = String.format(".*_(" + elems + ")$");
        pattern = Pattern.compile(regex);
    }

    // Må bruke Object her for @Around

    @Around("get(double inf222.aop.measures.Measures.*)")
    public Object convertToMeters(ProceedingJoinPoint pjp) throws Throwable {
        Object value = pjp.proceed();

        String name = pjp.getSignature().getName();

        Matcher matcher = pattern.matcher(name);
        if (matcher.matches()) {
            String unit = matcher.group(1);

            double converted = (Double) value * toMeter.get(unit);
            return converted;
        }
        return value;

    }


    @Around("set(double inf222.aop.measures.Measures.*) && !cflow(execution(inf222.aop.measures.Measures.new(..)))")
    public Object convertBackToOriginal(ProceedingJoinPoint pjp) throws Throwable {
        String name = pjp.getSignature().getName();

        Matcher matcher = pattern.matcher(name);
        if (matcher.matches()) {
            String unit = matcher.group(1);

            Object inValueMeters = pjp.getArgs()[0];

            double originalValue = (Double) inValueMeters / toMeter.get(unit);

            return pjp.proceed(new Object[]{originalValue});
        }

        return pjp.proceed();

    }

    @Before("set(double inf222.aop.measures.Measures.*)")
    public void checkValue(JoinPoint jp) throws Throwable {
        Object newValObj = jp.getArgs()[0];
        if (newValObj instanceof Double newVal) {
            if (newVal < 0) {
                throw new Error("Illegal modification");
            }
        }


    }

}
