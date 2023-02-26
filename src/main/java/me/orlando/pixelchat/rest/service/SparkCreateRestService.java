/*
 * This file is part of pixelchat-backend, licensed under the MIT license
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

package me.orlando.pixelchat.rest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.orlando.pixelchat.repository.Model;
import me.orlando.pixelchat.repository.Repository;
import org.jetbrains.annotations.Nullable;
import spark.Spark;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SparkCreateRestService<M extends Model, P> extends AbstractSparkRestService<M> {

    private final ObjectMapper mapper;
    private final Class<P> partialClass;
    private final Function<P, M> creator;
    private final @Nullable BiConsumer<P, M> then;

    public SparkCreateRestService(
            ObjectMapper mapper,
            Repository<M> repository,
            Class<M> modelClass,
            Class<P> partialClass,
            Function<P, M> creator,
            @Nullable BiConsumer<P, M> then
    ) {
        super(repository, modelClass);
        this.mapper = mapper;
        this.partialClass = partialClass;
        this.creator = creator;
        this.then = then;
    }

    @Override
    public void register() {
        Spark.post(route(), (req, res) -> {
            res.type("application.json");

            P partial = mapper.readValue(res.body(), partialClass);
            M model = creator.apply(partial);

            if (then != null) {
                then.accept(partial, model);
            }

            repository.saveSync(model);

            return model;
        }, mapper::writeValueAsString);
    }

    @Override
    public String route() {
        return "/" + route + "/create";
    }
}