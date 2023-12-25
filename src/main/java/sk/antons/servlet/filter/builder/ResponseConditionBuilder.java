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

import javax.servlet.http.HttpServletResponse;
import java.util.function.Consumer;
import sk.antons.servlet.filter.condition.Condition;
import sk.antons.servlet.filter.condition.ConditionBuilder;
import sk.antons.servlet.filter.condition.ConstCondition;
import sk.antons.servlet.filter.condition.NamedCondition;

/**
 * Response condition builder.
 *
 * It define set of methods to create condition in a same way as you write it to line.
 * You write indifidual conditions like .statusOK() an combine them
 * with logical operatiosn like not(), and() and or(). You ban use also brackets lg() and rb().
 *
 * {@code <pre>}
 *  .header("custom-header").exists()
 *  .and()
 *  .lb()
 *    .status().equals("200")
 *    .or()
 *    .status().startsWith("3")
 *  .rb()
 *  .done()
 * {@code </pre>}
 *
 * @author antons
 */
public class ResponseConditionBuilder<C> {
    C backReference;
    Consumer<Condition<HttpServletResponse>> consumer;

    ConditionBuilder<HttpServletResponse> builder = ConditionBuilder.instance(HttpServletResponse.class);

    private ResponseConditionBuilder(C back, Consumer<Condition<HttpServletResponse>> consumer) {
        this.backReference = back;
        this.consumer = consumer;
    }
    public static <T> ResponseConditionBuilder<T> instance(T back, Consumer<Condition<HttpServletResponse>> consumer) { return new ResponseConditionBuilder(back, consumer); }

    public C done() {
        if(consumer != null) consumer.accept(builder.condition());
        return backReference;
    }

    public ResponseConditionBuilder<C> condition(Condition<HttpServletResponse> condition) { builder.add(condition); return this; }
    public ResponseConditionBuilder<C> not() { builder.not(); return this; }
    public ResponseConditionBuilder<C> and() { builder.and(); return this; }
    public ResponseConditionBuilder<C> or() { builder.or(); return this; }
    public ResponseConditionBuilder<C> lb() { builder.lb(); return this; }
    public ResponseConditionBuilder<C> rb() { builder.rb(); return this; }

    public ResponseConditionBuilder<C> statusOK() { builder.add(NamedCondition.instance(r -> r.getStatus() < 300, "response.statusOK")); return this; }
    public ResponseConditionBuilder<C> any() { builder.add(ConstCondition.instance(true)); return this; }

    public StringConditionBuilder<ResponseConditionBuilder<C>, HttpServletResponse> status() { return StringConditionBuilder.instance(this, r -> "" + r.getStatus(), c -> builder.add(c), "request.status"); }
    public StringConditionBuilder<ResponseConditionBuilder<C>, HttpServletResponse> header(final String key) { return StringConditionBuilder.instance(this, r -> r.getHeader(key), c -> builder.add(c), "request.header["+key+"]"); }
    public StringConditionBuilder<ResponseConditionBuilder<C>, HttpServletResponse> contentType() { return StringConditionBuilder.instance(this, r -> r.getContentType(), c -> builder.add(c), "request.contentType"); }
}
