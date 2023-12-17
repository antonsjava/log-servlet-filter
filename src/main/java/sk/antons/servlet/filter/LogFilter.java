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
import java.util.ArrayList;
import java.util.List;
import sk.antons.servlet.util.HttpServletRequestWrapper;
import sk.antons.servlet.util.HttpServletResponseWrapper;
import sk.antons.servlet.util.ServletRequestWrapper;
import sk.antons.servlet.util.ServletResponseWrapper;

public class LogFilter implements Filter {

    private List<FilterConfSelector> selectors = new ArrayList<>();

    public static LogFilter instance() { return new LogFilter(); }

//    private final RequestLimiter<LogFilter> limiter = new RequestLimiter<LogFilter>(this);
//    private Consumer consumer = new LogConsumer();
//    private ConsumerStatus consumerStatus = new LogConsumerStatus();
//    private Printable printable = new SimplePrintable();
//    private Jsonable jsonable = new SimpleJsonable();
//    private Xmlable xmlable = new SimpleXmlable();
//    private final Set<String> requestHeaderFilter = new HashSet<String>();
//    private final Set<String> responseHeaderFilter = new HashSet<String>();
//    private boolean logRequestHeaders = true;
//    private boolean logRequestPayload = true;
//    private boolean logResponseHeaders = true;
//    private boolean logResponsePayload = true;
//    private boolean forceOneLine = true;
//    private int truncateTo = 0;
//    private int truncateLineTo = 0;
//    private int truncateJsonelementTo = 0;
//    private String requestBeforePrefix = "REQ";
//    private String requestPrefix = "REQ";
//    private String responsePrefix = "RES";
//    private boolean logIdentity = false;

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
                if((conf.requestStartPrefix() != null) && (selector.responseCondition() != null)) {
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
            Headers headers = Headers.instance(httprequest);
            requestheaderbuff.append(' ').append(request.getProtocol());
            requestheaderbuff.append(" headers(");
            requestheaderbuff.append(conf.requestHeaderFormatter().apply(headers));
            requestheaderbuff.append(")");
        }

        if(conf.requestPayloadFormatter() != null) {
            String text = null;
            try {
                text = conf.requestPayloadFormatter().apply(request.getInputStream());
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
            Headers headers = Headers.instance(httpresponse);
            responseheadersbuff.append(" headers(");
            responseheadersbuff.append(conf.responseHeaderFormatter().apply(headers));
            responseheadersbuff.append(")");
        }

        if(conf.responsePayloadFormatter() != null) {
            String text = null;
            try {
                text = conf.responsePayloadFormatter().apply(response.getContentInputStream());
            } catch(Exception e) {
                text = "unable to read payload "+e;
            }
            if(text == null) text = "";
            int length = text.length();
            responsepayloadbuff.append(" payload[").append(text).append(']');
            responsepayloadbuff.append(" size: ").append(length);
        }

    }

}
