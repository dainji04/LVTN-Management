package group_10.group._0.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupRequest {

     String tenNhom;

     String moTa;

     String anhBia;

     String anhDaiDien;

     String loaiNhom;

     Boolean canDuyetBaiDang;
}
