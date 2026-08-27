package sample.common.dao.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import sample.common.dao.entity.Task;

public interface TaskArchiveMapper {

    List<Task> selectArchiveTargets(@Param("thresholdDate") LocalDate thresholdDate);

    void insertArchive(@Param("task") Task task);

    void deleteById(@Param("id") Long id);
    
}