package cn.enilu.xuanyuan.core.util;


import java.util.*;

/**
 * Map 工具类
 *
 * @Author enilu
 *
 */
public final class Maps {

    private Maps() {
    }

    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<K, V>(100);
    }

    public static <K, V> HashMap<K, V> newHashMap(K k, V v) {
        HashMap<K, V> map = new HashMap<K, V>(100);
        map.put(k, v);
        return map;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> HashMap<K, V> newHashMap(K k, V v,
                                                  Object... extraKeyValues) {
        if (extraKeyValues.length % 2 != 0) {
            throw new IllegalArgumentException();
        }
        HashMap<K, V> map = new HashMap<K, V>(100);
        map.put(k, v);
        for (int i = 0; i < extraKeyValues.length; i += 2) {
            k = (K) extraKeyValues[i];
            v = (V) extraKeyValues[i + 1];
            map.put(k, v);
        }
        return map;
    }
    /**
     * 对map进行排序
     * @param map
     * @param reverse 是否反转，：true:倒序排列，false:正序排列
     * @return
     */
    public static LinkedHashMap sort(Map<String, Object> map,final boolean reverse) {
        //先转成ArrayList集合
        ArrayList<Map.Entry<String, Object>> list =
                new ArrayList<Map.Entry<String, Object>>(map.entrySet());

        //从小到大排序
        Collections.sort(list, new Comparator<Map.Entry<String, Object>>() {
            @Override
            public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                if(reverse){
                    return o2.getKey().compareTo(o1.getKey());
                }else {
                    return o1.getKey().compareTo(o2.getKey());
                }
            }

        });

        //新建一个LinkedHashMap，把排序后的List放入
        LinkedHashMap<String, Object> map2 = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : list) {
            map2.put(entry.getKey(), entry.getValue());
        }
        return map2;
    }
}