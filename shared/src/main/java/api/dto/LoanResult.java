package api.dto;

import java.io.Serializable;
import java.time.LocalDate;

public record LoanResult(boolean ok, LocalDate dueDate, String message) implements Serializable {}
