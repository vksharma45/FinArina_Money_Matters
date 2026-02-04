package com.portfolio.service;

import com.portfolio.dto.request.AssetGroupRequest;
import com.portfolio.entity.AssetGroup;
import com.portfolio.repository.AssetGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AssetGroupServiceTest {

    @Mock
    private AssetGroupRepository assetGroupRepository;

    @Mock
    private AssetService assetService;

    @InjectMocks
    private AssetGroupService assetGroupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createGroup_success() {
        AssetGroupRequest req = new AssetGroupRequest();
        req.setGroupName("My Group");
        req.setDescription("desc");

        AssetGroup saved = new AssetGroup();
        saved.setGroupId(1L);
        saved.setGroupName("My Group");
        saved.setDescription("desc");

        when(assetGroupRepository.existsByGroupName("My Group")).thenReturn(false);
        when(assetGroupRepository.save(any(AssetGroup.class))).thenReturn(saved);

        var resp = assetGroupService.createGroup(req);
        assertNotNull(resp);
        assertEquals(1L, resp.getGroupId());
        assertEquals("My Group", resp.getGroupName());
    }

    @Test
    void findGroupById_notFound() {
        when(assetGroupRepository.findById(99L)).thenReturn(Optional.empty());
        var ex = assertThrows(RuntimeException.class, () -> assetGroupService.findGroupById(99L));
        assertTrue(ex.getMessage().contains("Asset group not found"));
    }
}
