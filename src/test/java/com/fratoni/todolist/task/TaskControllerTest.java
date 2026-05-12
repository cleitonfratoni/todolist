package com.fratoni.todolist.task;

import com.fratoni.todolist.user.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ITaskRepository taskRepository;

    // Necessário para que o Spring consiga criar o bean FilterTaskAuth
    @MockitoBean
    private IUserRepository userRepository;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    // Helper: injeta o idUser diretamente no request, simulando o que o FilterTaskAuth faria
    private static org.springframework.test.web.servlet.request.RequestPostProcessor withUser(UUID id) {
        return request -> {
            request.setAttribute("idUser", id);
            return request;
        };
    }

    @Test
    void shouldCreateTaskSuccessfully() throws Exception {
        var task = new TaskModel();
        task.setIdUser(userId);
        when(taskRepository.save(any())).thenReturn(task);

        mockMvc.perform(post("/tasks/create")
                        .with(withUser(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Test Task",
                                  "startAt": [2026, 6, 1, 10, 0, 0],
                                  "endAt": [2026, 6, 2, 10, 0, 0],
                                  "priority": "HIGH"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestWhenStartAtIsInPast() throws Exception {
        mockMvc.perform(post("/tasks/create")
                        .with(withUser(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Test",
                                  "startAt": [2026, 1, 1, 10, 0, 0],
                                  "endAt": [2026, 6, 30, 10, 0, 0],
                                  "priority": "HIGH"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenStartAtIsAfterEndAt() throws Exception {
        mockMvc.perform(post("/tasks/create")
                        .with(withUser(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Test",
                                  "startAt": [2026, 6, 10, 10, 0, 0],
                                  "endAt": [2026, 6, 5, 10, 0, 0],
                                  "priority": "HIGH"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldListTasksForAuthenticatedUser() throws Exception {
        when(taskRepository.findByIdUser(userId)).thenReturn(List.of());

        mockMvc.perform(get("/tasks/")
                        .with(withUser(userId)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingNonExistentTask() throws Exception {
        var id = UUID.randomUUID();
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(post("/tasks/" + id)
                        .with(withUser(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Tarefa não encontrada."));
    }

    @Test
    void shouldReturnBadRequestWhenUserDoesNotOwnTask() throws Exception {
        var id = UUID.randomUUID();
        var task = new TaskModel();
        task.setIdUser(UUID.randomUUID()); // dono diferente
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        mockMvc.perform(post("/tasks/" + id)
                        .with(withUser(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Usuário não tem permissão para alterar essa tarefa."));
    }

    @Test
    void shouldUpdateTaskSuccessfully() throws Exception {
        var id = UUID.randomUUID();
        var task = new TaskModel();
        task.setIdUser(userId);
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenReturn(task);

        mockMvc.perform(post("/tasks/" + id)
                        .with(withUser(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated Title\"}"))
                .andExpect(status().isOk());
    }
}
