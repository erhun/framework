package com.erhun.framework.orm.namegenerator.impl;



import com.erhun.framework.basic.utils.datetime.DateFormatUtils;
import com.erhun.framework.orm.namegenerator.TableNameGenerator;

import java.util.Date;

/**
 * @author weichao<gorilla@aliyun.com>
 */
public class MonthTableNameGenerator implements TableNameGenerator {

    @Override
    public String generate(String tableName) {
        return tableName + "_" + DateFormatUtils.format(new Date(), "YYYYMM");
    }
    
}
