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
package sk.antons.servlet.filter.builder;

import java.io.InputStream;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import sk.antons.servlet.filter.FilterConf;
import sk.antons.servlet.filter.HeadersWrapper;

/**
 * Builder for log filter configuration
 * @author antons
 */
public class FilterConfBuilder<C> {
    C backReference;
    Consumer<FilterConf> consumer;

    FilterConf conf;

    private FilterConfBuilder(FilterConf defaultConf, C back, Consumer<FilterConf> consumer) {
        this.conf = defaultConf == null ?  new FilterConf() : defaultConf.copy();
        this.backReference = back;
        this.consumer = consumer;
    }
    /**
     * New instance of builder
     * @param defaultConf default configuration from parent context or null
     * @param back parent
     * @param consumer where created configuration must be send
     * @return new builder
     */
    public static <T> FilterConfBuilder<T> instance(FilterConf defaultConf, T back, Consumer<FilterConf> consumer) { return new FilterConfBuilder(defaultConf, back, consumer); }

    /**
     * Returns to parent context
     * @return parent
     */
    public C done() {
        if(consumer != null) consumer.accept(conf);
        return backReference;
    }

    /**
     * reset configuration to defaut values.
     * @return this
     */
    public FilterConfBuilder<C> reset() { this.conf = new FilterConf(); return this; }

    /**
     * Message consumer for log filter messages
     * @param value consumer like (m) -> log.debug(m)
     * @return this
     */
    public FilterConfBuilder<C> messageConsumer(Consumer<String> value) { this.conf.messageConsumer(value); return this; }
    /**
     * Determines if logging is enabled
     * @param value like () -> log.isDebugEnabled()
     * @return this
     */
    public FilterConfBuilder<C> messageConsumerEnabled(BooleanSupplier value) { this.conf.messageConsumerEnabled(value); return this; }
    /**
     * Prefix for request displayed on request start. (default REQ) If it is not defined no message is printed
     * @param value prefix like REQ
     * @return this
     */
    public FilterConfBuilder<C> requestStartPrefix(String value) { this.conf.requestStartPrefix(value); return this; }
    /**
     * Prefix for request displayed on request end. (default REQ) If it is not defined no message is printed
     * @param value prefix like REQ
     * @return this
     */
    public FilterConfBuilder<C> requestPrefix(String value) { this.conf.requestPrefix(value); return this; }
    /**
     * Prefix for response displayed on request end (default RES) If it is not defined no message is printed
     * @param value prefix like RES
     * @return this
     */
    public FilterConfBuilder<C> responsePrefix(String value) { this.conf.responsePrefix(value); return this; }
    /**
     * Add user identity to request info
     * @param value true if identity should be displayed
     * @return this
     */
    public FilterConfBuilder<C> identity(boolean value) { this.conf.identity(value); return this; }
    /**
     * Add remote addr  to request info
     * @param value true if remote addr should be displayed
     * @return this
     */
    public FilterConfBuilder<C> remoteAddr(boolean value) { this.conf.remoteAddr(value); return this; }
    /**
     * Add remote host to request info
     * @param value true if remote addr should be displayed
     * @return this
     */
    public FilterConfBuilder<C> remoteHost(boolean value) { this.conf.remoteHost(value); return this; }
    /**
     * Do not log anything.
     * @param value true if no logging must be done
     * @return this
     */
    public FilterConfBuilder<C> doNothing(boolean value) { this.conf.doNothing(value); return this; }
    /**
     * How to format header to request info. (default no header is printed)
     * @param value like LogFilter.Header.all()
     * @return this
     */
    public FilterConfBuilder<C> requestHeaderFormatter(Function<HeadersWrapper, String> value) { this.conf.requestHeaderFormatter(value); return this; }
    /**
     * How to format body to request info. (default no body is printed)
     * @param value like LogFilter.Body.asIs()
     * @return this
     */
    public FilterConfBuilder<C> requestPayloadFormatter(Function<InputStream, String> value) { this.conf.requestPayloadFormatter(value); return this; }
    /**
     * How to format header to response info. (default no header is printed)
     * @param value like LogFilter.Header.all()
     * @return this
     */
    public FilterConfBuilder<C> responseHeaderFormatter(Function<HeadersWrapper, String> value) { this.conf.responseHeaderFormatter(value); return this; }
    /**
     * How to format body to response info. (default no body is printed)
     * @param value like LogFilter.Body.asIs()
     * @return this
     */
    public FilterConfBuilder<C> responsePayloadFormatter(Function<InputStream, String> value) { this.conf.responsePayloadFormatter(value); return this; }


}
