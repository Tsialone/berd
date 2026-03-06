package com.berd.dev.forms;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class DepenseForm {
    private Integer idCategorieDepense;
    private LocalDateTime created;
    private String description;
    private Boolean estPrevue = false;
    private List<DepenseDetailForm> details;
}
