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

import javax.servlet.http.HttpServletRequest;
import java.util.function.Consumer;
import sk.antons.servlet.filter.condition.Condition;
import sk.antons.servlet.filter.condition.ConditionBuilder;
import sk.antons.servlet.filter.condition.ConstCondition;

/**
 * Request condition builder.
 *
 * It define set of methods to create condition in a same way as you write it to line.
 * You write indifidual conditions like .path().startsWith("/foo/bar") an combine them
 * with logical operatiosn like not(), and() and or(). You ban use also brackets lg() and rb().
 *
 * {@code <pre>}
 *  .method().equals("POST")
 *  .and()
 *  .lb()
 *    .path().startsWith("/foo")
 *    .or()
 *    .path().startsWith("/bar")
 *  .rb()
 *  .done()
 * {@code </pre>}
 *
 *
 * @author antons
 */
public class RequestConditionBuilder<C> {
    C backReference;
    Consumer<Condition<HttpServletRequest>> consumer;

    ConditionBuilder<HttpServletRequest> builder = ConditionBuilder.instance(HttpServletRequest.class);

    private RequestConditionBuilder(C back, Consumer<Condition<HttpServletRequest>> consumer) {
        this.backReference = back;
        this.consumer = consumer;
    }
    public static <T> RequestConditionBuilder<T> instance(T back, Consumer<Condition<HttpServletRequest>> consumer) { return new RequestConditionBuilder(back, consumer); }

    public C done() {
        if(consumer != null) consumer.accept(builder.condition());
        return backReference;
    }

    public RequestConditionBuilder<C> condition(Condition<HttpServletRequest> condition) { builder.add(condition); return this; }
    public RequestConditionBuilder<C> not() { builder.not(); return this; }
    public RequestConditionBuilder<C> and() { builder.and(); return this; }
    public RequestConditionBuilder<C> or() { builder.or(); return this; }
    public RequestConditionBuilder<C> lb() { builder.lb(); return this; }
    public RequestConditionBuilder<C> rb() { builder.rb(); return this; }

    public StringConditionBuilder<RequestConditionBuilder<C>, HttpServletRequest> path() { return StringConditionBuilder.instance(this, r -> r.getPathInfo(), c -> builder.add(c), "request.path"); }
    public StringConditionBuilder<RequestConditionBuilder<C>, HttpServletRequest> method() { return StringConditionBuilder.instance(this, r -> r.getMethod(), c -> builder.add(c), "request.method"); }
    public StringConditionBuilder<RequestConditionBuilder<C>, HttpServletRequest> header(final String key) { return StringConditionBuilder.instance(this, r -> r.getHeader(key), c -> builder.add(c), "request.header["+key+"]"); }
    public StringConditionBuilder<RequestConditionBuilder<C>, HttpServletRequest> param(final String key) { return StringConditionBuilder.instance(this, r -> r.getParameter(key), c -> builder.add(c), "request.param["+key+"]"); }
    public StringConditionBuilder<RequestConditionBuilder<C>, HttpServletRequest> contentType() { return StringConditionBuilder.instance(this, r -> r.getContentType(), c -> builder.add(c), "request.contentType"); }
    public StringConditionBuilder<RequestConditionBuilder<C>, HttpServletRequest> contextPath() { return StringConditionBuilder.instance(this, r -> r.getContextPath(), c -> builder.add(c), "request.contextPath"); }
    public StringConditionBuilder<RequestConditionBuilder<C>, HttpServletRequest> localAddr() { return StringConditionBuilder.instance(this, r -> r.getLocalAddr(), c -> builder.add(c), "request.localAddr"); }
    public StringConditionBuilder<RequestConditionBuilder<C>, HttpServletRequest> localName() { return StringConditionBuilder.instance(this, r -> r.getLocalName(), c -> builder.add(c), "request.localName"); }
    public StringConditionBuilder<RequestConditionBuilder<C>, HttpServletRequest> pathTranslated() { return StringConditionBuilder.instance(this, r -> r.getPathTranslated(), c -> builder.add(c), "request.pathTranslated"); }
    public StringConditionBuilder<RequestConditionBuilder<C>, HttpServletRequest> queryString() { return StringConditionBuilder.instance(this, r -> r.getQueryString(), c -> builder.add(c), "request.queryString"); }
    public StringConditionBuilder<RequestConditionBuilder<C>, HttpServletRequest> remoteAddr() { return StringConditionBuilder.instance(this, r -> r.getRemoteAddr(), c -> builder.add(c), "request.remoteAddr"); }
    public StringConditionBuilder<RequestConditionBuilder<C>, HttpServletRequest> remoteHost() { return StringConditionBuilder.instance(this, r -> r.getRemoteHost(), c -> builder.add(c), "request.remoteHost"); }
    public StringConditionBuilder<RequestConditionBuilder<C>, HttpServletRequest> remoteUser() { return StringConditionBuilder.instance(this, r -> r.getRemoteUser(), c -> builder.add(c), "request.remoteUser"); }
    public StringConditionBuilder<RequestConditionBuilder<C>, HttpServletRequest> uri() { return StringConditionBuilder.instance(this, r -> r.getRequestURI(), c -> builder.add(c), "request.uri"); }
    public StringConditionBuilder<RequestConditionBuilder<C>, HttpServletRequest> schema() { return StringConditionBuilder.instance(this, r -> r.getScheme(), c -> builder.add(c), "request.schema"); }
    public StringConditionBuilder<RequestConditionBuilder<C>, HttpServletRequest> servletPath() { return StringConditionBuilder.instance(this, r -> r.getServletPath(), c -> builder.add(c), "request.servletPath"); }

    public RequestConditionBuilder<C> any() { builder.add(ConstCondition.instance(true)); return this; }

}
