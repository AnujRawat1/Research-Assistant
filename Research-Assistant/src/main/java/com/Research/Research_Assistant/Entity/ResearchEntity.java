package com.Research.Research_Assistant.Entity;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ResearchEntity {

    private String content;
    private String operation;

}
