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
export interface LogoutResponse {
  code: number;
  message: string;
  data: Object;
}