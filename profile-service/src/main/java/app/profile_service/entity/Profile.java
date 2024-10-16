package app.profile_service.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;

@Document("profile")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    @MongoId
    private String profileId;

    private String userId;
    private String email;
    private String userName;
    private String firstName;
    private String lastName;
    private LocalDate dob;
}
