package com.keyclaok.users;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

 

@Component
public class JwtUtil {
	
	private static String TOKEN_ENDPOINT = "http://localhost:9091/realms/master/protocol/openid-connect/token";
	private static String USERNAME_ENDPOINT = "http://localhost:9091/admin/realms/Canteen_Management/users?username=";
	private static String EMPLOYEEID_ENDPOINT = "http://localhost:9091/admin/realms/Canteen_Management/users?q=employeeID:";
    private static String ALLEMPLOYEE_ENDPOINT= "http://localhost:9091/admin/realms/Canteen_Management/users?min=0&max=999999&enabled=true";
    
    @Autowired
    private EmployeeRepo employeeRepo;
    
	public List<UserDTO> getAllUsers(String parameter) {
		System.out.println(parameter);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("grant_type", "password");
		requestBody.add("client_id", "admin-cli");
		requestBody.add("username", "admin");
		requestBody.add("password", "Wissen@01Key");
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);
		ResponseEntity<JsonNode> response = restTemplate.postForEntity(TOKEN_ENDPOINT, request, JsonNode.class);
		ResponseEntity<JsonNode> users = null;
		JSONArray usersArray = null;
		List<UserDTO> usersList = new ArrayList<>();
		try {
			if (response.getStatusCode() == HttpStatus.OK) {
				JsonNode responseBody = response.getBody();
				if (responseBody != null && responseBody.has("access_token")) {
					String accessToken = responseBody.get("access_token").toString();
					String token = accessToken.substring(1, accessToken.length() - 1);
					HttpHeaders headers1 = new HttpHeaders();
					headers1.add("Content-Type", "application/json");
					headers1.add("Authorization", "Bearer " + token);
					HttpEntity<String> httpEntity = new HttpEntity<>(headers1);
					if (isNumeric(parameter)) {
						String idEndPoint = EMPLOYEEID_ENDPOINT + parameter;
						users = restTemplate.exchange(idEndPoint, HttpMethod.GET, httpEntity, JsonNode.class);
					} else if (parameter != null && !parameter.isEmpty()) {
						String nameEndpoint = USERNAME_ENDPOINT + parameter;
						users = restTemplate.exchange(nameEndpoint, HttpMethod.GET, httpEntity, JsonNode.class);
					} else {
						String allEmployeeEndpoint = ALLEMPLOYEE_ENDPOINT;
						users = restTemplate.exchange(allEmployeeEndpoint, HttpMethod.GET, httpEntity ,JsonNode.class);
					}
					usersArray = new JSONArray(users.getBody().toString());
					String department = null;
						for (Object obj : usersArray) {
							JSONObject obj1 = (JSONObject) obj;
							JSONObject attributes = obj1.getJSONObject("attributes");
							String empID = (String) attributes.getJSONArray("employeeID").get(0);
							 
						        JSONArray departmentArray = attributes.optJSONArray("department");
						        if (departmentArray != null && departmentArray.length() > 0) {
						            department = departmentArray.toString();
						            
						            Pattern pattern = Pattern.compile("[a-zA-Z0-9]+");
						            Matcher matcher = pattern.matcher(department);
						            
						            StringBuilder result = new StringBuilder();
						            while (matcher.find()) {
						                if (result.length() > 0) {
						                    result.append(" ");
						                }
						                result.append(matcher.group());
						            }
						            department = result.toString();
						        }
							
							
							
							UserDTO dto = new UserDTO();
							dto.setEmpEmail(obj1.optString("email"));
							dto.setEmpName(obj1.optString("username"));
							dto.setEmpID(empID);
							dto.setDepartment(department);
							usersList.add(dto);
							saveToDataBase(dto);
							
						}
				}
			}
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
		return usersList;
	}
	
	public void saveToDataBase(UserDTO userDTO) {
		if (!employeeRepo.existsByEmpID(userDTO.getEmpID())) {
	        Employee employee = new Employee();
	        employee.setEmpEmail(userDTO.getEmpEmail());
	        employee.setEmpName(userDTO.getEmpName());
	        employee.setDepartment(userDTO.getDepartment());
	        employee.setEmpID(userDTO.getEmpID());

	        employeeRepo.save(employee);
	    } else {
	        System.out.println("Employee with empID " + userDTO.getEmpID() + " already exists.");
	    }
		
	}
	
	

	public static boolean isNumeric(String str) {
		// Return true if the string matches the numeric pattern, otherwise return false
		return str != null && str.matches("-?\\d+(\\.\\d+)?");
	}

}