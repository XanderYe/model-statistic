package cn.xanderye;

import cn.xanderye.entity.Friend;
import cn.xanderye.util.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        String qq = "";
        String skey = "";
        System.out.println("cookie获取方法：打开https://user.qzone.qq.com/,登录账号后，按F12，选择console，输入document.cookie，复制cookie");
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入cookie：");
        String cookieString = scanner.nextLine();
        qq = StringUtils.substringBetween(cookieString, "; uin=", ";");
        skey = StringUtils.substringBetween(cookieString, "skey=", ";");
        if (StringUtils.isEmpty(qq) || StringUtils.isEmpty(skey)) {
            System.out.println("cookie错误！");
        }
        System.out.println();
        Map<String, List<Friend>> friendList = getModelData(qq, skey);
        statistic(friendList);
        System.out.print("请输入机型查询用户：");
        while (scanner.hasNext()) {
            String model = scanner.nextLine();
            if (StringUtils.isNotEmpty(model)) {
                System.out.println(getFriends(friendList, model));
            } else {
                System.out.println("输入错误");
            }
            System.out.print("请输入机型查询用户：");
        }
    }

    /**
     * 根据机型获取用户
     * @param friendList
     * @param model
     * @return java.lang.String
     * @author yezhendong
     * @date 2020-02-29
     */
    public static String getFriends(Map<String, List<Friend>> friendList, String model) {
        String type = getType(model);
        List<Friend> list = friendList.get(type);
        StringBuilder stringBuilder = new StringBuilder();
        for (Friend friend : list) {
            if (friend.getAppName().equals(model)) {
                stringBuilder.append(friend.getName()).append(",");
            }
        }
        if (stringBuilder.length() != 0) {
            return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
        }
        return "无";
    }

    /**
     * 统计方法
     * @param friendList
     * @return void
     * @author yezhendong
     * @date 2020-02-29
     */
    public static void statistic(Map<String, List<Friend>> friendList) {
        System.out.println();
        System.out.println("-------------------统计开始-------------------");
        System.out.println();
        List<Map.Entry<String, List<Friend>>> list = new ArrayList<>(friendList.entrySet());
        list.stream().sorted((s1, s2) -> {
            Integer length1 = s1.getValue().size();
            Integer length2 = s2.getValue().size();
            return length2.compareTo(length1);
        }).forEach(entry -> {
            String key = entry.getKey();
            List<Friend> value = entry.getValue();
            System.out.println(key + ": " + value.size());
            List<String> modelList = value.stream().map(Friend::getAppName).collect(Collectors.toList());
            System.out.println(modelList.toString());
        });
        System.out.println();
        System.out.println("-------------------统计结束-------------------");
        System.out.println();
    }

    /**
     * 获取数据
     * @param uin
     * @param skey
     * @return java.util.Map<java.lang.String,java.util.List<cn.xanderye.entity.Friend>>
     * @author yezhendong
     * @date 2020-02-29
     */
    public static Map<String, List<Friend>> getModelData(String uin, String skey) {
        JSONArray page = getDataByPage(uin, skey, 1);
        JSONArray page2 = getDataByPage(uin, skey, 2);
        page.addAll(page2);
        List<Friend> friendList = new ArrayList<>();
        if (page.size() > 0) {
            for (int i = 0; i < page.size(); i++) {
                JSONObject jsonObject = page.getJSONObject(i);
                String appName = jsonObject.getString("appname");
                Friend friend = new Friend();
                friend.setName(jsonObject.getString("name"));
                friend.setAppName(appName);
                friend.setType(getType(appName));
                friendList.add(friend);
            }
        }
        return friendList.stream().collect(Collectors.groupingBy(Friend::getType));
    }

    /**
     * 转换品牌
     * @param appName
     * @return java.lang.String
     * @author yezhendong
     * @date 2020-02-29
     */
    public static String getType(String appName) {
        if (appName.toLowerCase().contains("iphone")) {
            return "iPhone";
        } else if (appName.toLowerCase().contains("huawei") || appName.toLowerCase().contains("nova") || appName.toLowerCase().contains("华为")) {
            return "华为";
        } else if (appName.toLowerCase().contains("荣耀") || appName.toLowerCase().contains("honor")) {
            return "荣耀";
        } else if (appName.toLowerCase().contains("redmi") || appName.toLowerCase().contains("红米")) {
            return "红米";
        } else if (appName.toLowerCase().contains("mi") || appName.toLowerCase().contains("mix") || appName.toLowerCase().contains("黑鲨") || appName.toLowerCase().contains("小米")) {
            return "小米";
        } else if (appName.toLowerCase().contains("oppo") || appName.toLowerCase().contains("realme")) {
            return "oppo";
        } else if (appName.toLowerCase().contains("vivo") || appName.toLowerCase().contains("iqoo")) {
            return "vivo";
        } else if (appName.toLowerCase().contains("samsung") || appName.toLowerCase().contains("三星")) {
            return "三星";
        } else if (appName.toLowerCase().contains("oneplus") || appName.toLowerCase().contains("一加")) {
            return "一加";
        } else if (appName.toLowerCase().contains("rog")) {
            return "ROG";
        } else {
            return "其他";
        }
    }

    public static JSONArray getDataByPage(String uin, String skey, Integer page) {
        Map<String, Object> headers = new HashMap<>(16);
        headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 10; TNY-AL00 Build/HUAWEITNY-AL00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/66.0.3359.126 MQQBrowser/6.2 TBS/045114 Mobile Safari/537.36 V1_AND_SQ_8.2.7_1334_YYB_D PA QQ/8.2.7.4410 NetType/WIFI WebP/0.3.0 Pixel/1080 StatusBarHeight/72 SimpleUISwitch/0");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Origin", "https://quic.yundong.qq.com");
        Map<String, Object> cookies = new HashMap<>(16);
        cookies.put("uin", uin);
        cookies.put("skey", skey);
        String gTk = getGTK(skey);
        Map<String, Object> params = new HashMap<>(16);
        params.put("dcapiKey", "user_rank");
        params.put("l5apiKey", "rank_friends");
        params.put("params", "{\"cmd\":1,\"pno\":" + page + ",\"dtype\":1,\"pnum\":50}");
        String result = HttpUtil.doPostWithCookie("https://quic.yundong.qq.com/pushsport/cgi/rank/friends?g_tk=" + gTk, headers, cookies, params);
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonObject = JSON.parseObject(result);
            if ("0".equals(jsonObject.getString("code"))) {
                jsonArray = jsonObject.getJSONObject("data").getJSONArray("list");
            } else {
                System.out.println(jsonObject.getString("msg"));
            }
        } catch (Exception ignored) {
        }
        return jsonArray;
    }

    public static String getGTK(String skey) {
        int hash = 5381;
        for (int i = 0; i < skey.length(); ++i) {
            hash += (hash << 5) + skey.charAt(i);
        }
        return String.valueOf(hash & 0x7fffffff);
    }
}
