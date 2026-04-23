package me.trigoczki.contenttree.controller.tree;

import me.trigoczki.contenttree.controller.TreeController;
import me.trigoczki.contenttree.domain.dto.FilteredTreeNodeResponse;
import me.trigoczki.contenttree.domain.dto.TreeSearchResponse;
import me.trigoczki.contenttree.service.TreeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TreeController.class)
public class SearchTreeControllerTest {

    private static final String SEARCH_TREE_URI = "/api/tree/search";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TreeService treeService;

    @Test
    void searchWithValidSearchTermReturnsOk() throws Exception {
        FilteredTreeNodeResponse matchedNode = new FilteredTreeNodeResponse();
        matchedNode.setId(1L);
        matchedNode.setName("Node 1");
        matchedNode.setContent("Content containing term");
        matchedNode.setMatch(true);

        TreeSearchResponse response = new TreeSearchResponse(List.of(matchedNode));

        when(treeService.searchTree("term")).thenReturn(response);

        mockMvc.perform(get(SEARCH_TREE_URI)
                        .param("searchTerm", "term"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodes[0].id").value(1L))
                .andExpect(jsonPath("$.nodes[0].name").value("Node 1"))
                .andExpect(jsonPath("$.nodes[0].content").value("Content containing term"))
                .andExpect(jsonPath("$.nodes[0].match").value(true));

        verify(treeService).searchTree("term");
    }

    @Test
    void searchWithoutSearchTermReturnsBadRequest() throws Exception {
        mockMvc.perform(get(SEARCH_TREE_URI))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(treeService);
    }
}
