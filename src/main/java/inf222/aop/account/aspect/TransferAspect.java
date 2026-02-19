package inf222.aop.account.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import inf222.aop.account.Account;
import inf222.aop.account.annotation.Transfer;

/* TODO: annotation */
@Aspect
public class TransferAspect {

    /* TODO: add the join point & pointcut & advice */
    @Around("execution(* *(..)) && @annotation(transfer)")
    public Object logTransfer(ProceedingJoinPoint pjp, Transfer transfer) throws Throwable {
        /* TODO */
        Logger logger = LoggerFactory.getLogger(pjp.getTarget().getClass());

        Object[] args = pjp.getArgs();
        Double amount = (Double) args[2];

        if (transfer.internationalTransfer()) {
            String msg = logInternationalTransfer(args);
            logger.atLevel(transfer.value()).log(msg);
        }

        if (amount != null && amount > transfer.LogTransferAbove()) {
            String msg = logTransferAbove(args, transfer.LogTransferAbove());
            logger.atLevel(transfer.value()).log(msg);
        }

        try {
            Object result = pjp.proceed();
            if (transfer.logErrors() && result instanceof Boolean && !((Boolean) result)) {
                MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
                String methodName = methodSignature.getName();
                String[] paramNames = methodSignature.getParameterNames();

                logger.atLevel(transfer.value()).log(logErrors(args, methodName, paramNames));
            }
            return result;

        } catch (Throwable t) {
            if (transfer.logErrors()) {
                MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
                String methodName = methodSignature.getName();
                String[] paramNames = methodSignature.getParameterNames();

                logger.atLevel(transfer.value()).log(logErrors(args, methodName, paramNames));
            }
            throw t;
        }

    }


    private String logInternationalTransfer(Object[] methodArgs) {
        Account from = (Account) methodArgs[0];
        Account to = (Account) methodArgs[1];
        Double amount = (Double) methodArgs[2];
        /* TODO */
        var message = String.format("International transfer from %s to %s, %s %s converted to %s",
                from.getAccountName(),
                to.getAccountName(),
                amount,
                from.getCurrency(),
                to.getCurrency());

        return message;
    }

    private String logTransferAbove(Object[] methodArgs, double value) {
        /* TODO */
        Account from = (Account) methodArgs[0];
        Account to = (Account) methodArgs[1];
        Double amount = (Double) methodArgs[2];

        var message = String.format("Transfer above %s from %s to %s, amount: %s",
                value,
                from.getAccountName(),
                to.getAccountName(),
                amount);
        return message;
    }

    private String logErrors(Object[] methodArgs, String methodName, String[] methodParams) {
        /* TODO */
        Account from = (Account) methodArgs[0];
        Account to = (Account) methodArgs[1];
        Double amount = (Double) methodArgs[2];

        String params = (methodParams == null || methodParams.length == 0)
                ? "from, to, amount"
                : String.join(", ", methodParams);

        var message = String.format("Error in transfer from %s to %s, amount: %s %s, method: %s(%s)",
                from.getAccountName(),
                to.getAccountName(),
                amount,
                from.getCurrency(),
                methodName,
                params);

        return message;
    }
}
