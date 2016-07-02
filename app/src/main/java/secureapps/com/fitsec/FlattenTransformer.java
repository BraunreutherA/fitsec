package secureapps.com.fitsec;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Alex on 02.07.16.
 */
public class FlattenTransformer<T> implements Observable.Transformer<Iterable<T>, T> {
    @Override
    public Observable<T> call(Observable<Iterable<T>> iterableObservable) {
        return iterableObservable.flatMap(new Func1<Iterable<T>, Observable<T>>() {
            @Override
            public Observable<T> call(Iterable<T> values) {

                return Observable.from(values);
            }
        });
    }
}