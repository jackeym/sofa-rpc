/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.rpc.hystrix;

import com.alipay.sofa.rpc.core.request.SofaRequest;
import com.alipay.sofa.rpc.filter.FilterInvoker;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixObservableCommand;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implements, uses interface id as group key, method name as command key
 *
 * @author <a href=mailto:scienjus@gmail.com>ScienJus</a>
 */
public class DefaultSetterFactory implements SetterFactory {

    private static final Map<Method, HystrixCommand.Setter>           SETTER_CACHE            = new ConcurrentHashMap<Method, HystrixCommand.Setter>();

    private static final Map<Method, HystrixObservableCommand.Setter> OBSERVABLE_SETTER_CACHE = new ConcurrentHashMap<Method, HystrixObservableCommand.Setter>();

    @Override
    public HystrixCommand.Setter createSetter(FilterInvoker invoker, SofaRequest request) {
        Method clientMethod = request.getMethod();
        if (!SETTER_CACHE.containsKey(clientMethod)) {
            synchronized (DefaultSetterFactory.class) {
                if (!SETTER_CACHE.containsKey(clientMethod)) {
                    String groupKey = invoker.getConfig().getInterfaceId();
                    String commandKey = request.getMethodName();
                    HystrixCommand.Setter setter = HystrixCommand.Setter
                        .withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey))
                        .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey));
                    SETTER_CACHE.put(clientMethod, setter);
                }
            }
        }
        return SETTER_CACHE.get(clientMethod);
    }

    @Override
    public HystrixObservableCommand.Setter createObservableSetter(FilterInvoker invoker, SofaRequest request) {
        Method clientMethod = request.getMethod();
        if (!OBSERVABLE_SETTER_CACHE.containsKey(clientMethod)) {
            synchronized (DefaultSetterFactory.class) {
                if (!OBSERVABLE_SETTER_CACHE.containsKey(clientMethod)) {
                    String groupKey = invoker.getConfig().getInterfaceId();
                    String commandKey = request.getMethodName();
                    HystrixObservableCommand.Setter setter = HystrixObservableCommand.Setter
                        .withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey))
                        .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey));
                    OBSERVABLE_SETTER_CACHE.put(clientMethod, setter);
                }
            }
        }
        return OBSERVABLE_SETTER_CACHE.get(clientMethod);
    }
}
