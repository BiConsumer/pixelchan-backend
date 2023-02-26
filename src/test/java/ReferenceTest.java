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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import me.orlando.pixelchat.data.forum.Forum;
import me.orlando.pixelchat.data.post.Post;
import me.orlando.pixelchat.data.thread.Thread;
import me.orlando.pixelchat.repository.MockRepository;
import me.orlando.pixelchat.repository.Repository;
import me.orlando.pixelchat.repository.RepositoryRegistry;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

public class ReferenceTest {

    private final static Forum FORUM = new Forum(
            UUID.randomUUID().toString(),
            new Date(),
            "deez",
            "nuts"
    );

    private final static Thread THREAD = new Thread(
            UUID.randomUUID().toString(),
            new Date(),
            FORUM,
            "deez"
    );

    private final static Post POST = new Post(
            UUID.randomUUID().toString(),
            new Date(),
            THREAD,
            "funny"
    );

    @Test
    public void test() throws JsonProcessingException {
        Repository<Forum> forumRepository = new MockRepository<>();
        Repository<Thread> threadRepository = new MockRepository<>();
        Repository<Post> postRepository = new MockRepository<>();

        RepositoryRegistry.getInstance()
                .register(Forum.class, forumRepository)
                .register(Thread.class, threadRepository)
                .register(Post.class, postRepository);

        ObjectMapper mapper = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true);

        forumRepository.saveSync(FORUM);
        threadRepository.saveSync(THREAD);
        postRepository.saveSync(POST);

        String postRaw = "{\n" +
                "  \"id\" : \"1dc614b1-0e8a-43ea-9d54-fd8e872a5932\",\n" +
                "  \"createdAt\" : 1677388683551,\n" +
                "  \"thread\" : \"" + THREAD.id() + "\",\n" +
                "  \"content\" : \"funny\"\n" +
                "}";

        Post post = mapper.readValue(postRaw, Post.class);
        System.out.println(post.thread().name());
    }
}