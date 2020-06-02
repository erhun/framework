package org.erhun.framework.orm.test.entities;

import org.erhun.framework.orm.annotations.Table;
import org.erhun.framework.orm.entities.IEntity;

import java.time.Instant;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2019-10-12
 */
@Table("t_test")
public class TestEntity implements IEntity<Long> {

    Long id;

    String text;

    Instant createTime;

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
}
