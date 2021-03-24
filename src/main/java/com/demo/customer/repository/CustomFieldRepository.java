package com.demo.customer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CustomFieldRepository extends CrudRepository<CustomField, Long> {
	@Query("SELECT f FROM CustomField f WHERE f.active = true")
    List<CustomField> findActiveFields();
}
