package com.freelancenexus.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//EmailNotificationDTO.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotificationDTO {
 private String to;
 private String subject;
 private String body;
 private String template;
}