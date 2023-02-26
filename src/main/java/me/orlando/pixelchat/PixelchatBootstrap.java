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

package me.orlando.pixelchat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import me.orlando.pixelchat.data.forum.Forum;
import me.orlando.pixelchat.data.forum.ForumModule;
import me.orlando.pixelchat.data.post.Post;
import me.orlando.pixelchat.data.post.PostModule;
import me.orlando.pixelchat.data.thread.Thread;
import me.orlando.pixelchat.data.thread.ThreadModule;
import me.orlando.pixelchat.repository.MockRepository;
import me.orlando.pixelchat.repository.Repository;
import me.orlando.pixelchat.repository.RepositoryRegistry;
import me.orlando.pixelchat.rest.RestApplication;

public class PixelchatBootstrap {

    private final static RepositoryRegistry REPOSITORY_REGISTRY = RepositoryRegistry.getInstance();

    public static void main(String[] args) {
        Repository<Forum> forumRepository = new MockRepository<>();
        Repository<Thread> threadRepository = new MockRepository<>();
        Repository<Post> postRepository = new MockRepository<>();

        REPOSITORY_REGISTRY
                .register(Forum.class, forumRepository)
                .register(Thread.class, threadRepository)
                .register(Post.class, postRepository);

        ObjectMapper mapper = new ObjectMapper()
                .configure(SerializationFeature.INDENT_OUTPUT, true);

        RestApplication restApplication = RestApplication.sparkApplication(mapper, binder -> {
            binder.registerRepository(Forum.class, forumRepository);
            binder.registerRepository(Thread.class, threadRepository);
            binder.registerRepository(Post.class, postRepository);

            binder.install(new ForumModule());
            binder.install(new ThreadModule());
            binder.install(new PostModule());
        });

        restApplication.initiate();
        Runtime.getRuntime().addShutdownHook(new java.lang.Thread(restApplication::shutdown));
    }
}