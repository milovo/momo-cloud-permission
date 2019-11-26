/**
 * Copyright (c) 2018-2019, Jie Li 李杰 (mqgnsds@163.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.momo.netty.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: ChannelManager
 * @Author: Jie Li
 * @Date 2019-11-25 09:27
 * @Description: netty channel 管理
 * @Version: 1.0
 * <p>Copyright: Copyright (c) 2019</p>
 **/
public class ChannelManager {
    //k-->channelId v-->channel
    private final static Map<String, Channel> channelCache = new ConcurrentHashMap<>(64);
    //k-->channelId v-->userId
    private final static Map<String, Long> userIdCache = new ConcurrentHashMap<>(64);
    //用户id和channelId的关系
    private final static Multimap<Long, String> channelIdCache = ArrayListMultimap.create();

    //###########   用户channelId和channel的关系   ###########
    public static Map<String, Channel> getAllChannel() {
        return channelCache;
    }

    public static void putChannel(String key, Channel value) {
        channelCache.put(key, value);
    }

    public static Channel getChannel(String key) {
        return channelCache.get(key);
    }

    public static void removeChannel(String key) {
        channelCache.remove(key);
    }

    public static int sizeChannel() {
        return channelCache.size();
    }
    //###########   用户channelId和用户id的关系   ###########

    public static Map<String, Long> getAllUserId() {
        return userIdCache;
    }

    public static void putUserId(String key, Long userId) {
        userIdCache.put(key, userId);
    }

    public static Long getUserId(String key) {
        return userIdCache.get(key);
    }

    public static void removeUserId(String key) {
        userIdCache.remove(key);
    }

    public static int sizeUserId() {
        return channelCache.size();
    }

    //###########   用户id和channelId的关系   ###########
    public static Map<Long, Collection<String>> getChannelId() {
        Map<Long, Collection<String>> listMap = channelIdCache.asMap();
        return listMap;
    }

    public static void putChannelId(Long key, String channelId) {
        channelIdCache.put(key, channelId);
    }

    public static List<String> getChannelId(Long key) {
        return (List<String>) channelIdCache.get(key);
    }

    public static void removeChannelId(Long key, String channelId) {
        channelIdCache.remove(key, channelId);
    }

    public static void ctxWrite(String key, Object obj) {
        Channel channel = getChannel(key);
        ctxWrite(channel, obj);
    }

    public static void ctxWrite(ChannelHandlerContext ctx, Object obj) {
        Channel channel = ctx.channel();
        ctxWrite(channel, obj);
    }

    public static void ctxWrite(Channel channel, Object obj) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(obj)));
    }

    public static String channelLongText(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        return channel.id().asLongText();
    }

    public static void channelClose(ChannelHandlerContext ctx) {
        String channelId = ChannelManager.channelLongText(ctx);
        Long UserId = ChannelManager.getUserId(channelId);
        channelClose(channelId, UserId);
    }

    public static void channelClose(String channelId, Long UserId) {
        ChannelManager.removeChannel(channelId);
        ChannelManager.removeUserId(channelId);
        ChannelManager.removeChannelId(UserId, channelId);
    }
}
