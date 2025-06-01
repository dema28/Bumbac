package com.bumbac.order.controller;

import com.bumbac.order.dto.ReturnDTO;
import com.bumbac.order.entity.ReturnStatus;
import com.bumbac.order.entity.ReturnStatusHistory;
import com.bumbac.order.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/returns")
@RequiredArgsConstructor
public class AdminReturnController {

    private final ReturnService returnService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ReturnDTO> getAllReturns(@RequestParam(required = false) ReturnStatus status) {
        return returnService.getReturnsByStatus(status);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ReturnDTO updateReturnStatus(@PathVariable Long id, @RequestBody ReturnStatus status) {
        return returnService.updateReturnStatus(id, status);
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ReturnStatusHistory> getReturnHistory(@PathVariable Long id) {
        return returnService.getHistoryByReturnId(id);
    }

}
