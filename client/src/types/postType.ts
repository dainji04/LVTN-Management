export interface PostItem {
  maBaiViet: number;
  maNguoiDung: number;
  hoTen: string;
  noiDung: string;
  daSua: boolean;
  quyenRiengTu: string;
  viTri: string;
  mauNen: string;
  luotThich: number;
  luotBinhLuan: number;
  luotChiaSe: number;
  ngayTao: string;
  ngayCapNhat: string;
  anhDaiDienNguoiDang: string;
  danhSachAnh: string[];
  daThich: boolean;
}

export interface PostPagingData {
  content: PostItem[];
  hasNext: boolean;
  page: number;
  size: number;
}

export interface PostListResponse {
  code: number;
  message: string;
  data: PostPagingData;
}

export interface CreatePostRequest {
  maNguoiDung: number;
  maNhom: number | null;
  noiDung: string;
  quyenRiengTu: string;
  viTri: string | null;
  mauNen: string | null;
  danhSachCongTacVien: number[] | null;
  danhSachAnh: string[] | null;
}

export interface EditPostRequest extends CreatePostRequest {
  maBaiViet: number;
}