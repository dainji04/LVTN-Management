package group_10.group._0.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BinhLuanRequest {
    private Integer maNguoiDung;
    private Integer maBaiDang;      // ID bài viết
    private Integer maNhom;
    private String noiDung;
    private Integer maBinhLuanCha; // ID bình luận cha (null nếu không reply)
}
