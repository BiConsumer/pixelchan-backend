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

package me.orlando.pixelchan.repository;

import java.util.HashMap;
import java.util.Map;

public class RepositoryRegistry {

    private static RepositoryRegistry INSTANCE;

    private final Map<Class<?>, Repository<?>> repositoryMap = new HashMap<>();

    public <M extends Model> RepositoryRegistry register(Class<M> modelClass, Repository<M> repository) {
        repositoryMap.put(modelClass, repository);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <M extends Model> Repository<M> repository(Class<?> modelClass) {
        return (Repository<M>) repositoryMap.get(modelClass);
    }

    public static RepositoryRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RepositoryRegistry();
        }

        return INSTANCE;
    }
}