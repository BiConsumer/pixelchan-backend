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

package me.orlando.pixelchan.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.function.Function;

public class Observable<T> {

    private final Callable<T> callable;
    private final Executor executor;

    private final List<Subscription<T>> subscriptions = new ArrayList<>();

    public Observable(Callable<T> callable, Executor executor) {
        this.callable = callable;
        this.executor = executor;
    }

    public Callable<T> getCallable() {
        return this.callable;
    }

    public Executor getExecutor() {
        return this.executor;
    }

    public List<Subscription<T>> getSubscriptions() {
        return this.subscriptions;
    }

    public Observable<T> subscribe(Observer<T> observer) {
        Subscription<T> subscription = new Subscription<>(this, observer);
        this.subscriptions.forEach(iterated -> iterated.getSubscriber().onSubscribe(subscription));
        this.subscriptions.add(subscription);
        return this;
    }

    public <R> Observable<R> pipe(Function<Pipe<T>, Pipe<R>> function) {
        return function.apply(new Pipe<>(this.callable, this.executor)).buildObservable();
    }

    public void query() {
        this.executor.execute(() -> {
            try {
                T value = this.callable.call();
                for (Subscription<T> subscription : subscriptions) {
                    Observer<T> observer = subscription.getSubscriber();
                    observer.onComplete();
                    observer.onNext(value);
                }
            } catch (Exception e) {
                for (Subscription<T> subscription : subscriptions) {
                    Observer<T> observer = subscription.getSubscriber();
                    observer.onComplete();
                    observer.onError(e);
                }
            }
        });
    }
}