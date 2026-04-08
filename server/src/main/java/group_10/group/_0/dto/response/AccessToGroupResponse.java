package group_10.group._0.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessToGroupResponse {
    Integer id;

    Integer maNhom;

    String trangThai;

    Instant yeuCauLuc;

    Integer xuLyBoi;

    Instant xuLyLuc;
}
