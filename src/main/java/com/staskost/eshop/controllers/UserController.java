package com.staskost.eshop.controllers;

import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.staskost.eshop.model.User;
import com.staskost.eshop.security.Authorization;
import com.staskost.eshop.services.UserService;

@RestController
@RequestMapping("/secured/app")
public class UserController {

	private UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/save")
	public ResponseEntity<String> registerUser(@RequestBody User user) {
		User user2 = userService.findByEmail(user.getEmail());
		if (user2 == null) {
			String password = user.retrievePassword();
			String secret = UUID.randomUUID().toString();
			user.setSecret(secret);
			String sha256hex = DigestUtils.sha256Hex(password + secret);
			user.setPassword(sha256hex);
			userService.createUser(user);
			return ResponseEntity.status(HttpStatus.OK)
					.body("User " + user.getFirstName() + " " + user.getLastName() + "was registered successfully");
		} else {
			return new ResponseEntity<>("Email Already exists", HttpStatus.BAD_REQUEST);
		}

	}

	@GetMapping("/by-token")
	public ResponseEntity<User> getUserFromToken(@RequestHeader(value = "staskost") String token) {
		User user = Authorization.getUserFromToken(token);
		return ResponseEntity.status(HttpStatus.OK).body(user);

	}

	@GetMapping
	public String sayHello() {
		return "Hello";
	}

}
