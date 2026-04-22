package me.trigoczki.contenttree.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.trigoczki.contenttree.domain.dto.TreeNodeResponse;
import me.trigoczki.contenttree.domain.dto.UpdateNodeRequest;
import me.trigoczki.contenttree.exception.NotFoundException;
import me.trigoczki.contenttree.service.TreeNodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static me.trigoczki.contenttree.Constants.NODES_URI;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TreeNodeController.class)
class UpdateNodesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TreeNodeService treeNodeService;

    @Test
    void updateWithValidRequestReturnsOk() throws Exception {
        UpdateNodeRequest request = new UpdateNodeRequest();
        request.setId(2L);
        request.setName("Updated Node");
        request.setContent("Updated content");
        request.setParentId(1L);

        TreeNodeResponse response = new TreeNodeResponse(2L, "Updated Node", "Updated content", false, List.of());

        when(treeNodeService.updateNode(any(UpdateNodeRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put(NODES_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Updated Node"))
                .andExpect(jsonPath("$.content").value("Updated content"));
    }

    @Test
    void updateWithInvalidRequestReturnsBadRequest() throws Exception {
        UpdateNodeRequest request = new UpdateNodeRequest();

        mockMvc.perform(put(NODES_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateWithNegativeParentIdReturnsBadRequest() throws Exception {
        UpdateNodeRequest request = new UpdateNodeRequest();
        request.setId(2L);
        request.setName("Updated Node");
        request.setContent("Updated content");
        request.setParentId(-1L);

        mockMvc.perform(put(NODES_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateWithNonExistentParentReturnsNotFound() throws Exception {
        UpdateNodeRequest request = new UpdateNodeRequest();
        request.setId(2L);
        request.setName("Updated Node");
        request.setContent("Updated content");
        request.setParentId(11L);

        when(treeNodeService.updateNode(any(UpdateNodeRequest.class)))
                .thenThrow(new NotFoundException("Parent node not found: 11"));

        mockMvc.perform(put(NODES_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Parent node not found: 11"));
    }
}
