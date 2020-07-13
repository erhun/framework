package org.erhun.framework.orm.test.entities;

import org.erhun.framework.orm.annotations.IdGenerator;
import org.erhun.framework.orm.annotations.IdentityType;
import org.erhun.framework.orm.annotations.Join;
import org.erhun.framework.orm.annotations.Table;
import org.erhun.framework.orm.entities.IEntity;

import java.time.Instant;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2019-10-12
 */
@Table("t_test")
@IdGenerator(type = IdentityType.AUTO_INCREMENT, value="snowflakeIdApi")
public class TestEntity implements IEntity<Long> {

    private Long id;

    private String text;

    private Instant createTime;

    @Join(clazz = TestEntity2.class, key="id")
    private Long testId;

    @Join(clazz = TestEntity2.class, value="text")
    private String testText;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public String getTestText() {
        return testText;
    }

    public void setTestText(String testText) {
        this.testText = testText;
    }
}
