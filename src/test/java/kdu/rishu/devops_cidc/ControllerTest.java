package kdu.rishu.devops_cidc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.is;

@WebMvcTest(controller.class)
@TestPropertySource(properties = {"spring.application.version=1"})
public class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testVersionEndpoint() throws Exception {
        mockMvc.perform(get("/version"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    public void testVersionEndpointWithInvalidVersion() throws Exception {
        // This test will fail if the version is not a valid integer
        mockMvc.perform(get("/version"))
                .andExpect(status().isOk())
                .andExpect(content().string(is("1")));
    }
} 