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

package me.orlando.pixelchan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import me.orlando.pixelchan.config.CategoryConfig;
import me.orlando.pixelchan.data.category.Category;
import me.orlando.pixelchan.data.category.CategoryModule;
import me.orlando.pixelchan.data.post.Post;
import me.orlando.pixelchan.data.post.PostModule;
import me.orlando.pixelchan.data.topic.Topic;
import me.orlando.pixelchan.data.topic.TopicModule;
import me.orlando.pixelchan.repository.MockRepository;
import me.orlando.pixelchan.repository.Repository;
import me.orlando.pixelchan.repository.RepositoryRegistry;
import me.orlando.pixelchan.rest.RestApplication;
import me.orlando.pixelchan.util.ModelFactory;
import world.pokeland.cosmetics.repository.FileRepository;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class PixelchanBootstrap {

    private final static RepositoryRegistry REPOSITORY_REGISTRY = RepositoryRegistry.getInstance();

    public static void main(String[] args) throws ParseException, IOException {
        ObjectMapper mapper = new ObjectMapper()
                .configure(SerializationFeature.INDENT_OUTPUT, true);

        Repository<Category> categoryRepository = new FileRepository<>(new File("categories"), mapper, Category.class);
        Repository<Topic> topicRepository = new FileRepository<>(new File("topics"), mapper, Topic.class);
        Repository<Post> postRepository = new FileRepository<>(new File("posts"), mapper, Post.class);

        REPOSITORY_REGISTRY
                .register(Category.class, categoryRepository)
                .register(Topic.class, topicRepository)
                .register(Post.class, postRepository);

        CategoryConfig[] categories = mapper.readValue(
                PixelchanBootstrap.class.getClassLoader().getResource("categories.json"),
                CategoryConfig[].class
        );

        for (CategoryConfig category : categories) {
            categoryRepository.saveSync(ModelFactory.category(category.name()));
        }

        RestApplication restApplication = RestApplication.sparkApplication(5000, mapper, binder -> {
            binder.bindRepository(Category.class, categoryRepository);
            binder.bindRepository(Topic.class, topicRepository);
            binder.bindRepository(Post.class, postRepository);

            binder.install(new CategoryModule());
            binder.install(new TopicModule());
            binder.install(new PostModule());
        });

        restApplication.initiate();
        Runtime.getRuntime().addShutdownHook(new java.lang.Thread(restApplication::shutdown));
    }
}