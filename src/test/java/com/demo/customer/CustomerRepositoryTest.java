package com.demo.customer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.demo.customer.repository.CustomField;
import com.demo.customer.repository.CustomFieldRepository;
import com.demo.customer.repository.CustomFieldValues;
import com.demo.customer.repository.Customer;
import com.demo.customer.repository.CustomFiedValuesRepository;

import java.util.List;

import static com.demo.customer.repository.FieldType.DROPDOWN;
import static com.demo.customer.repository.FieldType.TEXTBOX;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CustomerRepositoryTest {

	@Autowired
	private TestEntityManager em;

	@Autowired
	private CustomFiedValuesRepository customFieldValueRepository;
	@Autowired
	private CustomFieldRepository customFieldRepository;

	@Test
	public void shouldFetchCustomFields() {
		//create some custom fields
		em.persist(new CustomField("field-1", TEXTBOX, new String[] {}));
		em.persist(new CustomField("field-2", DROPDOWN, new String[] { "value-1", "value-2" }));

		List<CustomField> fields = customFieldRepository.findActiveFields();

		assertEquals(2, fields.size());
	}

	@Test
	public void shouldSaveAndRetrieveCurrentCustomFieldValues() {
		//create some custom fields
		CustomField field1 = em.persist(new CustomField("field-1", TEXTBOX, new String[] {}));
		CustomField field2 = em.persist(new CustomField("field-2", DROPDOWN, new String[] { "value-1", "value-2" }));
		//create a customer
		Customer customer = em.persist(new Customer("some-customer"));

		//save some values for custom fields
		CustomFieldValues value1 = new CustomFieldValues();
		value1.setCustomField(field1);
		value1.setCustomer(customer);
		value1.setValues(new String[] { "some-value" });
		em.persist(value1);

		CustomFieldValues updatedValue1 = new CustomFieldValues();
		updatedValue1.setCustomField(field1);
		updatedValue1.setCustomer(customer);
		updatedValue1.setValues(new String[] { "updated-value" });
		em.persist(updatedValue1);

		CustomFieldValues value2 = new CustomFieldValues();
		value2.setCustomField(field2);
		value2.setCustomer(customer);
		value2.setValues(new String[] { "first-value", "second-value" });
		em.persist(value2);

		List<CustomFieldValues> values = customFieldValueRepository.fetchCustomerCustomFieldsValues(customer);
		assertEquals(2, values.size());

		assertEquals("updated-value", getTextboxValue(values));
		assertArrayEquals(new String[] { "first-value", "second-value" }, getDropDownValues(values));
	}

	@Test
	public void shouldUpdateCustomFieldValuesAndRetrieveHistoricalValues() {
		//create a custom field
		CustomField field1 = em.persist(new CustomField("field-1", TEXTBOX, new String[] {}));
		//create a customer
		Customer customer = em.persist(new Customer("some-customer"));

		//save a value for the field
		CustomFieldValues value1 = new CustomFieldValues();
		value1.setCustomField(field1);
		value1.setCustomer(customer);
		value1.setValues(new String[] { "some-value" });
		em.persist(value1);

		//update the value of the field
		CustomFieldValues value2 = new CustomFieldValues();
		value2.setCustomField(field1);
		value2.setCustomer(customer);
		value2.setValues(new String[] { "another-value" });
		em.persist(value2);

		//update the value of the field once more
		CustomFieldValues value3 = new CustomFieldValues();
		value3.setCustomField(field1);
		value3.setCustomer(customer);
		value3.setValues(new String[] { "latest-value" });
		em.persist(value3);

		List<CustomFieldValues> historicalValues = customFieldValueRepository
				.fetchCustomerCustomFieldHistoricValues(customer, field1);
		assertEquals(2, historicalValues.size());

		//assert only previous records are fetched with descending order
		assertEquals("another-value", historicalValues.get(0).getValues()[0]);
		assertEquals("some-value", historicalValues.get(1).getValues()[0]);
	}

	private String[] getDropDownValues(List<CustomFieldValues> values) {
		return values.stream().filter(v -> v.getCustomField().getType().equals(DROPDOWN)).findAny().get().getValues();
	}

	private String getTextboxValue(List<CustomFieldValues> values) {
		return values.stream().filter(v -> v.getCustomField().getType().equals(TEXTBOX)).findAny().get()
				.getValues()[0];
	}

}
