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
import spark.Spark;

public class SparkListAllRestService<M extends Model> extends AbstractSparkRestService<M> {

    public SparkListAllRestService(
            ObjectMapper mapper,
            Repository<M> repository,
            Class<M> modelClass
    ) {
        super(mapper, repository, modelClass);
    }

    @Override
    public void register() {
        Spark.get(route(), (req, res) -> repository.findAllSync(), mapper::writeValueAsString);
    }

    @Override
    public String route() {
        return "/" + route;
    }
}