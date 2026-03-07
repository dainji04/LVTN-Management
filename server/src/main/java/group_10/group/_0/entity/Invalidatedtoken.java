package group_10.group._0.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "invalidatedtoken")
public class Invalidatedtoken {
    @Id
    @Size(max = 255)
    @Column(name = "id", nullable = false)
    String id;

    @Column(name = "expirationtime")
    Date expirationtime;

}