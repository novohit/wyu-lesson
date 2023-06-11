package com.wyu.wyulesson.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @author zwx
 * @date 2022-10-23 20:53
 */
@Configuration
public class RedisConfig {
    // 序列化方案默认用的是jdk 存字符串的时候会出现乱码前缀 会影响命令行获取key的使用
    //RedisTemplate redisTemplate; // 可以存对象

    //StringRedisTemplate stringRedisTemplate; // 不能存对象

    /**
     * 2.7.x版本会显示爆红 Spring没注入RedisConnectionFactory 应该是识别问题不用管
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        // GenericJackson2JsonRedisSerializer保存了包名
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        //设置string key和value序列器
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        //设置hash key和value序列器
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        //设置 RedisConnection 工厂 它就是实现多种 Java Redis 客户端接入的工厂
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }

    @Bean
    public DefaultRedisScript<Long> limitScript() {
        DefaultRedisScript<Long> limitScript = new DefaultRedisScript<>();
        // 设置脚本返回的类型
        limitScript.setResultType(Long.class);
        // 设置脚本位置 根目录从resource开始
        limitScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/limit.lua")));
        return limitScript;
    }
}
