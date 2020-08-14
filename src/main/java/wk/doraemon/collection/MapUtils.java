package wk.doraemon.collection;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Map工具类
 * Created by TF on 2019/1/4.
 */
public class MapUtils {

    public static <K,V> boolean isBlank(Map<K,V> theMap) {
        return theMap==null || theMap.isEmpty();
    }

    /**
     * Take care the map type should be LinkedHashMap
     * */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> theMap) {
        if(isBlank(theMap)) return theMap;
        LinkedHashMap<K,V> resultMap = new LinkedHashMap<>();
        theMap.entrySet().stream().sorted(Comparator.comparing(Entry::getValue))
                .collect(Collectors.toList()).forEach(e -> resultMap.put(e.getKey(),e.getValue()));
        return resultMap;
    }

    /**
     * Take care the map type should be LinkedHashMap
     * */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> theMap, boolean inv) {
        if(isBlank(theMap)) return theMap;
        LinkedHashMap<K,V> resultMap = new LinkedHashMap<>();
        theMap.entrySet().stream().sorted((e1, e2) -> (inv?-1:1)*(e1.getValue().compareTo(e2.getValue())))
                .collect(Collectors.toList()).forEach(e -> resultMap.put(e.getKey(),e.getValue()));
        return resultMap;
    }

    public static <K,V> Map.Entry<K,V> headEntry(Map<K,V> theMap) {
        if(isBlank(theMap)) return null;
        return theMap.entrySet().stream().findFirst().get();
    }

    public static <K,V> K headKey(Map<K,V> theMap) {
        if(isBlank(theMap)) return null;
        return headEntry(theMap).getKey();
    }

    public static <K,V> V headValue(Map<K,V> theMap) {
        if(isBlank(theMap)) return null;
        return headEntry(theMap).getValue();
    }

    public static <K,V extends Comparable<? super V>> V maxValue(Map<K,V> theMap) {
        if(isBlank(theMap)) return null;
        return theMap.entrySet().stream().max(Comparator.comparing(Entry::getValue)).get().getValue();
    }

    public static <K,V> Map<K,V> filter(Map<K,V> theMap, Predicate<Entry<K,V>> predicate) {
        if(isBlank(theMap)) return theMap;
        return theMap.entrySet().stream().filter(predicate)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    public static <K,V> Map<K,V> merge(Map<K,V> theMap1, Map<K,V> theMap2, BinaryOperator<V> mergeFunction) {
        return Stream.concat(theMap1.entrySet().stream(),theMap2.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, mergeFunction));
    }

    public static void main(String[] args) {
        LinkedHashMap<String,Integer> theMap = new LinkedHashMap<>();
        theMap.put("wangke",4);
        theMap.put("zhangsu",2);
        theMap.put("xujiang",3);

        LinkedHashMap<String,Integer> theMap2 = new LinkedHashMap<>();
        theMap2.put("wangke",4);
        theMap2.put("zhangsu",2);
        theMap2.put("xujiang",3);

        Map<String,Integer> theMap3 = merge(theMap,theMap2,(v1,v2)->v1+v2);
        theMap3 = sortByValue(theMap3,true);
        System.out.println(theMap3);
        System.out.println(headValue(theMap3));

    }

}
