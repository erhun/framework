package org.erhun.framework.orm.supports.mybatis;

import org.erhun.framework.orm.IGenericDAO;
import org.erhun.framework.orm.dialect.Dialect;
import org.erhun.framework.orm.dialect.MySQLDialect;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * gorilla@aliyun.com
 */
public class MybatisMappedStatementRegistry implements BeanPostProcessor {

    private SqlSessionFactory sqlSessionFactory;

    private Dialect dialect = new MySQLDialect();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (sqlSessionFactory == null && bean instanceof SqlSessionFactory) {
            sqlSessionFactory = (SqlSessionFactory) bean;
        }
        if (bean instanceof MapperFactoryBean) {
            if (sqlSessionFactory != null) {
                MapperFactoryBean mapperFactoryBean = ((MapperFactoryBean) bean);
                Class<IGenericDAO> clazz = mapperFactoryBean.getMapperInterface();
                if (IGenericDAO.class.isAssignableFrom(clazz)) {
                    try {
                        MybatisBuilder.buildDefaultMappedStatements(sqlSessionFactory.getConfiguration(), dialect, clazz);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


}
