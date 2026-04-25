package me.trigoczki.contenttree.controller.tree;

import me.trigoczki.contenttree.controller.TreeController;
import me.trigoczki.contenttree.domain.dto.TreeNodeResponse;
import me.trigoczki.contenttree.service.TreeNodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static me.trigoczki.contenttree.Constants.TREE_SEARCH_URI;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TreeController.class)
public class SearchTreeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TreeNodeService treeNodeService;

    @Test
    void searchWithMatchingQueryReturnsMatchingNodes() throws Exception {
        TreeNodeResponse matchingNode = new TreeNodeResponse(1L, "Java Basics", "Introduction to Java", false, List.of());
        matchingNode.setMatch(true);

        when(treeNodeService.searchTree("java"))
                .thenReturn(List.of(matchingNode));

        mockMvc.perform(get(TREE_SEARCH_URI).param("searchTerm", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Java Basics"))
                .andExpect(jsonPath("$[0].content").value("Introduction to Java"))
                .andExpect(jsonPath("$[0].match").value(true));
    }

    @Test
    void searchWithBlankQueryReturnsFullTree() throws Exception {
        TreeNodeResponse root1 = new TreeNodeResponse(1L, "Root One", "Content One", false, List.of());
        TreeNodeResponse root2 = new TreeNodeResponse(2L, "Root Two", "Content Two", true, List.of());

        when(treeNodeService.searchTree(""))
                .thenReturn(List.of(root1, root2));

        mockMvc.perform(get(TREE_SEARCH_URI).param("searchTerm", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void searchWithNoMatchReturnsNodesWithMatchFalse() throws Exception {
        TreeNodeResponse nonMatchingNode = new TreeNodeResponse(1L, "Spring Framework", "Spring Boot guide", false, List.of());
        nonMatchingNode.setMatch(false);

        when(treeNodeService.searchTree("python"))
                .thenReturn(List.of(nonMatchingNode));

        mockMvc.perform(get(TREE_SEARCH_URI).param("searchTerm", "python"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].match").value(false));
    }

    @Test
    void searchReturnsEmptyListWhenNoNodesExist() throws Exception {
        when(treeNodeService.searchTree("anything"))
                .thenReturn(List.of());

        mockMvc.perform(get(TREE_SEARCH_URI).param("searchTerm", "anything"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void searchWithChildNodesReturnsNestedStructure() throws Exception {
        TreeNodeResponse child = new TreeNodeResponse(2L, "Java Advanced", "Advanced Java topics", false, List.of());
        child.setParentId(1L);
        child.setMatch(true);

        TreeNodeResponse parent = new TreeNodeResponse(1L, "Programming", "Programming resources", true, List.of(child));
        parent.setMatch(false);

        when(treeNodeService.searchTree("advanced"))
                .thenReturn(List.of(parent));

        mockMvc.perform(get(TREE_SEARCH_URI).param("searchTerm", "advanced"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].match").value(false))
                .andExpect(jsonPath("$[0].hasChildren").value(true))
                .andExpect(jsonPath("$[0].children", hasSize(1)))
                .andExpect(jsonPath("$[0].children[0].id").value(2L))
                .andExpect(jsonPath("$[0].children[0].match").value(true))
                .andExpect(jsonPath("$[0].children[0].parentId").value(1L));
    }
}
