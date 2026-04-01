package com.damien.campusordering.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置类。
 */
@Configuration
@Slf4j
public class RedisConfiguration {

    /**
     * 创建一个可直接注入使用的 RedisTemplate。
     *
     * @param redisConnectionFactory Spring 自动创建的 Redis 连接工厂
     * @return 配好序列化方式的 RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 打印日志，方便启动时确认 Redis 模板已经创建成功
        log.info("开始创建redis模板对象...");

        // 创建一个 RedisTemplate，用来操作 Redis
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // 把 Spring 提供的 Redis 连接工厂交给模板，这样模板才能真正连上 Redis
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // key 统一使用字符串序列化，方便我们在 Redis 里直接看到 key 的内容
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // hash 的 key 也统一使用字符串序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // value 使用 JSON 序列化，方便存对象，也避免只能存字符串
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // hash 的 value 也使用 JSON 序列化，保证数据结构统一
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 让上面的序列化配置正式生效
        redisTemplate.afterPropertiesSet();

        // 把配置好的模板交给 Spring 容器管理
        return redisTemplate;
    }
}
