/*
 *
 */
package sk.antons.servlet.filter.formatter;

import sk.antons.servlet.filter.HeadersWrapper;


/**
 *
 * @author antons
 */
public class MultiLineFormatter extends AbstractFormatter implements Formatter {


    public static MultiLineFormatter instance() { return new MultiLineFormatter(); }

    @Override
    public String prefixMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(method).append(' ').append(uri());
        return sb.toString();
    }

    @Override
    public String requestMessage() {
        StringBuilder sb = new StringBuilder();

        if(requestAttrs != null) {
            for(Attr requestAttr : requestAttrs) {
                sb.append(' ').append(requestAttr.name).append('(').append(requestAttr.value).append(')');
            }
        }
        sb.append('\n');

        sb.append(method).append(' ').append(uri());
        if(protocol != null) sb.append(' ').append(protocol);
        sb.append('\n');

        if(requestHeaders != null) {
            for(HeadersWrapper.Header header : requestHeaders.headers()) {
                sb.append(header.name()).append(": ").append(header.value()).append('\n');
            }
        }

        sb.append('\n');

        if(requestPayload != null) {
            sb.append(requestPayload);
            sb.append('\n');
        }

        return sb.toString();
    }

    @Override
    public String responseMessage() {
        StringBuilder sb = new StringBuilder();

        sb.append('\n');

        if(protocol != null) sb.append(protocol);
        sb.append(' ').append(responseStatus);
        sb.append('\n');

        if(responseHeaders != null) {
            for(HeadersWrapper.Header header : responseHeaders.headers()) {
                sb.append(header.name()).append(": ").append(header.value()).append('\n');
            }
        }

        sb.append('\n');

        if(error != null) {
            sb.append(" error[").append(errorAsString(error)).append(']');
            sb.append('\n');
        }

        if(responsePayload != null) {
            sb.append(responsePayload);
            sb.append('\n');
        }

        return sb.toString();
    }

    protected String errorAsString(Throwable value) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        while(value != null) {
            if(first) first = false; else sb.append(", causedBy: ");
            sb.append(value.toString());
            value = value.getCause();
        }
        return sb.toString();
    }








    public static class Factory implements FormatterFactory {

        @Override
        public Formatter formatter() {
            return MultiLineFormatter.instance();
        }

        public static Factory instance() { return new Factory(); }

    }
}
