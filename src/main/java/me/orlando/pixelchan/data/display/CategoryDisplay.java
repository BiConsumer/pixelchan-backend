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

package me.orlando.pixelchan.data.display;

import me.orlando.pixelchan.data.category.Category;
import me.orlando.pixelchan.data.post.Post;
import me.orlando.pixelchan.data.topic.Topic;
import me.orlando.pixelchan.repository.Repository;

import java.util.HashSet;
import java.util.Set;

public record CategoryDisplay(Category category, Set<TopicDisplay> topicDisplays) {

    public static CategoryDisplay fromCategory(Category category, Repository<Topic> topicRepository, Repository<Post> postRepository) {
        Set<TopicDisplay> topicDisplays = new HashSet<>();

        for (Topic topic : topicRepository.findAllSync()) {
            if (!topic.category().id().equals(category.id())) {
                continue;
            }

            topicDisplays.add(TopicDisplay.fromTopic(topic, postRepository));
        }

        return new CategoryDisplay(category, topicDisplays);
    }
}