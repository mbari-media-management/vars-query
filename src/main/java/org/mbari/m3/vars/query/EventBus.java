package org.mbari.m3.vars.query;



import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;


/**
 * An event bus based on <a href="https://github.com/ReactiveX/RxJava">RXJava</a>.
 *
 * @author Brian Schlining
 * @since 2015-07-17T16:37:00
 */
public class EventBus {

    /*
     If multiple threads are going to emit events to this
     then it must be made thread-safe like this instead
     */
    private final Subject<Object> rxSubject = PublishSubject.create().toSerialized();

    public void send(Object o) {
        rxSubject.onNext(o);
    }

    public Observable<Object> toObserverable() {
        return rxSubject;
    }

}
