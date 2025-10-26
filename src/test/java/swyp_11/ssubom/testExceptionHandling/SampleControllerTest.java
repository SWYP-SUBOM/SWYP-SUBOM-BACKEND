package swyp_11.ssubom.testExceptionHandling;// src/test/java/org/scoula/ssubom/sample/SampleControllerTest.java


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import swyp_11.ssubom.global.error.GlobalExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;  // post(), get(), etc.
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;  // status(), jsonPath()



@WithMockUser(username = "test", roles = {"USER"})
@WebMvcTest(controllers = SampleController.class)
@Import({ GlobalExceptionHandler.class })
public class SampleControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper om;

    @Test
    void success_shouldReturn200_andSuccessBody() throws Exception {
        var body = om.writeValueAsString(new UserCreateRequest("hsj", "hsj@example.com"));

        mockMvc.perform(post("/samples")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("ok:hsj"))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void validationFail_shouldReturn400_andErrorBranch() throws Exception {
        var body = om.writeValueAsString(new UserCreateRequest("", "not-an-email"));

        mockMvc.perform(post("/samples")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.errorCode").value("C001"))
                .andExpect(jsonPath("$.error.errorMessage").exists());
    }

    @Test
    void businessException_shouldReturnMappedStatus_andError() throws Exception {
        mockMvc.perform(get("/samples/boom"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.errorCode").value("U001"))
                .andExpect(jsonPath("$.error.errorMessage").value("User not found"));
    }

    @Test
    void unhandled_shouldReturn500_andGenericError() throws Exception {
        mockMvc.perform(get("/samples/oops"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.errorCode").value("C002"));
    }
}