package com.keyclaok.users;
 
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
 
 
public class UserController {
	 private static final Logger logger = LoggerFactory.getLogger(UserController.class);
 
	@Autowired
	private UserService userService;
	
	@GetMapping("/all")
	public List<UserDTO> getAllUsers(@RequestParam(value = "parameter", required = false) String parameter) {
	    System.out.println(parameter);
	    logger.info("Received request to get all users with parameter: {}", parameter);
	    return userService.getAllUsers(parameter);
	}
	@GetMapping
	public String test() {
		return "hi";
	}
}