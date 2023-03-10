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

package me.orlando.pixelchan.data.category;

import me.orlando.pixelchan.data.display.CategoryDisplay;
import me.orlando.pixelchan.data.post.Post;
import me.orlando.pixelchan.data.topic.Topic;
import me.orlando.pixelchan.repository.Repository;
import me.orlando.pixelchan.repository.RepositoryRegistry;
import me.orlando.pixelchan.rest.RestApplicationBinder;
import me.orlando.pixelchan.rest.RestModule;

import java.util.HashSet;
import java.util.Set;

public class CategoryModule implements RestModule {

    private final static RepositoryRegistry REPOSITORY_REGISTRY = RepositoryRegistry.getInstance();

    private final Repository<Topic> TOPIC_REPOSITORY = REPOSITORY_REGISTRY.repository(Topic.class);
    private final Repository<Post> POST_REPOSITORY = REPOSITORY_REGISTRY.repository(Post.class);

    @Override
    public void configure(RestApplicationBinder binder) {
        binder.bindModel(Category.class)
                .handleGet("/displays/", ((mapper, repository, params) -> {
                    Set<CategoryDisplay> displays = new HashSet<>();
                    for (Category category : repository.findAllSync()) {
                        displays.add(CategoryDisplay.fromCategory(category, TOPIC_REPOSITORY, POST_REPOSITORY));
                    }

                    return mapper.writeValueAsString(displays);
                }))
                .get()
                .listAll();
    }
}