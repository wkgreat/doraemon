package wk.doraemon.collection;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * List列表工具类
 * Created by TF on 2018/11/25.
 */
public class ListUtil {

    /**
     * 按条件过滤
     */
    public static <T> List<T> filter(List<T> theList, Predicate<? super T> predicate) {
        return theList.stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * 是否为空
     */
    public static <T> boolean isEmpty(List<T> theList) {
        return theList == null || theList.isEmpty();
    }

    /**
     * 按条件过滤，同时保留始末点
     */
    public static <T> List<T> filterWithVertex(List<T> theList, Predicate<? super T> predicate) {
        if (isEmpty(theList) || theList.size() <= 2) {
            return theList;
        } else {
            T s = theList.get(0);
            T e = theList.get(theList.size() - 1);
            Predicate<? super T> pp = predicate.or(p -> (p == s || p == e));
            return filter(theList, pp);
        }
    }

    /**
     * 元素转换
     */
    public static <T, R> List<R> map(List<T> theList, Function<T, R> function) {
        return theList.stream().map(function).collect(Collectors.toList());
    }

    public static <T> void forEach(List<T> theList, Consumer<? super T> consumer) {
        theList.forEach(consumer);
    }

    public static <T> T first(List<T> theList) {
        if (isEmpty(theList)) {
            return null;
        }
        return theList.get(0);
    }

    public static <T> T last(List<T> theList) {
        if (isEmpty(theList)) {
            return null;
        }
        return theList.get(theList.size() - 1);
    }

    public static <T> T popFirst(List<T> theList) {
        if (isEmpty(theList)) {
            return null;
        }
        return theList.remove(0);
    }

    public static <T> T popLast(List<T> theList) {
        if (isEmpty(theList)) {
            return null;
        }
        return theList.remove(theList.size() - 1);
    }

}
