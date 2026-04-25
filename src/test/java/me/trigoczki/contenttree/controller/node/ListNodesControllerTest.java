package me.trigoczki.contenttree.controller.node;

import me.trigoczki.contenttree.controller.TreeNodeController;
import me.trigoczki.contenttree.domain.dto.TreeNodeResponse;
import me.trigoczki.contenttree.service.TreeNodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static me.trigoczki.contenttree.Constants.NODES_URI;
import static org.hamcrest.Matchers.hasSize;
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

        when(treeNodeService.listTree())
                .thenReturn(List.of(response));

        mockMvc.perform(get(NODES_URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Root Node"))
                .andExpect(jsonPath("$[0].content").value("Root content"))
                .andExpect(jsonPath("$[0].hasChildren").value(true));
    }

    @Test
    void listNodesWhenEmptyReturnsOkWithEmptyArray() throws Exception {
        when(treeNodeService.listTree())
                .thenReturn(List.of());

        mockMvc.perform(get(NODES_URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void listMultipleRootNodesReturnsOk() throws Exception {
        TreeNodeResponse first = new TreeNodeResponse(1L, "First Root", "First content", false, List.of());
        TreeNodeResponse second = new TreeNodeResponse(2L, "Second Root", "Second content", true, List.of());

        when(treeNodeService.listTree())
                .thenReturn(List.of(first, second));

        mockMvc.perform(get(NODES_URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("First Root"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Second Root"))
                .andExpect(jsonPath("$[1].hasChildren").value(true));
    }
}
