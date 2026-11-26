// package org.policysim.spring;


// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.password.PasswordEncoder;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;

// import org.cokeke.policysim.repos.*;
// import org.cokeke.policysim.services.*;
// import org.cokeke.policysim.components.ServerStatus;
// import org.cokeke.policysim.exceptions.UserAlreadyExistsException;
// import org.cokeke.policysim.models.*;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// @ExtendWith(MockitoExtension.class)
// public class UnitApplicationTests {
//     @Mock 
//     private AppSessionRepository appSessionRepo;

//     @Mock
//     private TokenService tokenSvc;

//     @Mock
//     private UserRepository repo;

//     @Mock
//     private AppSessionService sessionSvc;

//     @Mock
//     private EmailRepository emailRepo;

//     @Mock
//     private ServerStatus serverStatus;

//     @InjectMocks
//     private UserService userService;

//     @Test
//     public void test_user_has_token() {
//         Token token = tokenSvc.findByUserId(1L);
//         assertEquals(token.getUserId(), 1, "User with ID 1 doesnt have a token");
//     }

//     @Test
//     public void get_user_by_email_equals_get_user_by_id() {
//         User emailUser = userService.getUserByEmail("me@charlesokeke.com");
//         User idUser = userService.getUserById(1L);

//         assertEquals(emailUser.getUserName(), idUser.getUserName(), "Retrieving user by email and id returned two different records");
//     }

//     @Test
//     public void create_user_with_taken_email() {
//         UserAlreadyExistsException exception = assertThrows(
//             UserAlreadyExistsException.class,
//             () -> userService.createUser("Super_User", "me@charlesokeke.com", "wtv")
//         );

//         assertEquals("User with this email already exists", exception.getMessage());
//     }

//     @Test
//     public void create_user_with_taken_username() {
//         UserAlreadyExistsException exception = assertThrows(
//             UserAlreadyExistsException.class,
//             () -> userService.createUser("Super_User", "me@charlesokeke.com", "wtv")
//         );

//         assertEquals("User with this username already exists", exception.getMessage());
//     }

//     @Test
//     public void user_can_login_with_username() {
//         UserAlreadyExistsException exception = assertThrows(
//             UserAlreadyExistsException.class,
//             () -> userService.loginUser("Super_User", "fake_Password", "BLAH", "BLAH")
//         );

//         assertEquals("User to retrieve user with given username", exception.getMessage());
//     }
// } 