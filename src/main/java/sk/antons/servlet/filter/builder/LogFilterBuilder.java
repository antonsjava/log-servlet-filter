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

import sk.antons.servlet.filter.FilterConf;
import sk.antons.servlet.filter.LogFilter;

/**
 * Builder for logging servlet filter.
 * @author antons
 */
public class LogFilterBuilder {

    LogFilter filter = LogFilter.instance();
    FilterConf def;

    /**
     * New instance of builder.
     * @return
     */
    public static LogFilterBuilder instance() { return new LogFilterBuilder(); }
    /**
     * Builder for default configuration.
     * @return
     */
    public FilterConfBuilder<LogFilterBuilder> defaultConf() { return FilterConfBuilder.instance(null, this, f -> this.def = f); }
    /**
     * Builder for next log filter use case configuration
     * @return
     */
    public FilterConfSelectorBuilder<LogFilterBuilder> inCase() { return FilterConfSelectorBuilder.instance(def, this, f -> this.filter.selector(f)); }

    /**
     * Creates filter
     * @return
     */
    public LogFilter build() { return filter; }


}
