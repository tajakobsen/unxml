package com.nerdforge.unxml.xml;

import org.slf4j.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LoggingErrorHandler implements ErrorHandler {
    private final Logger logger;

    @Inject
    public LoggingErrorHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
        logger.warn(e.getMessage(), e);
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        logger.error(e.getMessage(), e);
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        logger.error(e.getMessage(), e);
    }
}
