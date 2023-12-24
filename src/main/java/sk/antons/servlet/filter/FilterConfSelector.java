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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sk.antons.servlet.filter.condition.Condition;

/**
 *
 * @author antons
 */
public class FilterConfSelector {

    private FilterConf conf;
    private Condition<HttpServletRequest> requestCondition;
    private Condition<HttpServletResponse> responseCondition;

    public FilterConf conf() { return conf; }
    public Condition<HttpServletRequest> requestCondition() { return requestCondition; }
    public Condition<HttpServletResponse> responseCondition() { return responseCondition; }

    public static FilterConfSelector instance() { return new FilterConfSelector(); }
    public FilterConfSelector conf(FilterConf value) { this.conf = value; return this; }
    public FilterConfSelector requestCondition(Condition<HttpServletRequest> value) { this.requestCondition = value; return this; }
    public FilterConfSelector responseCondition(Condition<HttpServletResponse> value) { this.responseCondition = value; return this; }

    public String configurationInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n---- case -------");
        if(requestCondition != null) sb.append("\n  when request: ").append(requestCondition);
        if(responseCondition != null) sb.append("\n  when response: ").append(responseCondition);
        if(conf != null) sb.append("\n  do: ").append(conf);
        return sb.toString();
    }
}
