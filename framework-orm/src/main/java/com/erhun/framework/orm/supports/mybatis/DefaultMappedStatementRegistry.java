package com.erhun.framework.orm.supports.mybatis;

import com.erhun.framework.orm.IGenericDAO;
import com.erhun.framework.orm.dialect.Dialect;
import org.apache.ibatis.session.Configuration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public class DefaultMappedStatementRegistry implements BeanDefinitionRegistryPostProcessor{

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        addDefaultMappedStatements(beanFactory);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

    }

    @SuppressWarnings("rawtypes")
    private static void addDefaultMappedStatements(ConfigurableListableBeanFactory beanFactory) {

        Map <String, Configuration> configuration = beanFactory.getBeansOfType(Configuration.class);

        for(Map.Entry<String, Configuration> cf : configuration.entrySet()) {

            Dialect dialect = beanFactory.getBean(Dialect.class);
            String names [] = beanFactory.getBeanNamesForType(IGenericDAO.class);

            for (int i = 0; i < names.length; i++) {
               ScannedGenericBeanDefinition bd = (ScannedGenericBeanDefinition) beanFactory.getBeanDefinition(names[i]);
                String name = bd.getMetadata().getClassName();
                try {
                    Class daoClass = Class.forName(name);
                    //接口必须要实例化之后才能获取到泛型 信息。暂时这样处理，之后再优化
                    Object obj = Proxy.newProxyInstance(DefaultMappedStatementRegistry.class.getClassLoader(), new Class[]{daoClass}, new InvocationHandler(){
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            return null;
                        }});
                    MybatisBuilder.buildDefaultMappedStatements(cf.getValue(), dialect, obj.getClass().getInterfaces()[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        //beanFactory.getBean(SqlSessionFactoryBean.class);

    }

}
