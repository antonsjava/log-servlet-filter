/*
 *
 */
package sk.antons.servlet.filter.formatter;

import sk.antons.servlet.filter.HeadersWrapper;

/**
 *
 * @author antons
 */
public interface Formatter {

    String prefixMessage();
    String requestMessage();
    String responseMessage();


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
