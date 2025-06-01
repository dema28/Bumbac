package com.bumbac.order.controller;

import com.bumbac.order.dto.ReturnDTO;
import com.bumbac.order.dto.ReturnRequestDTO;
import com.bumbac.order.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public ReturnDTO createReturn(@RequestBody ReturnRequestDTO dto) {
        return returnService.createReturnDTO(dto);
    }
}
