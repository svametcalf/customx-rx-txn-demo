package com.example.custom_rx_demo;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import reactor.core.CorePublisher;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;

public class MonoWrapper<T> implements CorePublisher<T> {
    private final Mono<T> wrapped;

    public MonoWrapper(Mono<T> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void subscribe(CoreSubscriber<? super T> coreSubscriber) {
        wrapped.subscribe(coreSubscriber);
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        wrapped.subscribe(subscriber);
    }

    public Mono<T> asMono() {
        return this.wrapped;
    }
}
