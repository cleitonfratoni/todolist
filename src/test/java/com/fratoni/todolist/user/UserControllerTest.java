package com.fratoni.todolist.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IUserRepository userRepository;

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        when(userRepository.findByUsername("john")).thenReturn(null);
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"john\",\"password\":\"123456\",\"name\":\"John\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnBadRequestWhenUsernameAlreadyExists() throws Exception {
        var existing = new UserModel();
        existing.setUsername("john");
        when(userRepository.findByUsername("john")).thenReturn(existing);

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"john\",\"password\":\"123456\",\"name\":\"John\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Usuário já existe."));
    }
}
