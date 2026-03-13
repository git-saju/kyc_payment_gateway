package com.ecommerce.aspect;

import com.ecommerce.exception.InsufficientStockException;
import com.ecommerce.exception.PaymentFailedException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * ⭐ EXCEPTION HANDLING ASPECT — Global Error Tracking
 *
 * Different exception types ला specifically handle करतो.
 * Real world मध्ये यात email alerts, Slack notifications,
 * database logging, etc. करता येतं.
 *
 * @AfterThrowing → specific exception type match करता येतो!
 */
@Aspect
@Component
@Order(3)
public class ExceptionHandlingAspect {

    @Pointcut("execution(* com.ecommerce.service.*.*(..))")
    public void serviceMethods() {
    }

    /**
     * InsufficientStockException specifically handle करतो
     * throwing = "ex" → parameter name match करतो
     * Method signature मध्ये InsufficientStockException type दिल्यामुळे
     * फक्त हा specific exception type catch होतो!
     */
    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void handleInsufficientStock(JoinPoint joinPoint, InsufficientStockException ex) {
        System.out.println();
        System.out.println("🚨 ━━━━━━━━━━ STOCK ALERT ━━━━━━━━━━");
        System.out.println("🚨 Time: " + LocalDateTime.now());
        System.out.println("🚨 Method: " + joinPoint.getSignature().toShortString());
        System.out.println("🚨 Message: " + ex.getMessage());
        System.out.println("🚨 Action: Notify inventory team to restock!");
        System.out.println("🚨 ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        // Real world मध्ये इथे:
        // - Email send to inventory manager
        // - Slack notification to #stock-alerts channel
        // - Insert into audit_logs table
    }

    /**
     * PaymentFailedException specifically handle करतो
     */
    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void handlePaymentFailed(JoinPoint joinPoint, PaymentFailedException ex) {
        System.out.println();
        System.out.println("💳 ━━━━━━━━━━ PAYMENT FAILURE ALERT ━━━━━━━━━━");
        System.out.println("💳 Time: " + LocalDateTime.now());
        System.out.println("💳 Method: " + joinPoint.getSignature().toShortString());
        System.out.println("💳 Message: " + ex.getMessage());
        System.out.println("💳 Action: Notify payment team, initiate retry mechanism!");
        System.out.println("💳 ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        // Real world मध्ये इथे:
        // - Alert payment gateway team
        // - Log to payment_failures table
        // - Trigger automatic retry after 5 minutes
    }

    /**
     * Generic RuntimeException — catch-all for other errors
     * Note: हा advice वरच्या specific handlers ला match न झालेले
     * RuntimeExceptions handle करतो
     */
    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void handleGenericException(JoinPoint joinPoint, RuntimeException ex) {
        // Skip already handled exceptions
        if (ex instanceof InsufficientStockException || ex instanceof PaymentFailedException) {
            return;
        }
        System.out.println();
        System.out.println("🔴 ━━━━━━━━━━ UNEXPECTED ERROR ━━━━━━━━━━");
        System.out.println("🔴 Time: " + LocalDateTime.now());
        System.out.println("🔴 Method: " + joinPoint.getSignature().toShortString());
        System.out.println("🔴 Error: " + ex.getMessage());
        System.out.println("🔴 Action: Check logs and investigate!");
        System.out.println("🔴 ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}
