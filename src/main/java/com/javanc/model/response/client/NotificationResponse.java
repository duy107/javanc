package com.javanc.model.response.client;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    Long id;
    String timeAgo;
    String content;
    Boolean status;
}
