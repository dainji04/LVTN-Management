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
