package org.cokeke.policysim.external;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RedisConfig {
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        var typeValidator = BasicPolymorphicTypeValidator.builder()
            .allowIfBaseType(Object.class)
            .build();

        GenericJacksonJsonRedisSerializer serializer = GenericJacksonJsonRedisSerializer.create(builder ->
            builder
                .enableDefaultTyping(typeValidator)
                .enableSpringCacheNullValueSupport()
        );

        return RedisCacheConfiguration.defaultCacheConfig()
            .serializeValuesWith(SerializationPair.fromSerializer(serializer));
    }

    @Bean 
    public RedisCacheManager cacheManager(
        RedisConnectionFactory connectionFactory,
        RedisCacheConfiguration cacheConfiguration) {

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(cacheConfiguration)
            .enableStatistics()
            .build();
    }
}
