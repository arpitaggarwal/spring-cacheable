package com.arpit.spring.cache.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.arpit.spring.cache.service.IEmployeeService;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired
	private IEmployeeService employeeService;

	@RequestMapping(value = "/get")
	public String get() {
		employeeService.getEmployee(1);
		return "index";
	}
}
