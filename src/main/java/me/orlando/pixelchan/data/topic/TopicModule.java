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

package me.orlando.pixelchan.data.topic;

import me.orlando.pixelchan.data.display.TopicDisplay;
import me.orlando.pixelchan.data.post.Post;
import me.orlando.pixelchan.repository.Repository;
import me.orlando.pixelchan.repository.RepositoryRegistry;
import me.orlando.pixelchan.rest.RestApplicationBinder;
import me.orlando.pixelchan.rest.RestModule;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TopicModule implements RestModule {

    private final static RepositoryRegistry REPOSITORY_REGISTRY = RepositoryRegistry.getInstance();
    private final static Repository<Post> POST_REPOSITORY = REPOSITORY_REGISTRY.repository(Post.class);
    @Override
    public void configure(RestApplicationBinder binder) {
        binder.bindModel(Topic.class)
                .get()
                .listAll()
                .create(TopicCreateRequest.class, creationRequest -> new Topic(
                        UUID.randomUUID().toString(),
                        new Date(),
                        creationRequest.category(),
                        creationRequest.name(),
                        0
                ), (createRequest, thread) -> POST_REPOSITORY.saveSync(
                        new Post(
                                UUID.randomUUID().toString(),
                                new Date(),
                                thread,
                                createRequest.postContent()
                        )
                ))
                .handleGet("/favorite/:id", (mapper, repository, params) -> {
                    Topic topic = repository.findByIdSync(params.get(":id"));

                    if (topic != null) {
                        repository.saveSync(topic.incrementFavorites());
                    }

                    return mapper.writeValueAsString(topic);
                })
                .handleGet("/unfavorite/:id", (mapper, repository, params) -> {
                    Topic topic = repository.findByIdSync(params.get(":id"));

                    if (topic != null) {
                        repository.saveSync(topic.decrementFavorites());
                    }

                    return mapper.writeValueAsString(topic);
                }).handleGet("/displays", ((mapper, repository, params) -> {
                    Set<TopicDisplay> displays = new HashSet<>();
                    for (Topic topic : repository.findAllSync()) {
                        displays.add(TopicDisplay.fromTopic(topic, POST_REPOSITORY));
                    }

                    return mapper.writeValueAsString(displays);
                }));
    }
}