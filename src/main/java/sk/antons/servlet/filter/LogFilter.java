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

import java.io.IOException;
import java.security.Principal;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import sk.antons.servlet.filter.builder.LogFilterBuilder;
import sk.antons.servlet.util.HttpServletRequestWrapper;
import sk.antons.servlet.util.HttpServletResponseWrapper;
import sk.antons.servlet.util.JsonFormat;
import sk.antons.servlet.util.ServletRequestWrapper;
import sk.antons.servlet.util.ServletResponseWrapper;
import sk.antons.servlet.util.XmlFormat;

/**
 * Helper class for creating servlet filter for logging requests and responses.
 *
 * It allows to define set of (request condition, response condition and filter configuration)
 *
 * Filter for each request finds first item where request condition
 * (or optional response condition) is valid and pronts info usinf asociated configuration.
 *
 * @author antons
 */
public class LogFilter implements Filter {

    private List<FilterConfSelector> selectors = new ArrayList<>();

    public static LogFilter instance() { return new LogFilter(); }
    public LogFilter selector(FilterConfSelector selector) { this.selectors.add(selector); return this; }


    @Override
    public void init(FilterConfig fc) throws ServletException {}

    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httprequest = null;
        if(request instanceof HttpServletRequest) httprequest = (HttpServletRequest)request;
        HttpServletResponse httpresponse = null;
        if(response instanceof HttpServletResponse) httpresponse = (HttpServletResponse)response;

        boolean somethingEnabled = somethingEnabled();
        FilterConfSelector selector = null;
        if(somethingEnabled) selector = selector(httprequest);

        if ((selector != null) && (!selector.conf().doNothing())) {
            doFilterInternal(wrapRequest(request), wrapResponse(response), selector, chain);
        } else {
            chain.doFilter(request, response);
        }
    }


    private FilterConfSelector selector(HttpServletRequest request) {
        if(request == null) return null;
        for(FilterConfSelector selector : selectors) {
            if(selector.requestCondition() != null) {
                if(selector.requestCondition().check(request)) return selector;
            } else if(selector.responseCondition() != null) {
                return selector;
            }
        }
        return null;
    }

    private boolean somethingEnabled() {
        for(FilterConfSelector selector : selectors) {
            if(selector.conf().messageConsumerEnabled().getAsBoolean()) return true;
        }
        return false;
    }



    private static long requestId = 1;
    protected void doFilterInternal(ServletRequestWrapper request, ServletResponseWrapper response, FilterConfSelector selector, FilterChain filterChain) throws ServletException, IOException {
        FilterConf conf = selector.conf();
        StringBuilder pathbuff = new StringBuilder();
        StringBuilder requestheaderbuff = new StringBuilder();
        StringBuilder requestpayloadbuff = new StringBuilder();
        StringBuilder responseheadersbuff = new StringBuilder();
        StringBuilder responsepayloadbuff = new StringBuilder();
        boolean responseAllowed = true;
        int status = -1;
        int exceprionStatus = -1;
        long id = requestId++;
        long starttime = System.currentTimeMillis();
        try {
            if (conf.messageConsumerEnabled().getAsBoolean()) {
                requestData(request, conf, pathbuff, requestheaderbuff, requestpayloadbuff);
                if((conf.requestStartPrefix() != null) && (selector.responseCondition() == null)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(conf.requestStartPrefix())
                        .append('[').append(id).append(']')
                        .append(pathbuff)
                        .append(" vvv");
                    conf.messageConsumer().accept(sb.toString());
                }
            }
            filterChain.doFilter(request, response);
            if(response instanceof HttpServletResponse) {
                HttpServletResponse httpresponse = (HttpServletResponse)response;
                status = httpresponse.getStatus();
                if(selector.responseCondition() != null) responseAllowed = selector.responseCondition().check(httpresponse);
            }
        } catch(Throwable t) {
            exceprionStatus = 500;
            if (conf.messageConsumerEnabled().getAsBoolean()) {
                StringBuilder sb = new StringBuilder();
                sb.append(conf.responsePrefix())
                    .append('[').append(id).append(']')
                    .append(" ServletException ")
                    .append(pathbuff).append(' ').append(t);
                conf.messageConsumer().accept(sb.toString());
            }
            if(t instanceof IOException) throw (IOException)t;
            else if(t instanceof ServletException) throw (ServletException)t;
            else throw new ServletException(t);
        } finally {
            if (conf.messageConsumerEnabled().getAsBoolean()) {
                if(exceprionStatus > 0) status = exceprionStatus;
                if((status <= 0) || responseAllowed) {
                    responseData(response, conf, responseheadersbuff, responsepayloadbuff);
                    long endtime = System.currentTimeMillis();
                    long time = (endtime - starttime);
                    if(conf.requestPrefix() != null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(conf.requestPrefix())
                            .append('[').append(id).append(']')
                            .append(pathbuff)
                            .append(requestheaderbuff)
                            .append(requestpayloadbuff);
                        conf.messageConsumer().accept(sb.toString());
                    }
                    if(conf.responsePrefix() != null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(conf.responsePrefix())
                            .append('[').append(id).append(']')
                            .append(pathbuff);
                        sb.append(" status: ").append(status);
                        sb.append(" time: ").append(time)
                            .append(responseheadersbuff)
                            .append(responsepayloadbuff);
                        conf.messageConsumer().accept(sb.toString());
                    }
                }
            }
        }
    }

    private static ServletRequestWrapper wrapRequest(ServletRequest request) {
        if (request instanceof ServletRequestWrapper) {
            return (ServletRequestWrapper) request;
        } else if (request instanceof HttpServletRequest) {
            return new HttpServletRequestWrapper((HttpServletRequest)request);
        } else {
            return new ServletRequestWrapper(request);
        }
    }

    private static ServletResponseWrapper wrapResponse(ServletResponse response) {
        if (response instanceof ServletResponseWrapper) {
            return (ServletResponseWrapper) response;
        } else if (response instanceof HttpServletResponse) {
            return new HttpServletResponseWrapper((HttpServletResponse)response);
        } else {
            return new ServletResponseWrapper(response);
        }
    }

    protected void requestData(ServletRequestWrapper request, FilterConf conf, StringBuilder pathbuff, StringBuilder requestheaderbuff, StringBuilder requestpayloadbuff) {
        HttpServletRequestWrapper httprequest = null;
        if(request instanceof HttpServletRequestWrapper) httprequest = (HttpServletRequestWrapper)request;
        if(httprequest != null) {
            String method = httprequest.getMethod();
            String pathString = httprequest.getRequestURI();
            String queryString = httprequest.getQueryString();
            pathbuff.append(' ').append(method).append(' ').append(pathString);
            if(queryString != null) pathbuff.append('?').append(queryString);
        }
        if(conf.identity() && (httprequest != null)) {
            requestheaderbuff.append(" identity(");
            try {
                Principal user = httprequest.getUserPrincipal();
                if(user != null) requestheaderbuff.append(user.getName());
            } catch(Exception e) {
            }
            requestheaderbuff.append(")");
        }
        if((conf.requestHeaderFormatter()!= null) && (httprequest != null)) {
            HeadersWrapper headers = HeadersWrapper.instance(httprequest);
            requestheaderbuff.append(' ').append(request.getProtocol());
            requestheaderbuff.append(" headers(");
            requestheaderbuff.append(conf.requestHeaderFormatter().apply(headers));
            requestheaderbuff.append(")");
        }

        if(conf.requestPayloadFormatter() != null) {
            String text = null;
            try {
                if(request.getInputStream() != null) text = conf.requestPayloadFormatter().apply(request.getInputStream());
            } catch(Exception e) {
                text = "unable to read payload "+e;
            }
            if(text == null) text = "";
            int length = text.length();
            requestpayloadbuff.append(" payload[").append(text).append(']');
            requestpayloadbuff.append(" size: ").append(length);
        }

    }

    protected void responseData(ServletResponseWrapper response, FilterConf conf, StringBuilder responseheadersbuff, StringBuilder responsepayloadbuff) {
        HttpServletResponseWrapper httpresponse = null;
        if(response instanceof HttpServletResponseWrapper) httpresponse = (HttpServletResponseWrapper)response;
        if((conf.responseHeaderFormatter() != null) && (httpresponse != null)) {
            HeadersWrapper headers = HeadersWrapper.instance(httpresponse);
            responseheadersbuff.append(" headers(");
            responseheadersbuff.append(conf.responseHeaderFormatter().apply(headers));
            responseheadersbuff.append(")");
        }

        if(conf.responsePayloadFormatter() != null) {
            String text = null;
            try {
                if(response.getContentInputStream() != null) text = conf.responsePayloadFormatter().apply(response.getContentInputStream());
            } catch(Exception e) {
                text = "unable to read payload "+e;
            }
            if(text == null) text = "";
            int length = text.length();
            responsepayloadbuff.append(" payload[").append(text).append(']');
            responsepayloadbuff.append(" size: ").append(length);
        }

    }

    /**
     * Filter configuration info
     * @return configuration info
     */
    public String configurationInfo() {
        StringBuilder sb = new StringBuilder();
        for(FilterConfSelector selector : selectors) {
            sb.append(selector.configurationInfo());
        }
        return sb.toString();
    }

    /**
     * Builder for log filter
     * @return builder
     */
    public static LogFilterBuilder builder() { return LogFilterBuilder.instance(); }

    /**
     * Helper class for default HttpHeaders convertors.
     */
    public static class Headers {
        /**
         * Converts HttpHeaders as list of (key: value) pairs for all header values.
         * @return
         */
        public static Function<sk.antons.servlet.filter.HeadersWrapper, String> all() {
            return headers -> {
                StringBuffer sb = new StringBuffer();
                sb.append("headers[");
                if(headers != null) {
                    boolean first = true;
                    for(sk.antons.servlet.filter.HeadersWrapper.Header header : headers.headers()) {
                        if(first) first = false;
                        else sb.append(", ");
                        sb.append(header.name()).append(": ").append(header.value());
                    }
                }
                sb.append("]");
                return sb.toString();
            };
        }

        /**
         * Converts HttpHeaders as list of (key: value) pairs for listed header values.
         * @return
         */
        public static Function<sk.antons.servlet.filter.HeadersWrapper, String> listed(final String... name) {
            return headers -> {
                StringBuffer sb = new StringBuffer();
                sb.append("headers[");
                if((headers != null) && (name != null)) {
                    boolean first = true;
                    for(sk.antons.servlet.filter.HeadersWrapper.Header header : headers.headers()) {
                        String key = header.name();
                        boolean match = false;
                        for(String string : name) {
                            if(key.equalsIgnoreCase(string)) {
                                match = true;
                                break;
                            }
                        }
                        if(match) {
                            if(first) first = false;
                            else sb.append(", ");
                            sb.append(key).append(": ").append(header.value());
                        }
                    }
                }
                sb.append("]");
                return sb.toString();
            };
        }
    }

    /**
     * Helper class for default Body to string converters.
     */
    public static class Body {

        /**
         * Converts body payload to string without modification (using utf-8)
         * @return is to string converter
         */
        public static Function<InputStream, String> asIs() { return asIs("utf-8"); }

        /**
         * Converts body payload to string without modification
         * @param encoding
         * @return is to string converter
         */
        public static Function<InputStream, String> asIs(String encoding) {
            return inputStream -> {
                return readTextFromStream(inputStream, encoding);
            };
        }

        /**
         * Helper class for json to string formatter.
         */
        public static class Json {
            private String encoding = "utf-8";
            private String indent;
            private boolean forceOneLine;
            private int cutStringLiterals;

            /**
             * define input for converter
             * @return this
             */
            public static Json instance() { return new Json(); }
            /**
             * define encoding of input (default is utf-8)
             * @param value encoding like utf-8
             * @return this
             */
            public Json encoding(String value) { this.encoding = value; return this; }
            /**
             * define indendation string if result must be indended
             * @param value like "  "
             * @return this
             */
            public Json indent(String value) { this.indent = value; return this; }
            /**
             * true if result must be formatted to one line
             * @param value
             * @return this
             */
            public Json forceOneLine(boolean value) { this.forceOneLine = value; return this; }
            /**
             * if literals are greater than specified value they vill be cuted. (default 0 - no cutting)
             * @param value
             * @return this
             */
            public Json cutStringLiterals(int value) { this.cutStringLiterals = value; return this; }
            /**
             * Create formatter.
             * @return formatter
             */
            public Function<InputStream, String> format() {
                return inputStream -> {
                    try {
                        JsonFormat format = JsonFormat.from(new InputStreamReader(inputStream, encoding));
                        if(cutStringLiterals > 0) format.cutStringLiterals(cutStringLiterals);
                        if(forceOneLine) {
                            format.noindent();
                        } else {
                            format.indent(indent);
                        }
                        return format.toText();
                    } catch(Exception e) {
                        return "unable to read body content " + e;
                    }
                };
            }

        }

        /**
         * Helper class for xml to string formatter.
         */
        public static class Xml {
            private String encoding = "utf-8";
            private String indent;
            private boolean forceOneLine;
            private int cutStringLiterals;

            /**
             * define input for converter
             * @return this
             */
            public static Xml instance() { return new Xml(); }
            /**
             * define encoding of input (default is utf-8)
             * @param value encoding like utf-8
             * @return this
             */
            public Xml encoding(String value) { this.encoding = value; return this; }
            /**
             * define indendation string if result must be indended
             * @param value like "  "
             * @return this
             */
            public Xml indent(String value) { this.indent = value; return this; }
            /**
             * true if result must be formatted to one line
             * @param value
             * @return this
             */
            public Xml forceOneLine(boolean value) { this.forceOneLine = value; return this; }
            /**
             * if literals are greater than specified value they vill be cuted. (default 0 - no cutting)
             * @param value
             * @return this
             */
            public Xml cutStringLiterals(int value) { this.cutStringLiterals = value; return this; }
            /**
             * Create formatter.
             * @return formatter
             */
            public Function<InputStream, String> format() {
                return inputStream -> {
                    try {
                        XmlFormat format = XmlFormat.instance(readTextFromStream(inputStream, encoding), 2000);
                        if(cutStringLiterals > 0) format.cutStringLiterals(cutStringLiterals);
                        if(forceOneLine) {
                            format.forceoneline();
                        } else {
                            format.indent(indent);
                        }
                        return format.format();
                    } catch(Exception e) {
                        return "unable to read body content " + e;
                    }
                };
            }

        }

    }

    private static String readTextFromStream(InputStream is, String encoding) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
            byte[] byteArray = buffer.toByteArray();

            String text = new String(byteArray, encoding);
            return text;
        } catch(Exception e) {
            return "unable to read body content " + e;
        }
    }
}
