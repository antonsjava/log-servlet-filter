/*
 *
 */
package sk.antons.servlet.filter.formatter;

import java.io.IOException;
import sk.antons.servlet.filter.HeadersWrapper;

/**
 *
 * @author antons
 */
public interface Formatter {

    void prefixMessage(Appendable appender) throws IOException;
    void requestMessage(Appendable appender) throws IOException;
    void responseMessage(Appendable appender) throws IOException;


    void protocol(String value);
    void method(String value);
    void path(String value);
    void query(String value);
    void time(long value);
    void error(Throwable t);
    void requestAttr(String name, String value);
    void requestHeaders(HeadersWrapper value);
    void requestPayload(String value);
    void responseHeaders(HeadersWrapper value);
    void responsePayload(String value);
    void responseStatus(int value);
}
