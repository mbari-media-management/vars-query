package vars.shared.rx;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * An event bus based on <a href="https://github.com/ReactiveX/RxJava">RXJava</a>.
 *
 * @author Brian Schlining
 * @since 2015-07-17T16:37:00
 */
public class RXEventBus {

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
