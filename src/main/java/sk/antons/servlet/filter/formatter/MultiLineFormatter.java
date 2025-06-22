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

        sb.append(method).append(' ').append(uri());

        if(protocol != null) sb.append(' ').append(protocol);

        if(requestAttrs != null) {
            for(Attr requestAttr : requestAttrs) {
                sb.append(' ').append(requestAttr.name).append('(').append(requestAttr.value).append(')');
            }
        }

        if(requestHeaders != null) {
            sb.append(" headers[");
            boolean first = true;
            for(HeadersWrapper.Header header : requestHeaders.headers()) {
                if(first) first = false; else sb.append(", ");
                sb.append(header.name()).append(": ").append(header.value());
            }
            sb.append(']');
        }

        if(requestPayload != null) {
            sb.append(" payload[").append(requestPayload).append("] size: ").append(requestPayload.length());
        }

        return sb.toString();
    }

    @Override
    public String responseMessage() {
        StringBuilder sb = new StringBuilder();

        sb.append(method).append(' ').append(uri());

        sb.append(" status: ").append(responseStatus);
        sb.append(" time: ").append(time);

        if(error != null) {
            sb.append(" error[").append(errorAsString(error)).append(']');
        }

        if(responsePayload != null) {
            sb.append(" payload[").append(responsePayload).append("] size: ").append(responsePayload.length());
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
