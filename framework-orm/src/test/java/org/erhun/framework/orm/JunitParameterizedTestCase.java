package org.erhun.framework.orm;

import org.apache.ibatis.session.SqlSessionFactory;
import org.erhun.framework.basic.utils.json.JsonUtils;
import org.erhun.framework.orm.test.dao.TestDAO;
import org.erhun.framework.orm.test.entities.TestEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.runners.Parameterized.Parameters;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2019-10-12
 */
@RunWith(Parameterized.class)
public class JunitParameterizedTestCase {

    private Long id;

    private String text;

    private Instant createTime;

    @Parameters
    public static Collection parameters(){
        return Arrays.asList(new Object[][]{
                {1L, "test1", Instant.now()},
                {2L, "test2", Instant.now()},
                {3L, "test3", Instant.now()},
                {4L, "test4", Instant.now()},
                {5L, "test5", Instant.now()}
        });
    }

    public JunitParameterizedTestCase(Long id, String text, Instant createTime){
        this.id = id;
        this.text = text;
        this.createTime = createTime;
    }

    @Test
    public void initTestData() throws SQLException {

        TestEntity testEntity = new TestEntity();
        testEntity.setId(id);
        testEntity.setText(text);
        testEntity.setCreateTime(createTime);

        System.out.println(JsonUtils.toJSONString(testEntity));

    }

}
