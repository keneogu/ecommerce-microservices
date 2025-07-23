package com.ecommerce.user.controller;

import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

  @Autowired
  UserService userService;
//  private static Logger logger = LoggerFactory.getLogger(UserController.class);

  @GetMapping
  public ResponseEntity<List<UserResponse>> getAllUsers(){
    //these are the two ways of using ResponseEntity to return.
//    return ResponseEntity.ok(userService.fetchAllUsers());
    return new ResponseEntity<>(userService.fetchAllUsers(), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getUser(@PathVariable String id){
    log.info("Request received for user: {}", id);
    return userService.fetchUser(id)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<String> createUser(@RequestBody UserRequest userRequest){
    userService.addUser(userRequest);
    return ResponseEntity.ok("User added successfully");
  }

  @PutMapping("/{id}")
  public ResponseEntity<String> updateUser(@RequestBody UserRequest updatedUserRequest, @PathVariable String id) {
    boolean updated = userService.updateUser(updatedUserRequest, id);
    if(updated) return ResponseEntity.ok("User updated successfully");
    return ResponseEntity.notFound().build();
  }
}
