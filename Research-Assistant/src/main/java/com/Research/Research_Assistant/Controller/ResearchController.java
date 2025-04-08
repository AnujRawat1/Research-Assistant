package com.Research.Research_Assistant.Controller;

import com.Research.Research_Assistant.Entity.ResearchEntity;
import com.Research.Research_Assistant.Service.ResearchService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/research")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class ResearchController {

    @Autowired
    private ResearchService researchService;

    @PostMapping("/process")
    public ResponseEntity<String> processContent(@RequestBody ResearchEntity researchEntity) {
        String summary = researchService.processContent(researchEntity);
        return ResponseEntity.ok(summary);
    }

}
