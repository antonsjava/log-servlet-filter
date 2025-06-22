/*
 *
 */
package sk.antons.servlet.filter.formatter;

import java.util.ArrayList;
import java.util.List;
import sk.antons.servlet.filter.HeadersWrapper;

/**
 *
 * @author antons
 */
public abstract class AbstractFormatter implements Formatter {

    protected String protocol;
    @Override public void protocol(String value) { this.protocol = value; }

    protected String method;
    @Override public void method(String value) { this.method = value; }

    protected String path;
    @Override public void path(String value) { this.path = value; }

    protected String query;
    @Override public void query(String value) { this.query = value; }

    protected long time;
    @Override public void time(long value) { this.time = value; }

    protected Throwable error;
    @Override public void error(Throwable value) { this.error = value; }

    protected List<Attr> requestAttrs;
    @Override public void requestAttr(String name, String value) {
        Attr attr = Attr.instance(name, value);
        if(attr != null) {
            if(requestAttrs == null) requestAttrs = new ArrayList<>();
            requestAttrs.add(attr);
        }
    }

    protected HeadersWrapper requestHeaders;
    @Override public void requestHeaders(HeadersWrapper value) { this.requestHeaders = value; }

    protected String requestPayload;
    @Override public void requestPayload(String value) { this.requestPayload = value; }

    protected HeadersWrapper responseHeaders;
    @Override public void responseHeaders(HeadersWrapper value) { this.responseHeaders = value; }

    protected String responsePayload;
    @Override public void responsePayload(String value) { this.responsePayload = value; }

    protected int responseStatus;
    @Override public void responseStatus(int value) { this.responseStatus = value; }


    protected String uri;
    protected String uri() {
        if(uri == null) {
            uri = path + (query == null ? "" : ("?"+query)) ;
        }
        return uri;
    }






    protected static class Attr{
        String name;
        String value;

        protected static Attr instance(String name, String value) {
            if(name == null) return null;
            if(value == null) return null;
            Attr attr = new Attr();
            attr.name = name;
            attr.value = value;
            return attr;
        }

    }


}
