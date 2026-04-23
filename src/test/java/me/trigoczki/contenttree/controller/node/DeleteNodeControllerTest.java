package me.trigoczki.contenttree.controller.node;

import me.trigoczki.contenttree.controller.TreeNodeController;
import me.trigoczki.contenttree.exception.NotFoundException;
import me.trigoczki.contenttree.service.TreeNodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static me.trigoczki.contenttree.Constants.NODES_URI;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TreeNodeController.class)
class DeleteNodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TreeNodeService treeNodeService;

    @Test
    void deleteNodeWithExistingIdReturnsNoContent() throws Exception {
        mockMvc.perform(delete(NODES_URI + "/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(treeNodeService).deleteNode(1L);
    }

    @Test
    void deleteNodeWithNonExistentIdReturnsNotFound() throws Exception {
        doThrow(new NotFoundException("Node not found: 11"))
                .when(treeNodeService).deleteNode(11L);

        mockMvc.perform(delete(NODES_URI + "/{id}", 11L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Node not found: 11"));
    }
}
