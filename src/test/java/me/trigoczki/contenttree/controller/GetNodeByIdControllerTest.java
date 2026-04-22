package me.trigoczki.contenttree.controller;

import me.trigoczki.contenttree.domain.dto.TreeNodeResponse;
import me.trigoczki.contenttree.exception.BadRequestException;
import me.trigoczki.contenttree.exception.NotFoundException;
import me.trigoczki.contenttree.service.TreeNodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static me.trigoczki.contenttree.Constants.NODES_URI;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TreeNodeController.class)
class GetNodeByIdControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TreeNodeService treeNodeService;

    @Test
    void getNodeByIdWithExistingIdReturnsOk() throws Exception {
        TreeNodeResponse response = new TreeNodeResponse(1L, "Root Node", "Root content", true, List.of());

        when(treeNodeService.getNode(1L)).thenReturn(response);

        mockMvc.perform(get(NODES_URI + "/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Root Node"))
                .andExpect(jsonPath("$.content").value("Root content"))
                .andExpect(jsonPath("$.hasChildren").value(true));
    }

    @Test
    void getNodeByIdWithInvalidIdReturnsBadRequest() throws Exception {
        when(treeNodeService.getNode(-1L))
                .thenThrow(new BadRequestException("Parent ID must be a positive number"));

        mockMvc.perform(get(NODES_URI + "/{id}", -1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Parent ID must be a positive number"));
    }

    @Test
    void getNodeByIdWithNonExistentIdReturnsNotFound() throws Exception {
        when(treeNodeService.getNode(11L))
                .thenThrow(new NotFoundException("Parent node not found: 11"));

        mockMvc.perform(get(NODES_URI + "/{id}", 11L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Parent node not found: 11"));
    }
}
