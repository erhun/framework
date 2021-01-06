package org.erhun.framework.orm;

import org.apache.ibatis.binding.MapperProxy;
import org.apache.ibatis.session.SqlSessionFactory;
import org.erhun.framework.basic.utils.MapUtils;
import org.erhun.framework.orm.dialect.Dialect;
import org.erhun.framework.orm.dialect.MySQLDialect;
import org.erhun.framework.orm.supports.mybatis.MybatisBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2020/10/20
 */
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextRefreshedListener.class);

    private boolean running = false;

    private boolean inited = false;

    private Dialect dialect = new MySQLDialect();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent ev) {
        if(!running && !inited){
            running = true;
            SqlSessionFactory sqlSessionFactory = ev.getApplicationContext().getBean(SqlSessionFactory.class);
            Map<String, IGenericDAO> beans = ev.getApplicationContext().getBeansOfType(IGenericDAO.class);
            if(MapUtils.isNotEmpty(beans)){
                beans.forEach((key, item)->{
                    try {
                        Proxy proxy = ((Proxy) item);
                        Field hField = proxy.getClass().getSuperclass().getDeclaredField("h");
                        hField.setAccessible(true);
                        MapperProxy mapperProxy = (MapperProxy) hField.get(proxy);
                        Field mapperInterfaceField = mapperProxy.getClass().getDeclaredField("mapperInterface");
                        mapperInterfaceField.setAccessible(true);
                        Class clazz = (Class) mapperInterfaceField.get(mapperProxy);
                        MybatisBuilder.buildDefaultMappedStatements(sqlSessionFactory.getConfiguration(), dialect, clazz);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                inited = true;
            }
            running = false;
        }
    }

}
