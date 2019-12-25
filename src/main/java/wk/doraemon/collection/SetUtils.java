package wk.doraemon.collection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by TF on 2019/1/4.
 */
public class SetUtils {

    /**
     * 元素转换
     */
    public static <T, R> Set<R> map(Set<T> theSet, Function<T, R> function) {
        return theSet.stream().map(function).collect(Collectors.toSet());
    }

    public static <T> Set<T> filter(Set<T> theSet, Predicate<T> predicate) {
        return theSet.stream().filter(predicate).collect(Collectors.toSet());
    }

    public static <T> Set<T> intersect(Set<T> theSet1, Set<T> theSet2) {
        if(theSet1==null || theSet2==null) {
            return null;
        }
        Set<T> newSet = new TreeSet<>();
        newSet.addAll(theSet1);
        newSet.retainAll(theSet2);
        return newSet;
    }

}
