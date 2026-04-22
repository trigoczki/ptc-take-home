package me.trigoczki.contenttree.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.trigoczki.contenttree.domain.dto.CreateNodeRequest;
import me.trigoczki.contenttree.domain.dto.TreeNodeResponse;
import me.trigoczki.contenttree.exception.NotFoundException;
import me.trigoczki.contenttree.service.TreeNodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TreeNodeController.class)
class TreeNodeControllerTest {

    public static final String URI_TEMPLATE = "/api/tree/nodes";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TreeNodeService treeNodeService;

    @Test
    void createWithValidRequestWithoutPArentIdReturnsOk() throws Exception {
        CreateNodeRequest request = new CreateNodeRequest();
        request.setName("New Node");
        request.setContent("Some content");

        TreeNodeResponse response = new TreeNodeResponse(1L, "New Node", "Some content", List.of());

        when(treeNodeService.insertNode(any(CreateNodeRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post(URI_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("New Node"))
                .andExpect(jsonPath("$.content").value("Some content"));
    }

    @Test
    void createWithInvalidRequestReturnsBadRequest() throws Exception {
        CreateNodeRequest request = new CreateNodeRequest();

        mockMvc.perform(post(URI_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithNegativeParentIdReturnsBadRequest() throws Exception {
        CreateNodeRequest request = new CreateNodeRequest();
        request.setName("Node");
        request.setContent("Content");
        request.setParentId(-1L);

        mockMvc.perform(post(URI_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithNonExistentParentReturnsNotFound() throws Exception {
        CreateNodeRequest request = new CreateNodeRequest();
        request.setName("Child Node");
        request.setContent("Child content");
        request.setParentId(1L);

        when(treeNodeService.insertNode(any(CreateNodeRequest.class)))
                .thenThrow(new NotFoundException("Parent node not found: 1"));

        mockMvc.perform(post(URI_TEMPLATE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Parent node not found: 1"));
    }
}