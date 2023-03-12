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

package me.orlando.pixelchan;import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import me.orlando.pixelchan.data.category.Category;
import me.orlando.pixelchan.data.post.Post;
import me.orlando.pixelchan.data.topic.Topic;
import me.orlando.pixelchan.repository.MockRepository;
import me.orlando.pixelchan.repository.Repository;
import me.orlando.pixelchan.repository.RepositoryRegistry;
import me.orlando.pixelchan.util.ModelFactory;
import org.junit.jupiter.api.Test;

public class ReferenceTest {

    private final static Category CATEGORY = ModelFactory.category("Testing");

    private final static Topic TOPIC = ModelFactory.topic(
            CATEGORY,
            "What happens if testing test?",
            0
    );

    private final static Post POST = ModelFactory.post(
            TOPIC,
            "I've been wondering what a test is."
    );

    @Test
    public void test() throws JsonProcessingException {
        Repository<Category> categoryRepository = new MockRepository<>();
        Repository<Topic> topicRepository = new MockRepository<>();
        Repository<Post> postRepository = new MockRepository<>();

        RepositoryRegistry.getInstance()
                .register(Category.class, categoryRepository)
                .register(Topic.class, topicRepository)
                .register(Post.class, postRepository);

        ObjectMapper mapper = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true);

        categoryRepository.saveSync(CATEGORY);
        topicRepository.saveSync(TOPIC);
        postRepository.saveSync(POST);

        String postRaw = "{\n" +
                "  \"id\" : \"1dc614b1-0e8a-43ea-9d54-fd8e872a5932\",\n" +
                "  \"createdAt\" : 1677388683551,\n" +
                "  \"topic\" : \"" + TOPIC.id() + "\",\n" +
                "  \"content\" : \"funny\"\n" +
                "}";

        Post post = mapper.readValue(postRaw, Post.class);

        System.out.println(post.topic().name());
        assert (post.topic().name().equals(TOPIC.name()));
    }
}