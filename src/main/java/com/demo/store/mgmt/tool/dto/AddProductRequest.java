package com.demo.store.mgmt.tool.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

// This record is used when a client wants to add a new product
public record AddProductRequest(
        @NotBlank String name,
        @Min(0) BigDecimal price
) {}
