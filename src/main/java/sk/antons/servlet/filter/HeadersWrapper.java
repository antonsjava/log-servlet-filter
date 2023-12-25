/*
 * Copyright 2023 Anton Straka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sk.antons.servlet.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class HeadersWrapper {
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

    public static HeadersWrapper instance(HttpServletRequest request) {
        if(request == null) return null;
        HeadersWrapper headers = new HeadersWrapper();
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

    public static HeadersWrapper instance(HttpServletResponse response) {
        if(response == null) return null;
        HeadersWrapper headers = new HeadersWrapper();
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
