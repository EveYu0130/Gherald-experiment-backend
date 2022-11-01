package com.gherald.springboot.dao;

import com.gherald.springboot.model.Method;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface MethodRepository extends CrudRepository<Method, String> {
    List<Method> findAllByFileIdAndChangeId(Integer fileId, String changeId);
}
