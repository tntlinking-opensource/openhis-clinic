package com.geeke.medicareutils.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

/**
 * @Description
 * @Author Hzx
 * @Date 2024/10/31
 */
public class SignUtil {
    private final  static List<String> ignoreSign = Arrays.asList("signData", "encData", "extra");

    public static String getSignText(JSONObject jsonObject, String appSecret) {
        SortedMap<String, String> signMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        for (String key : jsonObject.keySet()) {
            if (jsonObject.get(key) != null && !ignoreSign.contains(key)) {
                signMap.put(key, getValue(jsonObject.get(key)));
            }
        }

        List<String> list = new ArrayList<>();

        for (Map.Entry<String, String> entry : signMap.entrySet()) {
            list.add(entry.getKey() + "=" + entry.getValue() + "&");
        }

        list.sort(String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();

        for (String s : list) {
            sb.append(s);
        }

        return sb.append("key=").append(appSecret).toString();
    }
    private static String treeJsonParam(Object value) {
        String jsonParam = null;

        if (value instanceof Map<?, ?>) {
            SortedMap<String, Object> treeNestedMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            Map<?, ?> nestedMap = (Map<?, ?>) value;

            for (Object key : nestedMap.keySet()) {
                treeNestedMap.put(key.toString(), nestedMap.get(key));
            }
            jsonParam = JSONObject.toJSONString(treeParams(treeNestedMap));
        } else if (value instanceof List<?>) {
            List<Object> ar = (List<Object>) value;
            if (ar != null && !ar.isEmpty())
                jsonParam = JSONArray.toJSONString(treeList(ar));
        } else if (value instanceof JSONObject) {
            SortedMap<String, Object> treeNestedMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            JSONObject nestedMap = (JSONObject) value;
            for (String key : nestedMap.keySet()) {
                treeNestedMap.put(key, nestedMap.get(key));
            }
            jsonParam = JSONObject.toJSONString(treeParams(treeNestedMap));
        } else if (value instanceof JSONArray) {
            JSONArray ar = (JSONArray) value;
            if (ar != null && !ar.isEmpty())
                jsonParam = JSONArray.toJSONString(treeJsonArray(ar));
        } else if (value instanceof com.alibaba.fastjson.JSON) {
            Object jval = value;
            if (jval != null && !jval.toString().isEmpty()) {
                String valStr = jval.toString().toLowerCase().trim();
                jsonParam = (valStr.equals("true") || valStr.equals("false")) ? valStr : jval.toString();
            }
        } else if (value instanceof Map.Entry) {
            Map.Entry<String, Object> nestedMap = (Map.Entry<String, Object>) value;
            SortedMap<String, Object> treeNestedMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            treeNestedMap.put(nestedMap.getKey(), nestedMap.getValue());
            jsonParam = JSONObject.toJSONString(treeParams(treeNestedMap));
        } else {
            jsonParam = value != null ? value.toString() : null;
        }

        return jsonParam;
    }
    private static SortedMap<String, Object> treeParams(SortedMap<String, Object> param) {
        if (param == null) {
            return new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        } else {
            SortedMap<String, Object> treeParam = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

            for (Map.Entry<String, Object> entry : param.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof Map<?, ?>) {
                    SortedMap<String, Object> treeNestedMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                    Map<?, ?> nestedMap = (Map<?, ?>) value;

                    for (Object nestedKey : nestedMap.keySet()) {
                        treeNestedMap.put(nestedKey.toString(), nestedMap.get(nestedKey));
                    }
                    treeParam.put(key, treeParams(treeNestedMap));
                } else if (value instanceof List<?>) {
                    List<Object> ar = (List<Object>) value;
                    if (ar != null && !ar.isEmpty())
                        treeParam.put(key, treeList(ar));
                } else if (value instanceof JSONArray) {
                    JSONArray ar = (JSONArray) value;
                    if (ar != null && !ar.isEmpty())
                        treeParam.put(key, treeJsonArray(ar));
                } else if (value instanceof JSONObject) {
                    SortedMap<String, Object> treeNestedMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                    JSONObject nestedMap = (JSONObject) value;

                    for (String nestedKey : nestedMap.keySet()) {
                        treeNestedMap.put(nestedKey, nestedMap.get(nestedKey));
                    }
                    treeParam.put(key, treeParams(treeNestedMap));
                } else if (value instanceof com.alibaba.fastjson.JSON) {
                    Object jval = value;
                    if (jval != null && !jval.toString().isEmpty()) {
                        String valStr = jval.toString().toLowerCase().trim();
                        treeParam.put(key, valStr.equals("true") || valStr.equals("false") ? valStr : jval.toString());
                    }
                } else if (value != null && !"".equals(value)) {
                    treeParam.put(key, value.toString());
                }
            }
            return treeParam;
        }
    }

    public static String getObjString(Object obj) {
        return obj == null ? "" : (String) obj;
    }

    private static String getValue(Object value) {
        return (value instanceof String) ? getObjString(value) : treeJsonParam(value);
    }
    private static List<Object> treeList(List<Object> list) {
        if (list != null && !list.isEmpty()) {
            JSONArray jsonArray = new JSONArray();

            for (Object item : list) {
                jsonArray.add(item);
            }

            return treeJsonArray(jsonArray);
        } else {
            return null;
        }
    }
    private static List<Object> treeJsonArray(JSONArray jarr) {
        if (jarr != null && !jarr.isEmpty()) {
            List<Object> jsonArray = new ArrayList<>();

            for (Object value : jarr) {
                if (value instanceof List<?>) {
                    List<Object> ar = (List<Object>) value;
                    if (ar != null && !ar.isEmpty()) {
                        jsonArray.add(treeList(ar));
                    }
                } else if (value instanceof JSONArray) {
                    JSONArray ar = (JSONArray) value;
                    if (ar != null && !ar.isEmpty()) {
                        jsonArray.add(treeJsonArray(ar));
                    }
                } else if (value instanceof JSONObject) {
                    SortedMap<String, Object> treeNestedMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                    JSONObject nestedMap = (JSONObject) value;
                    for (String key : nestedMap.keySet()) {
                        treeNestedMap.put(key, nestedMap.get(key));
                    }
                    jsonArray.add(treeParams(treeNestedMap));
                } else if (value instanceof com.alibaba.fastjson.JSON) {
                    Object jval = value;
                    if (jval != null && !jval.toString().isEmpty()) {
                        String valStr = jval.toString().toLowerCase().trim();
                        if (valStr.equals("true") || valStr.equals("false")) {
                            jsonArray.add(valStr);
                        } else {
                            jsonArray.add(jval.toString());
                        }
                    }
                } else if (value instanceof Map.Entry) {
                    Map.Entry<String, Object> nestedMap = (Map.Entry<String, Object>) value;
                    SortedMap<String, Object> treeNestedMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                    treeNestedMap.put(nestedMap.getKey(), nestedMap.getValue());
                    jsonArray.add(treeParams(treeNestedMap));
                } else if (!"".equals(value)) {
                    jsonArray.add(value.toString());
                }
            }

            return jsonArray;
        } else {
            return null;
        }
    }





}
