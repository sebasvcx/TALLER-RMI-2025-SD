package api.dto;

import java.io.Serializable;

public record QueryResult(boolean exists, int availableCount, String message) implements Serializable {}