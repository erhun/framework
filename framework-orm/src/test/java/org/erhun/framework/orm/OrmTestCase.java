package org.erhun.framework.orm;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.erhun.framework.basic.utils.ArrayUtils;
import org.erhun.framework.basic.utils.PV;
import org.erhun.framework.basic.utils.PageResult;
import org.erhun.framework.basic.utils.json.JsonUtils;
import org.erhun.framework.orm.test.dao.TestDAO;
import org.erhun.framework.orm.test.entities.TestEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.Assert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2019-10-12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = OrmApplication.class)
public class OrmTestCase {

    @Autowired
    private TestDAO testDao;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;


    @Before
    public void init() throws SQLException {
        createTable();
        initTestData();
    }

    private void createTable() throws SQLException {
        SqlSession sqlSession = sqlSessionFactory.openSession();

        Connection connection = sqlSession.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("create table t_test(Id int primary key,text varchar,create_time datetime, test_id bigint,test_text varchar)");
            preparedStatement.execute();
        }catch (Exception ex){
            ex.printStackTrace();;
        }finally {
            preparedStatement.close();
            connection.close();
        }
    }

    public void initTestData() throws SQLException {

        TestEntity testEntity = new TestEntity();
        testEntity.setId(1L);
        testEntity.setText("test1");
        testEntity.setCreateTime(Instant.now());

        testDao.add(testEntity);

        testEntity = new TestEntity();
        testEntity.setId(2L);
        testEntity.setText("test1");
        testEntity.setCreateTime(Instant.now());

        testDao.add(testEntity);

        testEntity = new TestEntity();
        testEntity.setId(3L);
        testEntity.setText("test2");
        testEntity.setCreateTime(Instant.now());

        testDao.add(testEntity);

        testEntity = new TestEntity();
        testEntity.setId(4L);
        testEntity.setText("test2");
        testEntity.setCreateTime(Instant.now());

        testDao.add(testEntity);

        testEntity = new TestEntity();
        testEntity.setId(5L);
        testEntity.setText("test3");
        testEntity.setCreateTime(Instant.now());

        testDao.add(testEntity);

    }

    @Test
    public void testIn() {
        testDao.queryByColumn( PV.of("id", 1));
    }

    @Test
    public void testQueryByPage() {

        QueryParam queryParam = new QueryParam();

       PageResult re = testDao.queryByPage( queryParam, null, Limits.of(1, 10));
        PageResult r2 = testDao.queryByPage( queryParam, null, Criteria.fetch().eq(TestEntity::getId, 2), Limits.of(1, 10));


        //System.out.println(JsonUtils.toJSONString(re));
    }

    @Test
    public void testQueryByNextPage() {

        QueryParam queryParam = new QueryParam();

        PageResult re2 = testDao.queryByNextPage(queryParam, null, Limits.of(1, 2));

        Assert.assertTrue(re2.hasNextPage());

        PageResult re3 = testDao.queryByNextPage(queryParam, null, Limits.of(1, 10));

        Assert.assertFalse(re3.hasNextPage());


        //System.out.println(JsonUtils.toJSONString(re));
    }

    @Test
    public void testCriteria() {

        TestEntity testInfo = testDao.get(1L);

        Assert.assertTrue(testInfo != null);

        System.out.println(JsonUtils.toJSONString(testInfo));

        testInfo = testDao.findByCriteria(Criteria.fetch(TestEntity::getText).eq(TestEntity::getId, 1));

        Assert.assertTrue(testInfo != null);

        System.out.println(JsonUtils.toJSONString(testInfo));

        List<TestEntity> testInfoList = testDao.queryByCriteria(Criteria.fetch(TestEntity::getText).eq(TestEntity::getText, "test1"));

        Assert.assertTrue(testInfoList != null && testInfoList.size() > 1);

        System.out.println(JsonUtils.toJSONString(testInfoList));

        testInfoList = testDao.queryByCriteria(Criteria.fetch(TestEntity::getText).gt(TestEntity::getText, "test1"));

        Assert.assertTrue(testInfoList != null && testInfoList.size() == 3);

        System.out.println(JsonUtils.toJSONString(testInfoList));

        testInfoList = testDao.queryByCriteria(Criteria.fetch(TestEntity::getId, TestEntity::getText).lt(TestEntity::getText, "test1"));

        Assert.assertTrue(testInfoList != null && testInfoList.size() == 0);

        System.out.println(JsonUtils.toJSONString(testInfoList));

        testInfoList = testDao.queryByCriteria(Criteria.fetch(TestEntity::getId, TestEntity::getText).lt(TestEntity::getText, "test2"));

        Assert.assertTrue(testInfoList != null && testInfoList.size() == 2);

        System.out.println(JsonUtils.toJSONString(testInfoList));

        testDao.queryByCriteria(Criteria.fetch().orderByAsc(TestEntity::getId));
        testDao.queryByCriteria(Criteria.fetch().orderByAsc(TestEntity::getCreateTime));

    }


    @Test
    public void testUpdate() {

        TestEntity entity = new TestEntity();

       // entity.setId(1L);
        entity.setText("test1sss");
        testDao.updateByCondition(entity, PV.of("text", "test1"));

        TestEntity entity1 = testDao.get(1L);

        Assert.assertTrue(entity.getText().equals(entity1.getText()));


        Object testInfo = testDao.findByCriteria(Criteria.fetch(TestEntity::getText).gt(TestEntity::getText, "test1").orderByAsc(TestEntity::getText).limit(1).forUpdate());
    }

}
