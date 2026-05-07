package com.lt.boot.utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 集合工具类
 */
public class CollUtils {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static <T> List<T> emptyList() {
        return Collections.emptyList();
    }

    public static <T> Set<T> emptySet() {
        return Collections.emptySet();
    }

    public static <K, V> Map<K, V> emptyMap() {
        return Collections.emptyMap();
    }

    public static <T> Set<T> singletonSet(T t) {
        return Collections.singleton(t);
    }

    public static <T> List<T> singletonList(T t) {
        return Collections.singletonList(t);
    }

    public static List<Integer> convertToInteger(List<String> originList) {
        return isNotEmpty(originList) ? originList.stream().map(Integer::parseInt).collect(Collectors.toList()) : null;
    }

    public static List<Long> convertToLong(List<String> originList) {
        return isNotEmpty(originList) ? originList.stream().map(Long::parseLong).collect(Collectors.toList()) : null;
    }

    public static <T> String join(Collection<T> collection, CharSequence conjunction) {
        if (isEmpty(collection)) {
            return null;
        }
        return collection.stream().map(String::valueOf).collect(Collectors.joining(conjunction));
    }

    public static <T> String joinIgnoreNull(Collection<T> collection, CharSequence conjunction) {
        if (isEmpty(collection)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (T t : collection) {
            if (t != null) {
                if (sb.length() > 0) {
                    sb.append(conjunction);
                }
                sb.append(t);
            }
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    @SafeVarargs
    public static <T> void add(Collection<T> list, T... data) {
        if (list == null || data == null || data.length == 0) {
            return;
        }
        for (T t : data) {
            if (t != null) {
                list.add(t);
            }
        }
    }
}
