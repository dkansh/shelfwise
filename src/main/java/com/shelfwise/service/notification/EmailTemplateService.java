package com.shelfwise.service.notification;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * Loads the plain text email templates from resources/email-templates and fills in the
 * {{placeholders}}.
 */
@Service
public class EmailTemplateService {

    public String render(String templateName, Map<String, String> values) {
        String template = load(templateName);
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return template;
    }

    private String load(String templateName) {
        try {
            File file = new File(getClass().getClassLoader()
                    .getResource("email-templates/" + templateName + ".txt").getFile());
            return Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Could not load email template " + templateName, e);
        }
    }
}
