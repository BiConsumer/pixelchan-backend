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

import java.io.IOException;
import java.text.ParseException;
import java.util.Random;

public class PixelchanBootstrapTest {

    private final static RepositoryRegistry REPOSITORY_REGISTRY = RepositoryRegistry.getInstance();

    private final static Category CATEGORY = ModelFactory.category(
            "Testing"
    );

    private final static Topic TOPIC = ModelFactory.topic(
            CATEGORY,
            "What happens if testing test?",
            0
    );

    private final static Post POST = ModelFactory.post(
            TOPIC,
            "I've been wondering what a test is."
    );

    private final static Random RANDOM = new Random();

    public static void main(String[] args) throws ParseException, IOException {
        Repository<Category> categoryRepository = new MockRepository<>();
        Repository<Topic> topicRepository = new MockRepository<>();
        Repository<Post> postRepository = new MockRepository<>();

        REPOSITORY_REGISTRY
                .register(Category.class, categoryRepository)
                .register(Topic.class, topicRepository)
                .register(Post.class, postRepository);

        ObjectMapper mapper = new ObjectMapper()
                .configure(SerializationFeature.INDENT_OUTPUT, true);

        RestApplication restApplication = RestApplication.sparkApplication("127.0.0.1", 5000, mapper, binder -> {
            binder.bindRepository(Category.class, categoryRepository);
            binder.bindRepository(Topic.class, topicRepository);
            binder.bindRepository(Post.class, postRepository);

            binder.install(new CategoryModule());
            binder.install(new TopicModule());
            binder.install(new PostModule());
        });

        for (int i = 0; i < 10; i++) {
            Topic topic = ModelFactory.topic(CATEGORY, "Test" + i, RANDOM.nextInt(100));
            topicRepository.saveSync(topic);

            for (int postIndex = 0; postIndex < RANDOM.nextInt(1, 5); postIndex++) {
                postRepository.saveSync(ModelFactory.randomDatePost(topic, generateRandomText(RANDOM.nextInt(1000))));
            }
        }

        categoryRepository.saveSync(CATEGORY);
        topicRepository.saveSync(TOPIC);
        postRepository.saveSync(POST);

        CategoryConfig[] categories = mapper.readValue(
                PixelchanBootstrapTest.class.getClassLoader().getResource("categories.json"),
                CategoryConfig[].class
        );

        for (CategoryConfig category : categories) {
            categoryRepository.saveSync(ModelFactory.category(category.name()));
        }

        restApplication.initiate();
        Runtime.getRuntime().addShutdownHook(new Thread(restApplication::shutdown));
    }

    private static String generateRandomText(int length) {
        int leftLimit = 48;
        int rightLimit = 122;

        return RANDOM.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}