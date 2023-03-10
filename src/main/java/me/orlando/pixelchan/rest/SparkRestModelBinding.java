/*
 * This file is part of pixelchan-backend, licensed under the MIT license
 *
 * Copyright (c) 2023 Orlando Dieguez
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.orlando.pixelchan.rest;

import me.orlando.pixelchan.repository.Model;
import me.orlando.pixelchan.rest.service.RestService;
import me.orlando.pixelchan.rest.service.spark.*;
import org.jetbrains.annotations.Nullable;
import spark.Spark;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class SparkRestModelBinding<M extends Model> implements RestModelBinding<M> {

    private final SparkRestApplicationBinder binder;
    private final Class<M> modelClass;

    private final Set<SparkRestService<? extends Model>> services = new HashSet<>();

    SparkRestModelBinding(SparkRestApplicationBinder binder, Class<M> modelClass) {
        this.binder = binder;
        this.modelClass = modelClass;
    }

    @Override
    public RestModelBinding<M> service(RestService<M> service) {
        if (!(service instanceof SparkRestService<M>)) {
            throw new IllegalArgumentException("Service must be a spark type service");
        }

        services.add((SparkRestService<? extends Model>) service);
        return this;
    }

    @Override
    public RestModelBinding<M> handleGet(String route, Handler<M> handler) {

        SparkRestService<M> restService = new AbstractSparkRestService<M>(binder.mapper(), binder.repository(modelClass), modelClass) {
            @Override
            public void register() {
                Spark.get(route(), (req, res) -> handler.handle(mapper, repository, req.params()));
            }

            @Override
            public String route() {
                return "/" + modelRoute + route;
            }
        };

        return service(restService);
    }

    @Override
    public RestModelBinding<M> listAll() {
        return service(new SparkListAllRestService<>(binder.mapper(), binder.repository(modelClass), modelClass));
    }

    @Override
    public RestModelBinding<M> get() {
        return service(new SparkGetRestService<>(binder.mapper(), binder.repository(modelClass), modelClass));
    }

    @Override
    public <P> RestModelBinding<M> create(Class<P> partialClass, Function<P, M> creator, @Nullable BiConsumer<P, M> then) {
        return service(new SparkCreateRestService<>(binder.mapper(), binder.repository(modelClass), modelClass, partialClass, creator, then));
    }

    public Set<SparkRestService<? extends Model>> services() {
        return services;
    }
}