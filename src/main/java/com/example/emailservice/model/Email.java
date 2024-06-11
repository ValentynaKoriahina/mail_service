
package com.example.emailservice.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A data model for storing email information.
 */
@Getter
@Setter
@Document(indexName = "emails")
public class Email implements Serializable {
    @Id
    private String id;
    private String recipient;
    private String subject;
    private String content;
    private String status;
    private String errorMessage;
    private int attemptNumber;

    @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastAttemptTime;

    @Override
    public String toString() {
        return "Email{" +
                "recipient='" + recipient + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                ", lastAttemptTime='" + lastAttemptTime + '\'' +
                '}';
    }
}
