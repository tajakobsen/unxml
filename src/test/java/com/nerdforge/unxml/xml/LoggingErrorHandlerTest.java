package com.nerdforge.unxml.xml;

import com.google.inject.Guice;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.inject.Inject;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class LoggingErrorHandlerTest {
    @Bind
    private Logger logger;

    @Inject
    private LoggingErrorHandler errorHandler;

    @Before
    public void before(){
        logger = mock(Logger.class);
        Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);
    }

    @Test
    public void testErrorHandlers() throws SAXException {
        SAXParseException testException = new SAXParseException("test-exception", null);

        errorHandler.warning(testException);
        errorHandler.error(testException);
        errorHandler.fatalError(testException);

        verify(logger).warn(eq("test-exception"), any(SAXParseException.class));
        verify(logger, times(2)).error(eq("test-exception"), any(SAXParseException.class));
    }

}
