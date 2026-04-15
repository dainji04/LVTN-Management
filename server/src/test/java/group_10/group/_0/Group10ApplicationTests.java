package group_10.group._0;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Group10ApplicationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    // =====================================================
    // SHARED STATE – lưu qua các test theo thứ tự
    // =====================================================
    static String tokenA;
    static String tokenB;
    static String tokenC;
    static Integer nhomPrivateId;
    static Integer nhomPublicId;
    static Integer yeuCauId;      // yêu cầu của B vào nhóm PRIVATE
    static Integer yeuCauCId;     // yêu cầu của C vào nhóm PRIVATE (để reject)
    static Integer thanhVienBId;  // id bản ghi ThanhVienNhom của B
    static Integer baiVietPendingId;
    static Integer theoDoiId;

    // =====================================================
    // EMAIL/MẬT KHẨU của 3 user đã tạo sẵn
    // =====================================================
    static final String EMAIL_A   = "usera@test.com";
    static final String EMAIL_B   = "userb@test.com";
    static final String EMAIL_C   = "userc@test.com";
    static final String PASSWORD  = "password123";

    // =====================================================
    // HELPER
    // =====================================================
    private String loginAndGetToken(String email, String password) throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("email", email, "password", password));

        MvcResult result = mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(
                result.getResponse().getContentAsString())
                .path("data").path("token").asText();
    }

    // =====================================================
    // BƯỚC 0 – LẤY TOKEN
    // =====================================================

    @Test
    @Order(1)
    @DisplayName("Setup – Lấy token cho User A, B, C")
    void setup_getTokens() throws Exception {
        tokenA = loginAndGetToken(EMAIL_A, PASSWORD);
        tokenB = loginAndGetToken(EMAIL_B, PASSWORD);
        tokenC = loginAndGetToken(EMAIL_C, PASSWORD);

        Assertions.assertFalse(tokenA.isBlank(), "tokenA phải có giá trị");
        Assertions.assertFalse(tokenB.isBlank(), "tokenB phải có giá trị");
        Assertions.assertFalse(tokenC.isBlank(), "tokenC phải có giá trị");
    }

    // =====================================================
    // MODULE 1 – NHÓM
    // =====================================================

    @Test
    @Order(10)
    @DisplayName("TC-G01 ✅ Tạo nhóm PRIVATE thành công")
    void tc_G01_taoNhomPrivate() throws Exception {
        String body = """
                {
                  "tenNhom": "Nhom Rieng Tu Test",
                  "moTa": "Chu danh cho thanh vien duoc moi",
                  "loaiNhom": "PRIVATE",
                  "canDuyetBaiDang": true
                }
                """;

        MvcResult result = mockMvc.perform(post("/group")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.loaiNhom").value("PRIVATE"))
                .andReturn();

        nhomPrivateId = objectMapper.readTree(
                result.getResponse().getContentAsString())
                .path("data").path("id").asInt();

        Assertions.assertNotNull(nhomPrivateId);
    }

    @Test
    @Order(11)
    @DisplayName("TC-G02 ✅ Tạo nhóm PUBLIC thành công")
    void tc_G02_taoNhomPublic() throws Exception {
        String body = """
                {
                  "tenNhom": "Nhom Cong Khai Test",
                  "moTa": "Ai cung co the tham gia",
                  "loaiNhom": "PUBLIC",
                  "canDuyetBaiDang": false
                }
                """;

        MvcResult result = mockMvc.perform(post("/group")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.loaiNhom").value("PUBLIC"))
                .andReturn();

        nhomPublicId = objectMapper.readTree(
                result.getResponse().getContentAsString())
                .path("data").path("id").asInt();

        Assertions.assertNotNull(nhomPublicId);
    }

    @Test
    @Order(12)
    @DisplayName("TC-G03 ❌ Tạo nhóm – Token không hợp lệ → 401")
    void tc_G03_taoNhom_tokenSai() throws Exception {
        String body = """
                { "tenNhom": "Ghost", "loaiNhom": "PUBLIC" }
                """;

        // Spring Security trả 401 với body rỗng (không có JSON) → chỉ check status
        mockMvc.perform(post("/group")
                        .header("Authorization", "Bearer TOKEN_SAI_XYZ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(13)
    @DisplayName("TC-G04 ✅ Cập nhật thông tin nhóm PRIVATE")
    void tc_G04_capNhatNhom() throws Exception {
        String body = """
                {
                  "tenNhom": "Nhom Rieng Tu Test - Doi Ten",
                  "moTa": "Mo ta moi",
                  "loaiNhom": "PRIVATE",
                  "canDuyetBaiDang": true
                }
                """;

        mockMvc.perform(put("/group/" + nhomPrivateId)
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tenNhom").value("Nhom Rieng Tu Test - Doi Ten"));
    }

    @Test
    @Order(14)
    @DisplayName("TC-G05 ❌ Cập nhật nhóm không tồn tại → 404")
    void tc_G05_capNhatNhom_khongTonTai() throws Exception {
        String body = """
                { "tenNhom": "Ghost", "loaiNhom": "PUBLIC" }
                """;

        mockMvc.perform(put("/group/9999999")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(4004));
    }

    // =====================================================
    // MODULE 2 – THAM GIA NHÓM
    // =====================================================

    @Test
    @Order(20)
    @DisplayName("TC-J01 ✅ B xin vào nhóm PRIVATE → DA_GUI")
    void tc_J01_xinVaoNhomPrivate() throws Exception {
        String body = String.format("""
                { "maNhom": %d, "maNguoiDung": %s }
                """, nhomPrivateId, extractUserId(tokenB));

        MvcResult result = mockMvc.perform(post("/access-to-group")
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.trangThai").value("DA_GUI"))
                .andReturn();

        yeuCauId = objectMapper.readTree(
                result.getResponse().getContentAsString())
                .path("data").path("id").asInt();
    }

    @Test
    @Order(21)
    @DisplayName("TC-J02 ✅ B xin vào nhóm PUBLIC → CHAP_NHAN tự động")
    void tc_J02_xinVaoNhomPublic_autoJoin() throws Exception {
        String body = String.format("""
                { "maNhom": %d, "maNguoiDung": %s }
                """, nhomPublicId, extractUserId(tokenB));

        mockMvc.perform(post("/access-to-group")
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.trangThai").value("CHAP_NHAN"));

        // Xác nhận B đã xuất hiện trong ThanhVienNhom (thêm auth)
        mockMvc.perform(get("/thanhvien-group/group/" + nhomPublicId)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(22)
    @DisplayName("TC-J03 ❌ Xin vào nhóm không tồn tại → 404")
    void tc_J03_xinVaoNhom_khongTonTai() throws Exception {
        String body = """
                { "maNhom": 9999999, "maNguoiDung": 1 }
                """;

        mockMvc.perform(post("/access-to-group")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(4004));
    }

    @Test
    @Order(23)
    @DisplayName("TC-J04 ✅ Admin xem danh sách yêu cầu chờ duyệt")
    void tc_J04_xemDsYeuCau() throws Exception {
        mockMvc.perform(get("/access-to-group/group/" + nhomPrivateId)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(24)
    @DisplayName("TC-J05 ✅ Admin CHẤP NHẬN yêu cầu của B")
    void tc_J05_adminChapNhan() throws Exception {
        String body = """
                { "trangThai": "CHAP_NHAN" }
                """;

        mockMvc.perform(put("/access-to-group/" + yeuCauId + "/accept")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk());

        // Xác nhận B đã là thành viên nhóm PRIVATE
        MvcResult listResult = mockMvc.perform(get("/thanhvien-group/group/" + nhomPrivateId)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andReturn();

        String listJson = listResult.getResponse().getContentAsString();
        var arr = objectMapper.readTree(listJson).path("data");
        String userBId = extractUserId(tokenB).toString();
        for (var node : arr) {
            // Tìm bản ghi của B
            if (!node.path("maNguoiDung").asText().equals(userBId)) continue;
            thanhVienBId = node.path("id").asInt();
        }
    }

    @Test
    @Order(25)
    @DisplayName("TC-J06 ❌ Accept lần 2 cùng yêu cầu → REQUEST_IS_PROCESSED (409)")
    void tc_J06_acceptLaitLan2() throws Exception {
        String body = """
                { "trangThai": "CHAP_NHAN" }
                """;

        mockMvc.perform(put("/access-to-group/" + yeuCauId + "/accept")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(9003));
    }

    @Test
    @Order(26)
    @DisplayName("TC-J07 ✅ Admin TỪ CHỐI yêu cầu của C")
    void tc_J07_adminTuChoi() throws Exception {
        // C gửi yêu cầu tham gia nhóm PRIVATE
        String bodyReq = String.format("""
                { "maNhom": %d, "maNguoiDung": %s }
                """, nhomPrivateId, extractUserId(tokenC));

        MvcResult reqResult = mockMvc.perform(post("/access-to-group")
                        .header("Authorization", "Bearer " + tokenC)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyReq))
                .andExpect(status().isOk())
                .andReturn();

        yeuCauCId = objectMapper.readTree(
                reqResult.getResponse().getContentAsString())
                .path("data").path("id").asInt();

        // Admin từ chối
        String bodyReject = """
                { "trangThai": "TU_CHOI" }
                """;

        mockMvc.perform(put("/access-to-group/" + yeuCauCId + "/reject")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyReject))
                .andExpect(status().isOk());
    }

    @Test
    @Order(27)
    @DisplayName("TC-J08 ❌ Reject – Token không hợp lệ → 401")
    void tc_J08_reject_tokenSai() throws Exception {
        String body = """
                { "trangThai": "TU_CHOI" }
                """;

        mockMvc.perform(put("/access-to-group/" + yeuCauCId + "/reject")
                        .header("Authorization", "Bearer TOKEN_SAI_XYZ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(28)
    @DisplayName("TC-J09 ✅ C tự hủy yêu cầu tham gia")
    void tc_J09_huyYeuCau() throws Exception {
        // C gửi yêu cầu mới
        String bodyReq = String.format("""
                { "maNhom": %d, "maNguoiDung": %s }
                """, nhomPrivateId, extractUserId(tokenC));

        MvcResult req = mockMvc.perform(post("/access-to-group")
                        .header("Authorization", "Bearer " + tokenC)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyReq))
                .andReturn();

        Integer idHuy = objectMapper.readTree(req.getResponse().getContentAsString())
                .path("data").path("id").asInt();

        // C hủy (thêm auth)
        mockMvc.perform(delete("/access-to-group/" + idHuy)
                        .header("Authorization", "Bearer " + tokenC))
                .andExpect(status().isOk());
    }

    @Test
    @Order(29)
    @DisplayName("TC-J10 ❌ Hủy yêu cầu – ID không tồn tại → 404")
    void tc_J10_huyYeuCau_khongTonTai() throws Exception {
        mockMvc.perform(delete("/access-to-group/9999999")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(5002));
    }

    // =====================================================
    // MODULE 3 – BÀI VIẾT
    // =====================================================

    @Test
    @Order(30)
    @DisplayName("TC-B01 ✅ B đăng bài trong nhóm PRIVATE → PENDING")
    void tc_B01_dangBaiPending() throws Exception {
        String body = String.format("""
                {
                  "maNguoiDung": %s,
                  "maNhom": %d,
                  "noiDung": "Bai viet cua B trong nhom rieng tu",
                  "danhSachAnh": []
                }
                """, extractUserId(tokenB), nhomPrivateId);

        MvcResult result = mockMvc.perform(post("/bai-viet")
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.trangThai").value("PENDING"))
                .andReturn();

        baiVietPendingId = objectMapper.readTree(
                result.getResponse().getContentAsString())
                .path("data").path("maBaiViet").asInt();
    }

    @Test
    @Order(31)
    @DisplayName("TC-B02 ✅ B đăng bài trong nhóm PUBLIC → APPROVED ngay")
    void tc_B02_dangBaiApproved() throws Exception {
        String body = String.format("""
                {
                  "maNguoiDung": %s,
                  "maNhom": %d,
                  "noiDung": "Bai viet cong khai cua B",
                  "danhSachAnh": []
                }
                """, extractUserId(tokenB), nhomPublicId);

        mockMvc.perform(post("/bai-viet")
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.trangThai").value("APPROVED"));
    }

    @Test
    @Order(32)
    @DisplayName("TC-B03 ❌ C đăng bài vào nhóm PRIVATE khi chưa là thành viên → bị chặn")
    void tc_B03_dangBai_khongThanhVien() throws Exception {
        String body = String.format("""
                {
                  "maNguoiDung": %s,
                  "maNhom": %d,
                  "noiDung": "Hack vao nhom rieng tu",
                  "danhSachAnh": []
                }
                """, extractUserId(tokenC), nhomPrivateId);

        mockMvc.perform(post("/bai-viet")
                        .header("Authorization", "Bearer " + tokenC)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Order(33)
    @DisplayName("TC-B04 ✅ Admin DUYỆT bài PENDING → APPROVED")
    void tc_B04_adminDuyetBai() throws Exception {
        mockMvc.perform(put("/bai-viet/xu-ly-dang-bai/" + baiVietPendingId)
                        .header("Authorization", "Bearer " + tokenA)
                        .param("kqTrangThai", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.trangThai").value("APPROVED"));
    }

    // =====================================================
    // MODULE 4 – THÀNH VIÊN NHÓM
    // =====================================================

    @Test
    @Order(40)
    @DisplayName("TC-TV01 ✅ Xem danh sách thành viên nhóm PRIVATE")
    void tc_TV01_xemDsThanhVien() throws Exception {
        MvcResult result = mockMvc.perform(get("/thanhvien-group/group/" + nhomPrivateId)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andReturn();

        // Lưu thanhVienBId nếu chưa có từ bước accept
        if (thanhVienBId == null) {
            var arr = objectMapper.readTree(result.getResponse().getContentAsString()).path("data");
            for (var node : arr) {
                if (node.path("vaiTro").asText().equals("MEMBER")) {
                    thanhVienBId = node.path("id").asInt();
                    break;
                }
            }
        }
    }

    @Test
    @Order(41)
    @DisplayName("TC-TV02 ✅ Đếm số thành viên nhóm PRIVATE ≥ 1")
    void tc_TV02_demThanhVien() throws Exception {
        mockMvc.perform(get("/thanhvien-group/group/" + nhomPrivateId + "/count")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", greaterThanOrEqualTo(1)));
    }

    @Test
    @Order(42)
    @DisplayName("TC-TV03 ✅ Nâng B lên MODERATOR")
    void tc_TV03_nangCapVaiTro() throws Exception {
        Assumptions.assumeTrue(thanhVienBId != null, "Bỏ qua: chưa có thanhVienBId");

        String body = """
                { "vaiTro": "MODERATOR" }
                """;

        mockMvc.perform(put("/thanhvien-group/" + thanhVienBId)
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.vaiTro").value("MODERATOR"));
    }

    @Test
    @Order(43)
    @DisplayName("TC-TV05 ❌ Kick thành viên – ID không tồn tại → 404")
    void tc_TV05_kick_khongTonTai() throws Exception {
        mockMvc.perform(delete("/thanhvien-group/9999999")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(4005));
    }

    // =====================================================
    // MODULE 5 – THEO DÕI
    // =====================================================

    @Test
    @Order(50)
    @DisplayName("TC-TD01 ✅ A theo dõi B (có thông báo)")
    void tc_TD01_theoDoi() throws Exception {
        String body = String.format("""
                { "maNguoiTheoDoi": %s, "maNguoiDuocTheoDoi": %s }
                """, extractUserId(tokenA), extractUserId(tokenB));

        mockMvc.perform(post("/theo-doi")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(51)
    @DisplayName("TC-TD02 ✅ A theo dõi C ngầm (không thông báo)")
    void tc_TD02_theoDoiNgam() throws Exception {
        String body = String.format("""
                { "maNguoiTheoDoi": %s, "maNguoiDuocTheoDoi": %s }
                """, extractUserId(tokenA), extractUserId(tokenC));

        MvcResult result = mockMvc.perform(post("/theo-doi/khong-thong-bao")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();

        theoDoiId = objectMapper.readTree(
                result.getResponse().getContentAsString())
                .path("data").path("id").asInt();

        // Đảm bảo theoDoiId hợp lệ (> 0) dù là record mới hay đã tồn tại (idempotent)
        Assertions.assertTrue(theoDoiId > 0, "theoDoiId phải > 0 sau khi theo dõi");
    }

    @Test
    @Order(52)
    @DisplayName("TC-TD03 ✅ Xem thống kê theo dõi User B")
    void tc_TD03_thongKeTheoDoi() throws Exception {
        mockMvc.perform(get("/theo-doi/user/" + extractUserId(tokenB))
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tongDangTheoDoi").exists())
                .andExpect(jsonPath("$.data.tongNguoiTheoDoi").exists());
    }

    @Test
    @Order(53)
    @DisplayName("TC-TD05 ✅ Hủy theo dõi")
    void tc_TD05_huyTheoDoi() throws Exception {
        Assumptions.assumeTrue(theoDoiId != null && theoDoiId > 0, "Bỏ qua: chưa có theoDoiId");

        mockMvc.perform(delete("/theo-doi/" + theoDoiId)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk());
    }

    @Test
    @Order(54)
    @DisplayName("TC-TD06 ❌ Hủy theo dõi – ID không tồn tại → 404")
    void tc_TD06_huyTheoDoi_khongTonTai() throws Exception {
        mockMvc.perform(delete("/theo-doi/9999999")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(4006));
    }

    // =====================================================
    // MODULE 6 – XÓA NHÓM (chạy cuối cùng)
    // =====================================================

    @Test
    @Order(60)
    @DisplayName("TC-TV04 ✅ Kick B khỏi nhóm PRIVATE (trước khi xóa nhóm)")
    void tc_TV04_kickThanhVien() throws Exception {
        Assumptions.assumeTrue(thanhVienBId != null, "Bỏ qua: chưa có thanhVienBId");

        mockMvc.perform(delete("/thanhvien-group/" + thanhVienBId)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk());
    }

    @Test
    @Order(61)
    @DisplayName("TC-XN02 ❌ B thử xóa nhóm PRIVATE (không phải người tạo) → bị chặn")
    void tc_XN02_xoaNhom_khongPhanQuyen() throws Exception {
        // Server trả 400 (4xx) vì RuntimeException được GlobalExceptionHandler bắt
        mockMvc.perform(delete("/group/" + nhomPrivateId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Order(62)
    @DisplayName("TC-XN03 ❌ Xóa nhóm – Token không hợp lệ → 401")
    void tc_XN03_xoaNhom_tokenSai() throws Exception {
        mockMvc.perform(delete("/group/" + nhomPrivateId)
                        .header("Authorization", "Bearer TOKEN_SAI_XYZ"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(63)
    @DisplayName("TC-XN04 ❌ Xóa nhóm không tồn tại → 404")
    void tc_XN04_xoaNhom_khongTonTai() throws Exception {
        mockMvc.perform(delete("/group/9999999")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(4004));
    }

    @Test
    @Order(64)
    @DisplayName("TC-XN01 ✅ A xóa nhóm PRIVATE – Dọn sạch toàn bộ dữ liệu")
    void tc_XN01_xoaNhomTransactional() throws Exception {
        mockMvc.perform(delete("/group/" + nhomPrivateId)
                        .header("Authorization", "Bearer " + tokenA))
                .andDo(print())
                .andExpect(status().isOk());

        // Kiểm tra nhóm đã mất
        mockMvc.perform(get("/thanhvien-group/group/" + nhomPrivateId)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(jsonPath("$.data", hasSize(0)));

        mockMvc.perform(get("/access-to-group/group/" + nhomPrivateId)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    @Order(65)
    @DisplayName("TC-XN01b ✅ A xóa nhóm PUBLIC")
    void tc_XN01b_xoaNhomPublic() throws Exception {
        mockMvc.perform(delete("/group/" + nhomPublicId)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk());
    }

    // =====================================================
    // HELPER – Lấy maNguoiDung từ token (decode JWT claim "id")
    // =====================================================
    private Integer extractUserId(String jwtToken) {
        try {
            String payload = jwtToken.split("\\.")[1];
            // Thêm padding đúng chuẩn Base64 URL-safe
            int paddingNeeded = (4 - payload.length() % 4) % 4;
            payload = payload + "=".repeat(paddingNeeded);
            byte[] decoded = java.util.Base64.getUrlDecoder().decode(payload);
            var node = objectMapper.readTree(decoded);
            return node.path("id").asInt();
        } catch (Exception e) {
            throw new RuntimeException("Không thể đọc userId từ token", e);
        }
    }

    @Test
    @Order(0)
    @DisplayName("contextLoads")
    void contextLoads() {
    }
}
