package com.erhun.framework.orm;

import com.erhun.framework.orm.test.dao.TestDao;
import com.erhun.framework.orm.test.entities.TestEntity;
import org.junit.Test;

import java.time.Instant;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2019-10-12
 */
public class CriteriaTest {

    private TestDao testDao;

    @Test
    public void testCriteria() {

       String string = Criteria.fetch()
               .eq(TestEntity::getText, "334343")
               .eq(TestEntity::getText, "")
               .eq(TestEntity::getId, 1)
               .or()
               .eq(TestEntity::getText, "")
               .le(TestEntity::getText, "1")
               .or()
               .gt(TestEntity::getText, 2)
               .like(TestEntity::getText, "test")
               .notLike(TestEntity::getText, "gg")
               .or()
               .startLike(TestEntity::getText, "22")
               .endLike(TestEntity::getText, "222")
               .lt(TestEntity::getText, 2)
               .toString();


        System.out.println(string);


    }

    @Test
    public void testCriteria2() {

        Criteria.fetch().eq(TestEntity::getId, 33).groupBy(TestEntity::getId).orderByAsc(TestEntity::getId).limit(1).forUpdate();

        Criteria criteria = Criteria.fetch()
                .eq(TestEntity::getText, "334343")
                .eq(TestEntity::getCreateTime, Instant.now())
                ;

        //testDao.findByCriteria(criteria);

        System.out.println(criteria);


    }
}
