package com.demo.store.mgmt.tool.advice;

import java.util.Date;

public record ErrorResponse(int statusCode, Date timestamp, String message, String description) {}
