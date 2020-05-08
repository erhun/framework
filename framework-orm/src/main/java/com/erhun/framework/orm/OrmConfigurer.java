package com.erhun.framework.orm;

import com.erhun.framework.orm.dialect.Dialect;
import com.erhun.framework.orm.dialect.MySQLDialect;
import com.erhun.framework.orm.dialect.OracleDialect;
import com.erhun.framework.orm.identity.IdentityDefinition;
import com.erhun.framework.orm.identity.IdentityManager;
import com.erhun.framework.orm.version.VersionManager;
import com.erhun.framework.basic.utils.string.StringUtils;
import org.redisson.Redisson;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrmConfigurer {

	@Value("${framework.orm.identity.redisson.uri}")
	private String identityRedissonUri;

	@Value("${framework.orm.identity.redisson.password}")
	private String identityRedissonPassword;

	@Value("${framework.orm.version.redisson.uri}")
	private String versionRedissonUri;

	@Value("${framework.orm.version.redisson.password}")
	private String versionRedissonPassword;

	@Bean("identityRedisson")
	public Redisson getIdentityRedisson() {
		// 注意此处的编解码器
		Codec codec = new JsonJacksonCodec();
		Config configuration = new Config();
		configuration.setCodec(codec);
		if (StringUtils.isBlank(identityRedissonPassword)) {
			configuration.useSingleServer().setAddress(identityRedissonUri).setConnectionMinimumIdleSize(5);
		} else {
			configuration.useSingleServer().setAddress(identityRedissonUri).setPassword(identityRedissonPassword).setConnectionMinimumIdleSize(5);
		}

		Redisson redisson = (Redisson) Redisson.create(configuration);
		return redisson;
	}

	@Bean
	public IdentityManager getIdentityManager(@Qualifier("identityRedisson") Redisson identityRedisson) {
		IdentityDefinition definition = new IdentityDefinition(10, 53);
		IdentityManager manager = new IdentityManager(definition, identityRedisson, 1000L);
		return manager;
	}

	@Bean("versionRedisson")
	public Redisson getVersionRedisson() {
		// 注意此处的编解码器
		Codec codec = new JsonJacksonCodec();
		Config configuration = new Config();
		configuration.setCodec(codec);
		if (StringUtils.isBlank(versionRedissonPassword)) {
			configuration.useSingleServer().setAddress(versionRedissonUri).setConnectionMinimumIdleSize(5);
		} else {
			configuration.useSingleServer().setAddress(versionRedissonUri).setPassword(versionRedissonPassword).setConnectionMinimumIdleSize(5);
		}
		Redisson redisson = (Redisson) Redisson.create(configuration);
		return redisson;
	}

	@Bean
	public VersionManager getVersionManager(@Qualifier("versionRedisson") Redisson versionRedisson) {
		VersionManager manager = new VersionManager(versionRedisson);
		return manager;
	}

	@Bean
	public Dialect frmDialect(@Value("${spring.datasource.driver-class-name}") String driverClassName){
		Dialect dialect = null;
		if(driverClassName.startsWith("com.mysql")){
			dialect = new MySQLDialect();
		}
		if(driverClassName.startsWith("oracle.jdbc")){
			dialect = new OracleDialect();
		}
		SQLUtils.setDialect(dialect);
		return dialect;
	}

}
