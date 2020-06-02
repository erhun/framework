package org.erhun.framework.orm;

import org.erhun.framework.orm.dialect.Dialect;
import org.erhun.framework.orm.dialect.MySQLDialect;
import org.erhun.framework.orm.dialect.OracleDialect;
import org.erhun.framework.orm.supports.mybatis.MybatisMappedStatementRegistry;
import org.erhun.framework.orm.supports.mybatis.config.MybatisConfigurationImpl;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrmConfigurer {

	@Bean
	public Dialect frmDialect(@Value("${spring.datasource.driver-class-name}") String driverClassName){
		Dialect dialect = null;
		if(driverClassName.startsWith("com.mysql")){
			dialect = new MySQLDialect();
		}
		if(driverClassName.startsWith("oracle.jdbc")){
			dialect = new OracleDialect();
		}
		if(dialect == null){
			dialect = new MySQLDialect();
		}
		SQLUtils.setDialect(dialect);
		return dialect;
	}

	@Bean
	public org.apache.ibatis.session.Configuration mybatisConfiguration(){
		org.apache.ibatis.session.Configuration configuration = new MybatisConfigurationImpl();
		configuration.setMapUnderscoreToCamelCase(true);
		return configuration;
	}

	@Bean
	public MybatisMappedStatementRegistry mybatisMappedStatementRegistry(){
		MybatisMappedStatementRegistry mmsr = new MybatisMappedStatementRegistry();
		return mmsr;
	}

}
