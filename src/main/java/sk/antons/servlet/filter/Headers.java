/*
 *
 */
package sk.antons.servlet.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author antons
 */
public class Headers {
    List<Header> data = new ArrayList<>();

    public Set<String> names() {
        Set<String> rv = new HashSet<>();
        Set<String> rvc = new HashSet<>();
        for(Header header : data) {
            String name = header.name();
            String namec = name.toLowerCase();
            if(rvc.contains(namec)) continue;
            rvc.add(namec);
            rv.add(name);
        }
        return rv;
    }

    public List<Header> headers() {
        return data;
    }

    public List<Header> headers(String name) {
        List<Header> rv = new ArrayList<>();
        if(name != null) {
            for(Header header : data) {
                if(name.equalsIgnoreCase(header.name())) rv.add(header);
            }
        }
        return rv;
    }

    public Header firstHeader(String name) {
        if(name != null) {
            for(Header header : data) {
                if(name.equalsIgnoreCase(header.name())) return header;
            }
        }
        return null;
    }

    public static Headers instance(HttpServletRequest request) {
        if(request == null) return null;
        Headers headers = new Headers();
        Enumeration<String> en = request.getHeaderNames();
        if(en != null) {
            while(en.hasMoreElements()) {
                String name = en.nextElement();
                Enumeration<String> vals = request.getHeaders(name);
                if(vals != null) {
                    while(vals.hasMoreElements()) {
                        String value = vals.nextElement();
                        headers.data.add(Header.instance(name, value));
                    }
                }
            }
        }
        return headers;
    }

    public static Headers instance(HttpServletResponse response) {
        if(response == null) return null;
        Headers headers = new Headers();
        Collection<String> en = response.getHeaderNames();
        if(en != null) {
            for(String name : en) {
                Collection<String> vals = response.getHeaders(name);
                if(vals != null) {
                    for(String value : vals) {
                        headers.data.add(Header.instance(name, value));
                    }
                }
            }
        }
        return headers;
    }

    public static class Header {
        private String name;
        private String value;
        public String name() { return name; }
        public String value() { return value; }

        public static Header instance(String name, String value) {
            Header h = new Header();
            h.name = name;
            h.value = value;
            return h;
        }
    }
}
