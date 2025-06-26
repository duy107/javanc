package com.javanc.model.response.admin;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.checkerframework.checker.units.qual.N;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatRoomResponse {
    Long chatRoomId;
    Long userId;
    String name;
    String avatar;
}
