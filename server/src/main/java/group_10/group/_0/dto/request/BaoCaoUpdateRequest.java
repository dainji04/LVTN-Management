package group_10.group._0.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaoCaoUpdateRequest {

    @NotBlank(message = "TRANG_THAI_BLANK")
    String trangThai;
}
