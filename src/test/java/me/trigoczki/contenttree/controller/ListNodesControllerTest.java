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
class ListNodesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TreeNodeService treeNodeService;

    @Test
    void listRootNodesWithoutParentIdReturnsOk() throws Exception {
        TreeNodeResponse response = new TreeNodeResponse(1L, "Root Node", "Root content", true, List.of());

        when(treeNodeService.listNodes(null))
                .thenReturn(List.of(response));

        mockMvc.perform(get(NODES_URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Root Node"))
                .andExpect(jsonPath("$[0].content").value("Root content"))
                .andExpect(jsonPath("$[0].hasChildren").value(true));
    }

    @Test
    void listNodesWithParentIdReturnsOk() throws Exception {
        TreeNodeResponse response = new TreeNodeResponse(2L, "Child Node", "Child content", false, List.of());

        when(treeNodeService.listNodes(1L))
                .thenReturn(List.of(response));

        mockMvc.perform(get(NODES_URI).param("parentId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].name").value("Child Node"))
                .andExpect(jsonPath("$[0].content").value("Child content"))
                .andExpect(jsonPath("$[0].hasChildren").value(false));
    }

    @Test
    void listNodesWithNegativeParentIdReturnsBadRequest() throws Exception {
        when(treeNodeService.listNodes(-1L))
                .thenThrow(new BadRequestException("Parent ID must be a positive number"));

        mockMvc.perform(get(NODES_URI).param("parentId", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Parent ID must be a positive number"));
    }

    @Test
    void listNodesWithNonExistentParentReturnsNotFound() throws Exception {
        when(treeNodeService.listNodes(11L))
                .thenThrow(new NotFoundException("Parent node not found: 11"));

        mockMvc.perform(get(NODES_URI).param("parentId", "11"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Parent node not found: 11"));
    }
}
