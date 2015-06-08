package com.matsemann.common;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.FileAppender;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class LoggerUtil {

    private static ConsoleAppender<ILoggingEvent> consoleAppender;
    private static FileAppender<ILoggingEvent> fileAppender;

    static {
        PatternLayoutEncoder ple = new PatternLayoutEncoder();
        ple.setPattern("%-7([%level]) [%date{HH:mm:ss}] %-30(%thread) %-37(%logger{36}:) %msg %n");
        ple.setContext((Context) LoggerFactory.getILoggerFactory());
        ple.start();

        PatternLayoutEncoder ple2 = new PatternLayoutEncoder();
        ple2.setPattern("%-7([%level]) [%date{HH:mm:ss}] %-30(%thread) %-37(%logger{36}:) %msg %n");
        ple2.setContext((Context) LoggerFactory.getILoggerFactory());
        ple2.start();

        consoleAppender = new ConsoleAppender<>();
        consoleAppender.setName("ConsoleAppender");
        consoleAppender.setEncoder(ple);
        consoleAppender.setContext(ple.getContext());
        consoleAppender.start();

        Date now = new Date();

        fileAppender = new FileAppender<>();
        fileAppender.setName("FileAppender");
        fileAppender.setFile("log/log." + (now.getMonth() + 1) + "-" + now.getDate() + "-" + now.getHours() + "-" + now.getMinutes() + "-" + now.getSeconds() + ".txt");
        fileAppender.setEncoder(ple2);
        fileAppender.setContext(ple2.getContext());
        fileAppender.start();
    }

    public static Logger getLogger(Class clazz) {
        Logger logger = (Logger) LoggerFactory.getLogger(clazz);

        logger.addAppender(fileAppender);
        logger.addAppender(consoleAppender);
        logger.setAdditive(false);

        return logger;
    }
}
