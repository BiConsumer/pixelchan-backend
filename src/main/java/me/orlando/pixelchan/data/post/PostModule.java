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

package me.orlando.pixelchan.data.post;

import me.orlando.pixelchan.data.topic.Topic;
import me.orlando.pixelchan.repository.RepositoryRegistry;
import me.orlando.pixelchan.rest.RestApplicationBinder;
import me.orlando.pixelchan.rest.RestModule;

import java.util.Date;
import java.util.UUID;

public class PostModule implements RestModule {

    private final static RepositoryRegistry REGISTRY = RepositoryRegistry.getInstance();

    @Override
    public void configure(RestApplicationBinder binder) {
        binder.bindModel(Post.class)
                .get()
                .listAll()
                .create(PostCreateRequest.class, createRequest -> {
                    if (createRequest.content().isBlank()) {
                        throw new IllegalArgumentException("Content cannot be blank");
                    }

                    if (!REGISTRY.repository(Topic.class).existsByIdSync(createRequest.topic().id())) {
                        throw new IllegalArgumentException("No topic with that id found");
                    }

                    return new Post(
                            UUID.randomUUID().toString(),
                            new Date(),
                            createRequest.topic(),
                            createRequest.content()
                    );
                });
    }
}