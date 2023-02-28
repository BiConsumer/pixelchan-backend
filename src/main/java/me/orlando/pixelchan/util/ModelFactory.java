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

package me.orlando.pixelchan.util;

import me.orlando.pixelchan.data.category.Category;
import me.orlando.pixelchan.data.post.Post;
import me.orlando.pixelchan.data.topic.Topic;

import java.util.Date;
import java.util.UUID;

public class ModelFactory {

    public static Category category(String name, String description) {
        return new Category(
                UUID.randomUUID().toString(),
                new Date(),
                name,
                description
        );
    }

    public static Topic topic(Category category, String name, int rating) {
        return new Topic(
                UUID.randomUUID().toString(),
                new Date(),
                category,
                name,
                rating
        );
    }

    public static Post post(Topic topic, String content) {
        return new Post(
                UUID.randomUUID().toString(),
                new Date(),
                topic,
                content
        );
    }

}