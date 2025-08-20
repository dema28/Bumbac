package com.bumbac.modules.user;

import com.bumbac.core.exception.GlobalExceptionHandler;
import com.bumbac.modules.user.controller.UserController;
import com.bumbac.modules.user.dto.ChangePasswordRequest;
import com.bumbac.modules.user.dto.UpdateUserDto;
import com.bumbac.modules.user.service.UserService;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false) // отключаем JWT/безопасность в этом срезе
@Import(GlobalExceptionHandler.class)     // подключаем наш обработчик ошибок
public class UserControllerValidationIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService; // мокаем сервис — проверяем только валидацию и формирование ответа

    @Test
    public void changePassword_shortNewPassword_returns400() throws Exception {
        String body = """
            {"oldPassword":"whatever","newPassword":"Poke"}
        """;

        mockMvc.perform(put("/api/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.messages[0].field").value("newPassword"));
    }

    @Test
    public void changePassword_wrongOldPassword_returns400() throws Exception {
        // эмулируем бизнес-исключение из сервиса
        Mockito.doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Old password is incorrect"))
                .when(userService).changePassword(anyString(), anyString(), anyString());

        String body = """
            {"oldPassword":"bad-old","newPassword":"StrongPass1"}
        """;

        mockMvc.perform(put("/api/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.messages[0].message").value("Old password is incorrect"));
    }

    @Test
    public void changePassword_ok_returns200() throws Exception {
        String body = """
            {"oldPassword":"OldStrong1","newPassword":"NewStrong1"}
        """;

        mockMvc.perform(put("/api/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().string("Password updated"));
    }
}
