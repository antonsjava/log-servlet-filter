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
public class OneLineFormatter extends AbstractFormatter implements Formatter {


    public static OneLineFormatter instance() { return new OneLineFormatter(); }

    @Override
    public void prefixMessage(Appendable appender) throws IOException {
        appender.append(method).append(' ').append(uri()).append(" vvv");
    }

    @Override
    public void requestMessage(Appendable appender) throws IOException {
        Appendable sb = appender;

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
            sb.append(" payload[").append(requestPayload).append("] size: ").append(String.valueOf(requestPayload.length()));
        }

    }

    @Override
    public void responseMessage(Appendable appender) throws IOException {
        Appendable sb = appender;

        sb.append(method).append(' ').append(uri());

        sb.append(" status: ").append(String.valueOf(responseStatus));
        sb.append(" time: ").append(String.valueOf(time));

        if(error != null) {
            sb.append(" error[").append(errorAsString(error)).append(']');
        }

        if(responsePayload != null) {
            sb.append(" payload[").append(responsePayload).append("] size: ").append(String.valueOf(responsePayload.length()));
        }

    }

    protected String errorAsString(Throwable value) {
        StringBuilder sb = new StringBuilder(500);
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
            return OneLineFormatter.instance();
        }

        public static Factory instance() { return new Factory(); }

    }
}
