package com.demo.store.mgmt.tool.dto;

import java.math.BigDecimal;

public record ProductResponse(Long id, String name, BigDecimal price) {}

