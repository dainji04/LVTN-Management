package group_10.group._0.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaoCaoRequest {

    @NotBlank(message = "LOAI_DOI_TUONG_BLANK")
    String loaiDoiTuong; // BAI_VIET, NHOM, NGUOI_DUNG

    @NotNull(message = "ID_DOI_TUONG_NULL")
    Integer idDoiTuong;

    @NotBlank(message = "LY_DO_BLANK")
    String lyDo;
}
