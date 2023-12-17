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
 *
 * @author antons
 */
public class FilterConf {

    private Consumer<String> messageConsumer;
    private BooleanSupplier messageConsumerEnabled;
    private String requestStartPrefix = "REQ";
    private String requestPrefix = "REQ";
    private String responsePrefix = "RES";
    private boolean identity = false;
    private boolean doNothing = false;
    private Function<Headers, String> requestHeaderFormatter;
    private Function<InputStream, String> requestPayloadFormatter;
    private Function<Headers, String> responseHeaderFormatter;
    private Function<InputStream, String> responsePayloadFormatter;

    public Consumer<String> messageConsumer() { return messageConsumer; }
    public BooleanSupplier messageConsumerEnabled() { return messageConsumerEnabled; }
    public String requestStartPrefix() { return requestStartPrefix; }
    public String requestPrefix() { return requestPrefix; }
    public String responsePrefix() { return responsePrefix; }
    public boolean identity() { return identity; }
    public boolean doNothing() { return doNothing; }
    public Function<Headers, String> requestHeaderFormatter() { return requestHeaderFormatter; }
    public Function<InputStream, String> requestPayloadFormatter() { return requestPayloadFormatter; }
    public Function<Headers, String> responseHeaderFormatter() { return responseHeaderFormatter; }
    public Function<InputStream, String> responsePayloadFormatter() { return responsePayloadFormatter; }

    public static FilterConf instance() { return new FilterConf(); }
    public FilterConf messageConsumer(Consumer<String> value) { this.messageConsumer = value; return this; }
    public FilterConf messageConsumerEnabled(BooleanSupplier value) { this.messageConsumerEnabled = value; return this; }
    public FilterConf requestStartPrefix(String value) { this.requestStartPrefix = value; return this; }
    public FilterConf requestPrefix(String value) { this.requestPrefix = value; return this; }
    public FilterConf responsePrefix(String value) { this.responsePrefix = value; return this; }
    public FilterConf identity(boolean value) { this.identity = value; return this; }
    public FilterConf doNothing(boolean value) { this.doNothing = value; return this; }
    public FilterConf requestHeaderFormatter(Function<Headers, String> value) { this.requestHeaderFormatter = value; return this; }
    public FilterConf requestPayloadFormatter(Function<InputStream, String> value) { this.requestPayloadFormatter = value; return this; }
    public FilterConf responseHeaderFormatter(Function<Headers, String> value) { this.responseHeaderFormatter = value; return this; }
    public FilterConf responsePayloadFormatter(Function<InputStream, String> value) { this.responsePayloadFormatter = value; return this; }
}
