package com.yujia.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface NumberSequenceMapper {

    @Select("SELECT COUNT(1) FROM pg_class WHERE relkind = 'S' AND relname = #{sequenceName}")
    int countSequence(String sequenceName);

    @Update("CREATE SEQUENCE ${sequenceName} START 1")
    void createSequence(String sequenceName);

    @Select("SELECT nextval(#{sequenceName})")
    long nextValue(String sequenceName);
}
