package api.dto;

import java.io.Serializable;

public record ReturnResult(boolean ok, String message) implements Serializable {}