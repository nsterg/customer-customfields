package com.demo.customer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CustomFiedValuesRepository extends CrudRepository<CustomFieldValues, Long> {

	@Query("SELECT f FROM CustomFieldValues f WHERE f.customer = ?1 AND f.id IN (SELECT max(fm.id) FROM CustomFieldValues fm WHERE fm.customer = ?1 GROUP BY fm.customField)")
	List<CustomFieldValues> fetchCustomerCustomFieldsValues(Customer customer);

	@Query("SELECT f FROM CustomFieldValues f WHERE f.customer = ?1 AND f.customField = ?2 AND f.id NOT IN (select max(fm.id) from CustomFieldValues fm WHERE fm.customer = ?1 AND fm.customField = ?2) ORDER BY f.created DESC")
    List<CustomFieldValues> fetchCustomerCustomFieldHistoricValues(Customer customer, CustomField customField);

}
