package expense_tally.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Transaction {
    private Double amount;
    private String category;
    private String subcategory;
    private String payment_method;
    private String description;
    private LocalDateTime expensedTime;
    private String status;
}
