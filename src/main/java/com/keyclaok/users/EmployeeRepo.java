package com.keyclaok.users;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepo extends JpaRepository<Employee, Long>{
	 boolean existsByEmpID(String empID);
	

}
