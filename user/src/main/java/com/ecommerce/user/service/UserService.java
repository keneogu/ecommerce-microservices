package com.ecommerce.user.service;

import com.ecommerce.user.dto.AddressDTO;
import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.model.Address;
import com.ecommerce.user.model.User;
import com.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final KeyCloakAdminService keyCloakAdminService;

//  private List<User> userList = new ArrayList<>();
//  private Long nextId = 1L;

  public List<UserResponse> fetchAllUsers(){
    return userRepository.findAll().stream()
        .map(this::mapToUserResponse)
        .collect(Collectors.toList());
  }

  public void addUser(UserRequest userRequest){
//    user.setId(nextId++);
//    userList.add(user);
    String token = keyCloakAdminService.getAdminAccessToken();
    String keycloakUserId = keyCloakAdminService.createUser(token, userRequest);

    User user = new User();
    updateUserFromRequest(user, userRequest);
    user.setKeycloakId(keycloakUserId);

    keyCloakAdminService.assignRealmRoleToUser(userRequest.getUsername(), "USER", keycloakUserId);
    userRepository.save(user);
  }

  public Optional<UserResponse> fetchUser(String id) {
//    return userList.stream().filter(user -> user.getId().equals(id)).findFirst();
    return userRepository.findById(id).map(this::mapToUserResponse);
  }

  public boolean updateUser(UserRequest updatedUserRequest, String id) {
//    return userList.stream().filter(user -> user.getId().equals(id)).findFirst().map(existingUser -> {
//      existingUser.setFirstName(updatedUser.getFirstName());
//      existingUser.setLastName(updatedUser.getLastName());
//      return true;
//    }).orElse(false);
    Optional<User> userId = userRepository.findById(id);
    if(!userId.isPresent()){
        return false;
    }

    User user = userRepository.findById(id).map(existingUser -> {
      updateUserFromRequest(existingUser, updatedUserRequest);
      return existingUser;
    }).orElseThrow();
    userRepository.save(user);
    return true;
  }

  private void updateUserFromRequest(User user, UserRequest userRequest) {
    user.setFirstName(userRequest.getFirstName());
    user.setLastName(userRequest.getLastName());
    user.setEmail(userRequest.getEmail());
    user.setPhone(userRequest.getPhone());
    if(userRequest.getAddress() != null) {
      Address address = new Address();
      address.setStreet(userRequest.getAddress().getStreet());
      address.setState(userRequest.getAddress().getState());
      address.setCity(userRequest.getAddress().getCity());
      address.setCountry(userRequest.getAddress().getCountry());
      address.setZipcode(userRequest.getAddress().getZipcode());
      user.setAddress(address);
    }
  }

  private UserResponse mapToUserResponse(User user) {
    UserResponse response = new UserResponse();
    response.setId(String.valueOf(user.getId()));
    response.setFirstName(user.getFirstName());
    response.setLastName(user.getLastName());
    response.setEmail(user.getEmail());
    response.setPhone(user.getPhone());
    response.setUserRole(user.getUserRole());
    if (user.getAddress() != null) {
      AddressDTO addressDTO = new AddressDTO();
      addressDTO.setStreet(user.getAddress().getStreet());
      addressDTO.setCity(user.getAddress().getCity());
      addressDTO.setState(user.getAddress().getState());
      addressDTO.setCountry(user.getAddress().getCountry());
      addressDTO.setZipcode(user.getAddress().getZipcode());
      response.setAddress(addressDTO);
    }
    return response;
  }
}
