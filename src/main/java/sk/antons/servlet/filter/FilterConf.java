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

import java.io.InputStream;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Log filter instance configuration.
 *
 * @author antons
 */
public class FilterConf implements Cloneable {

    private Consumer<String> messageConsumer;
    private BooleanSupplier messageConsumerEnabled;
    private String requestStartPrefix = "REQ";
    private String requestPrefix = "REQ";
    private String responsePrefix = "RES";
    private boolean identity = false;
    private boolean remoteHost = false;
    private boolean remoteAddr = false;
    private boolean doNothing = false;
    private Function<HeadersWrapper, String> requestHeaderFormatter;
    private Function<InputStream, String> requestPayloadFormatter;
    private Function<HeadersWrapper, String> responseHeaderFormatter;
    private Function<InputStream, String> responsePayloadFormatter;

    public Consumer<String> messageConsumer() { return messageConsumer; }
    public BooleanSupplier messageConsumerEnabled() { return messageConsumerEnabled; }
    public String requestStartPrefix() { return requestStartPrefix; }
    public String requestPrefix() { return requestPrefix; }
    public String responsePrefix() { return responsePrefix; }
    public boolean identity() { return identity; }
    public boolean remoteAddr() { return remoteAddr; }
    public boolean remoteHost() { return remoteHost; }
    public boolean doNothing() { return doNothing; }
    public Function<HeadersWrapper, String> requestHeaderFormatter() { return requestHeaderFormatter; }
    public Function<InputStream, String> requestPayloadFormatter() { return requestPayloadFormatter; }
    public Function<HeadersWrapper, String> responseHeaderFormatter() { return responseHeaderFormatter; }
    public Function<InputStream, String> responsePayloadFormatter() { return responsePayloadFormatter; }

    /**
     * create default instance of configuration.
     * @return new instance
     */
    public static FilterConf instance() { return new FilterConf(); }
    /**
     * Message consumer for log filter messages
     * @param value consumer like (m) -> log.debug(m)
     * @return this
     */
    public FilterConf messageConsumer(Consumer<String> value) { this.messageConsumer = value; return this; }
    /**
     * Determines if logging is enabled
     * @param value like () -> log.isDebugEnabled()
     * @return this
     */
    public FilterConf messageConsumerEnabled(BooleanSupplier value) { this.messageConsumerEnabled = value; return this; }
    /**
     * Prefix for request displayed on request start (default REQ)
     * @param value prefix like REQ
     * @return this
     */
    public FilterConf requestStartPrefix(String value) { this.requestStartPrefix = value; return this; }
    /**
     * Prefix for request displayed on request end (default REQ)
     * @param value prefix like REQ
     * @return this
     */
    public FilterConf requestPrefix(String value) { this.requestPrefix = value; return this; }
    /**
     * Prefix for response displayed on request end (default RES)
     * @param value prefix like RES
     * @return this
     */
    public FilterConf responsePrefix(String value) { this.responsePrefix = value; return this; }
    /**
     * Add user identity to request info
     * @param value true if identity should be displayed
     * @return this
     */
    public FilterConf identity(boolean value) { this.identity = value; return this; }
    /**
     * Add remoteHost to request info
     * @param value true if remoteHost should be displayed
     * @return this
     */
    public FilterConf remoteHost(boolean value) { this.remoteHost = value; return this; }
    /**
     * Add remoteAddr to request info
     * @param value true if remoteHost should be displayed
     * @return this
     */
    public FilterConf remoteAddr(boolean value) { this.remoteAddr = value; return this; }
    /**
     * Do not log anything.
     * @param value true if no logging must be done
     * @return this
     */
    public FilterConf doNothing(boolean value) { this.doNothing = value; return this; }
    /**
     * How to format header to request info. (default no header is printed)
     * @param value like LogFilter.Header.all()
     * @return this
     */
    public FilterConf requestHeaderFormatter(Function<HeadersWrapper, String> value) { this.requestHeaderFormatter = value; return this; }
    /**
     * How to format body to request info. (default no body is printed)
     * @param value like LogFilter.Body.asIs()
     * @return this
     */
    public FilterConf requestPayloadFormatter(Function<InputStream, String> value) { this.requestPayloadFormatter = value; return this; }
    /**
     * How to format header to response info. (default no header is printed)
     * @param value like LogFilter.Header.all()
     * @return this
     */
    public FilterConf responseHeaderFormatter(Function<HeadersWrapper, String> value) { this.responseHeaderFormatter = value; return this; }
    /**
     * How to format body to response info. (default no body is printed)
     * @param value like LogFilter.Body.asIs()
     * @return this
     */
    public FilterConf responsePayloadFormatter(Function<InputStream, String> value) { this.responsePayloadFormatter = value; return this; }

    public FilterConf copy() {
        try {
            return (FilterConf)this.clone();
        } catch(CloneNotSupportedException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public String toString() {
        if(doNothing) return "doNothing";
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("enabled=").append(messageConsumerEnabled.getAsBoolean());
        if(identity) sb.append(", identity");
        if(remoteAddr) sb.append(", addr");
        if(remoteHost) sb.append(", host");
        if(requestStartPrefix != null) sb.append(", requestStartPrefix=").append(requestStartPrefix);
        if(requestPrefix != null) sb.append(", requestPrefix=").append(requestPrefix);
        if(requestHeaderFormatter != null) sb.append(", requestHeader");
        if(requestPayloadFormatter != null) sb.append(", requestPayload");
        if(responsePrefix != null) sb.append(", responsePrefix=").append(responsePrefix);
        if(responseHeaderFormatter != null) sb.append(", responseHeader");
        if(responsePayloadFormatter != null) sb.append(", responsePayload");
        sb.append(']');
        return sb.toString();
    }



}
