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

package me.orlando.pixelchat.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.orlando.pixelchat.repository.Model;
import me.orlando.pixelchat.repository.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SparkRestApplicationBinder implements RestApplicationBinder {

    private final Map<Class<? extends Model>, Repository<? extends Model>> repositoryMap = new HashMap<>();
    private final Set<SparkRestModelBinding<? extends Model>> bindings = new HashSet<>();

    private final ObjectMapper mapper;

    SparkRestApplicationBinder(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public <M extends Model> void registerRepository(Class<M> modelClass, Repository<M> repository) {
        repositoryMap.put(modelClass, repository);
    }

    @Override
    public <M extends Model> RestModelBinding<M> bindModel(Class<M> modelClass) {
        SparkRestModelBinding<M> binding = new SparkRestModelBinding<>(this, modelClass);
        bindings.add(binding);
        return binding;
    }

    @SuppressWarnings("unchecked")
    public <M extends Model> Repository<M> repository(Class<M> modelClass) {
        return (Repository<M>) repositoryMap.get(modelClass);
    }

    public ObjectMapper mapper() {
        return mapper;
    }

    public Set<SparkRestModelBinding<? extends Model>> bindings() {
        return bindings;
    }
}