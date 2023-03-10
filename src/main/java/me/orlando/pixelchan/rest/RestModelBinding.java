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

import com.fasterxml.jackson.databind.ObjectMapper;
import me.orlando.pixelchan.repository.Model;
import me.orlando.pixelchan.repository.Repository;
import me.orlando.pixelchan.rest.service.RestService;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface RestModelBinding<M extends Model> {

    RestModelBinding<M> service(RestService<M> service);

    RestModelBinding<M> handleGet(String route, Handler<M> handler);

    RestModelBinding<M> listAll();

    RestModelBinding<M> get();

    <P> RestModelBinding<M> create(Class<P> partialClass, Function<P, M> creator, @Nullable BiConsumer<P, M> then);

    default <P> RestModelBinding<M> create(Class<P> partialClass, Function<P, M> creator) {
        return create(partialClass, creator, null);
    }


    interface Handler<M extends Model> {
        String handle(ObjectMapper mapper, Repository<M> repository, Map<String, String> params) throws Exception;
    }

}