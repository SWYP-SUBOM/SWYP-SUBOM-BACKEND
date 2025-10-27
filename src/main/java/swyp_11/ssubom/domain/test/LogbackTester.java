package swyp_11.ssubom.domain.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class LogbackTester implements CommandLineRunner {

    private static final Logger appLogger = LoggerFactory.getLogger(LogbackTester.class);

    private static final Logger hibernateLogger = LoggerFactory.getLogger("org.hibernate.SQL");

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n--- RUNNING LOGBACK TEST ---");

        // Logs from your application
        appLogger.debug("This is a DEBUG message from 'ssubom'.");
        appLogger.info("This is an INFO message from 'ssubom'.");
        appLogger.warn("This is a WARN message from 'ssubom'.");

        // Logs from a simulated 'hibernate' logger
        hibernateLogger.debug("This is a DEBUG message from 'Hibernate'.");
        hibernateLogger.info("This is an INFO message from 'Hibernate'.");
        hibernateLogger.warn("This is a WARN message from 'Hibernate'.");

        System.out.println("--- LOGBACK TEST COMPLETE ---\n");
    }
}