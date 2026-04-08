import type { User } from "./userType";

export interface ApiResponse {
  code: number;
  message: string | null;
  data: User[];
}

export interface LoginResponse {
  code: number;
  message: string;
  data: {
    token: string;
    authenticated: boolean;
    thongTinUser: User | null;
  };
}

export interface RegisterResponse {
  code: number;
  message: string;
  data: {
    maNguoiDung: number;
    ho: string;
    ten: string;
    bietDanh: string;
    email: string;
    anhDaiDien: string;
    anhNen: string;
    ngaySinh: string;
    gioiThieu: string;
    noiLamViec: string;
    noiHocTap: string;
    soDienThoai: string;
    ngayTao: string;
    ngayCapNhat: string;
  }
}

export interface LogoutResponse {
  code: number;
  message: string;
  data: Object;
}