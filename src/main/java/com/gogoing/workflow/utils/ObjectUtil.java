package com.gogoing.workflow.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gogoing.workflow.exception.ProcessException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lhj
 * @version 1.0
 * @description: 对象转换工具
 * @date 2020-5-17 20:35
 */
@Slf4j
public class ObjectUtil {


    private ObjectUtil() {
    }

    /**
     * 对象转map
     *
     * @param object
     * @param <T>
     * @return
     */
    public static <T> Map<String, Object> bean2Map(T object) {
        Map<String, Object> map = new HashMap<>();
        if (null != object) {
            BeanMap beanMap = BeanMap.create(object);
            for (Object key : beanMap.keySet()) {
                map.put(key + "", beanMap.get(key));
            }
        }
        return map;
    }

    /**
     * 将map转成java bean
     *
     * @param map
     * @param bean
     * @param <T>
     * @return
     */
    public static <T> T map2Bean(Map<String, Object> map, T bean) {
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }


    /**
     * 将list 转成 map
     *
     * @param objList
     * @param <T>
     * @return
     */
    public static <T> List<Map<String, Object>> list2Map(List<T> objList) {
        List<Map<String, Object>> list = Lists.newArrayList();
        if (CollectionUtil.isNotEmpty(list)) {
            Map<String, Object> map;
            T bean;
            for (T t : objList) {
                bean = t;
                map = bean2Map(bean);
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 将List<Map<String,Object>>转换为List<T>
     *
     * @param maps
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> List<T> map2List(List<Map<String, Object>> maps, Class<T> clazz) throws IllegalAccessException,
            InstantiationException {
        List<T> list = Lists.newArrayList();
        if (CollectionUtil.isNotEmpty(list)) {
            Map<String, Object> map;
            T bean;
            for (Map<String, Object> stringObjectMap : maps) {
                map = stringObjectMap;
                bean = clazz.newInstance();
                map2Bean(map, bean);
                list.add(bean);
            }
        }
        return list;
    }


    public static JSONObject parseObject2Json(Object obj){
        return  JSONUtil.parseObj(obj);
    }

    public static Map<String,Object>  jsonStr2Map(JSONObject str){
        Map<String, Object> map = parseJSON2Map(str);
        log.info("map数据：{}",map);
        return map;
    }

    public static Map<String, Object> parseJSON2Map(JSONObject json) {
        Map<String, Object> map = new HashMap<String, Object>();
        // 最外层解析
        for (Object k : json.keySet()) {
            Object v = json.get(k);
            // 如果内层还是json数组的话，继续解析
            if (v instanceof cn.hutool.json.JSONArray) {
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                Iterator<Object> it = ((cn.hutool.json.JSONArray) v).iterator();
                while (it.hasNext()) {
                    JSONObject json2 = (JSONObject) it.next();
                    list.add(parseJSON2Map(json2));
                }
                map.put(k.toString(), list);
            } else if (v instanceof JSONObject) {
                // 如果内层是json对象的话，继续解析
                map.put(k.toString(), parseJSON2Map((JSONObject) v));
            } else {
                // 如果内层是普通对象的话，直接放入map中
                map.put(k.toString(), v);
            }
        }
        return map;
    }

    public static Map<String, Object> parseJSON2Map(String jsonStr) {
        if(JSONUtil.isJson(jsonStr)){
           return jsonStr2Map(parseObject2Json(jsonStr));
         }
        throw new ProcessException("不是JSON字符，不支持转成Json");
    }

    /**
     * 设置请求参数值
     * @param params 参数列表
     * @return
     */
    public static Map<String, Object> setRequestParam(Map<String, Object> params) {
        if (!CollectionUtils.isEmpty(params)) {
            return paramObjectHandle(params);
        }
        return Collections.emptyMap();
    }

    /**
     * 将a.b.c.d转成{a:{b:{c:{d:xxx}}}}的map类型
     * 1.首先将所有参数的map的key进行打平数组
     * 2.根据key的点进行对象属性切分
     * 3.再将数组各个参数进行map组装
     * 如下map{'headers.age':2,'processDeployment.status':'age','headers.processDeployment.id':2,'headers.processDeployment.name':'lisi','headers.processDeployment.code':123a23'}
     *
     * @param params 入参信息
     * @return
     */
    public static Map<String, Object> paramObjectHandle(Map<String, Object> params) {
        Set<String> strings = params.keySet();

        List<String[]> collect = strings.stream().map(item -> item.split("\\.")).collect(Collectors.toList());
        Integer max = collect.stream().map(a -> a.length).max(Comparator.naturalOrder()).orElse(0);
        Map<String, Object> result = new HashMap<>();
        int index = 0;
        while (true) {
            for (int i = 0; i < collect.size(); i++) {
                String[] data = collect.get(i);
                int length = data.length;
                if (index == length - 1 && length != 1) {
                    Map betweenMap = null;
                    StringBuilder key = new StringBuilder();
                    for (int j = 0; j <= index; j++) {
                        key.append(data[j]).append(".");
                        if (j == 0) {
                            betweenMap = (Map) result.get(data[0]);
                        } else {
                            if (j == length - 1) {
                                String substring = key.substring(0, key.length() - 1);
                                betweenMap.put(data[index], params.get(substring));
                            } else {
                                Object o = betweenMap.get(data[j]);
                                if (null == o) {
                                    betweenMap.put(data[j], new HashMap<>());
                                } else {
                                    betweenMap = (Map) o;
                                }
                            }
                        }
                    }
                } else if (length == 1 && index == 0) {
                    result.put(data[index], params.get(data[index]));
                } else {
                    if (index > length - 2) {
                        continue;
                    }
                    Map mapValue = null;
                    for (int j = 0; j <= index; j++) {
                        String mapKey = data[j];
                        Object o = result.get(mapKey);
                        if (j == 0) {
                            if (null == o) {
                                result.put(mapKey, new HashMap<>());
                            } else {
                                mapValue = (Map) o;
                            }
                        } else {
                            Object o1 = mapValue.get(mapKey);
                            if (null == o1) {
                                mapValue.put(mapKey, new HashMap<>());
                            } else {
                                mapValue = (Map) o1;
                            }
                        }
                    }
                }
            }
            if (max == index + 1) {
                break;
            }
            index++;
        }
        return result;
    }

    /**
     * 处理map中key包含的特殊字符
     * @param mapEl
     * @return
     */
    public static Map<String, Object> patternSpecialMap(Map<String, Object> mapEl) {
        HashMap<String, Object> newMap = new HashMap<>();
        Set<Map.Entry<String, Object>> entries = mapEl.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            //处理特殊字符
            String key =patternParam(entry.getKey());
            Object value = entry.getValue();
            if(value instanceof Map){
                value = patternSpecialMap((Map)value);
            }
            // 往newMap中放入新的Entry
            newMap.put(key,value);
        }
        return newMap;
    }

    /**
     * 替换字符串中的"-"，不包含#
     * @param paramEL
     * @return
     */
    public static String patternParam(String paramEL) {
        return paramEL.replaceAll("-","");
    }
}
