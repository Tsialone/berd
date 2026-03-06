package com.berd.dev.forms;

import lombok.Data;

@Data
public class DepenseDetailForm {
    private Integer idCategorieDetail;
    private Integer idUnite;
    private Double qte;
    private Double pu;
    private String designation;
}
