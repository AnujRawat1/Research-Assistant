package com.Research.Research_Assistant.Service;

import com.Research.Research_Assistant.ApiResponse.GeminiResponse;
import com.Research.Research_Assistant.Entity.ResearchEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class ResearchService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

//    @Autowired
    private final WebClient webClient;

//    @Autowired
    private final ObjectMapper objectMapper;

    public ResearchService (WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public String processContent(ResearchEntity researchEntity) {

        // Build a prompt
        String prompt = buildPrompt(researchEntity);

        // Query the AI Model API
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[] {
                        Map.of("parts", new Object[] {
                                Map.of("text", prompt)
                        })
                }
        );

        String response = webClient.post()
                .uri(apiUrl+apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();


        // Parse the response & Return the response / summary
        return extractResponse(response);
    }

    private String extractResponse(String response) {
        try{
            GeminiResponse geminiResponse = objectMapper.readValue(response, GeminiResponse.class);

            if (geminiResponse.getCandidates() != null && !geminiResponse.getCandidates().isEmpty()) {
                GeminiResponse.Candidate firstCandidate = geminiResponse.getCandidates().get(0);
                if (firstCandidate.getContent() != null &&
                        firstCandidate.getContent().getParts() != null &&
                        !firstCandidate.getContent().getParts().isEmpty()) {
                    return firstCandidate.getContent().getParts().get(0).getText();
                }
            }
            return "No response received";
        }catch (Exception e){
            return  "Error while processing the response : "+e.getMessage();
        }
    }

    private String buildPrompt(ResearchEntity researchEntity) {
        // Build the prompt based on the content and operation

        StringBuilder prompt = new StringBuilder();

        switch (researchEntity.getOperation()) {
            case "summarise":
                prompt.append("Provide a clear and concise summary of the following content : \n\n");
                break;
            case "paraphrase":
                prompt.append("Rewrite the following content in different words while keeping the original meaning:\n\n");
                break;
            case "explain":
                prompt.append("Explain the following content in easy-to-understand, beginner-friendly language:\n\n");
                break;
            case "translate":
                prompt.append("Translate the following text into English clearly and accurately:\n\n");
                break;
            case "expand":
                prompt.append("Add more detail and context to the following content, making it more informative:\n\n");
                break;
            case "simplify":
                prompt.append("Break down the following content and simplify it as much as possible for a general audience:\n\n");
                break;
            case "suggest":
                prompt.append("Based on the following content, suggest related topics and further reading. Format the response with clear headings and bullet points: \n\n");
                break;
            case "code":
                prompt.append("Write a code snippet that performs the following task:\n\n");
                break;
            case "custom-prompt":
                prompt.append("\n\n");
                break;
            case "default":
                throw new IllegalArgumentException("Invalid operation: " + researchEntity.getOperation());
        }

        prompt.append(researchEntity.getContent());

        return prompt.toString();
    }

}
