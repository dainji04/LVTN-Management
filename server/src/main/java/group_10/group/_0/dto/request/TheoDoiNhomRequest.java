package group_10.group._0.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TheoDoiNhomRequest {

    private Integer maNguoiTheoDoi;

    private Integer maNhom;
}
