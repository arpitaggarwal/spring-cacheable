package com.arpit.spring.cache.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.arpit.spring.cache.domain.Employee;

@Component
public class EmployeeServiceImpl implements IEmployeeService {

	@Override
	@Cacheable(value = "EMPLOYEE_", key = "#id")
	public Employee getEmployee(int id) {
		return new Employee(id, "A");
	}

}
