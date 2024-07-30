
package com.keyclaok.users;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private JwtUtil jwtUtil;

	public List<UserDTO> getAllUsers(String parameter) {
		return jwtUtil.getAllUsers(parameter);
	}

}
