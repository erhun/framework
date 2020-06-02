package org.erhun.framework.orm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2020/6/2
 */
@SpringBootApplication(scanBasePackages = {"org.erhun"})
@MapperScan(basePackages = {"org.erhun.framework.orm.test.dao"})
public class OrmApplication {
}
