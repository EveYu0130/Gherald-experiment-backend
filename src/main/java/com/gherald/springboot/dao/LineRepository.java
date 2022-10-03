package com.gherald.springboot.dao;

import com.gherald.springboot.model.Line;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface LineRepository extends CrudRepository<Line, String> {
    List<Line> findAllByFileIdAndChangeId(Integer fileId, String changeId);
}
