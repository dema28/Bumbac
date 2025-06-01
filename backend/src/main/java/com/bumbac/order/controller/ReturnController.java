package com.bumbac.order.controller;

import com.bumbac.order.dto.ReturnDTO;
import com.bumbac.order.dto.ReturnRequestDTO;
import com.bumbac.order.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.List;

@RestController
@RequestMapping("/api/returns")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnService returnService;

    @GetMapping
    public List<ReturnDTO> getAllReturns() {
        return returnService.getAllReturnDTOs();
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ReturnDTO createReturn(@RequestBody ReturnRequestDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        return returnService.createReturnDTO(dto, userEmail);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ReturnDTO getReturnById(@PathVariable Long id) {
        return returnService.getReturnById(id);
    }

}
