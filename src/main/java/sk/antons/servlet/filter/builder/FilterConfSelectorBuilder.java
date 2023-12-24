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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Consumer;
import sk.antons.servlet.filter.FilterConf;
import sk.antons.servlet.filter.FilterConfSelector;
import sk.antons.servlet.filter.condition.Condition;

/**
 * Builder for one cache of log filter usage. It defines combination of
 * {@code <li>} request condition (mandatory)
 * {@code <li>} response condition (optional) (if it is defined no request start message is printed)
 * {@code <li>} filter configuration (optional) By default each case inherits default configuration.
 *              You only set what you want chage from defaul.
 *
 * @author antons
 */
public class FilterConfSelectorBuilder<C> {
    C backReference;
    Consumer<FilterConfSelector> consumer;

    private FilterConf conf;
    private Condition<HttpServletRequest> requestCondition;
    private Condition<HttpServletResponse> responseCondition;

    private FilterConfSelectorBuilder(FilterConf defaultConf, C back, Consumer<FilterConfSelector> consumer) {
        if(defaultConf == null) defaultConf = FilterConf.instance();
        this.backReference = back;
        this.consumer = consumer;
        this.conf = defaultConf.copy();
    }
    /**
     * Instance of builder
     * @param defaultConf default configuration used as base
     * @param back parent
     * @param consumer target of case combination
     * @return instance
     */
    public static <T> FilterConfSelectorBuilder instance(FilterConf defaultConf, T back, Consumer<FilterConfSelector> consumer) { return new FilterConfSelectorBuilder(defaultConf, back, consumer); }

    /**
     * Define case combination
     * @return parent
     */
    public C done() {
        if(requestCondition == null) throw new IllegalStateException("no request condition");
        if(consumer != null) consumer.accept(FilterConfSelector.instance().conf(conf).requestCondition(requestCondition).responseCondition(responseCondition));
        return backReference;
    }


    /**
     * Define filter configuration
     * @return filter builder
     */
    public FilterConfBuilder<FilterConfSelectorBuilder<C>> conf() { return FilterConfBuilder.instance(this.conf, this, f -> this.conf = f); }
    /**
     * Define request condition
     * @return condition builder
     */
    public RequestConditionBuilder<FilterConfSelectorBuilder<C>> request() { return RequestConditionBuilder.instance(this, f -> this.requestCondition = f); }
    /**
     * Define response condition
     * @return condition builder
     */
    public ResponseConditionBuilder<FilterConfSelectorBuilder<C>> response() { return ResponseConditionBuilder.instance(this, f -> this.responseCondition = f); }



}
