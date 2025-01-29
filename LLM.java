package com.broyojo.spotifywrappedclone.backend.stats;

import android.util.Log;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.util.Arrays;
import java.util.List;

public class LLM {
    //private final static String API_KEY = "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";
//    private OpenAiService service;
    private final String systemMessage;

//    public LLM(String systemMessage) {
//        this.service = new OpenAiService(API_KEY);
//        this.systemMessage = systemMessage;
//    }

    public interface LLMResponseCallback {
        void receive(String response);
    }

    public void getResponse(String message, LLMResponseCallback callback) {
        List<ChatMessage> messages = Arrays.asList(
                new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage),
                new ChatMessage(ChatMessageRole.USER.value(), message)
        );

        System.out.println(message);

        ChatCompletionRequest request = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo-0613")
                .messages(messages)
                .maxTokens(256)
                .build();

        new Thread(() -> {
            try {
                ChatCompletionResult result = service.createChatCompletion(request);
                if (!result.getChoices().isEmpty()) {
                    String response = result.getChoices().get(0).getMessage().getContent();
                    callback.receive(response);
                } else {
                    Log.e("LLM", "Chat completion was empty!");
                }
            } catch (Exception e) {
                Log.e("LLM", "Ran into error during LLM chat completion: " + e.getMessage());
            }
        }).start();
    }
}
