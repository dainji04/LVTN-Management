export interface GoiYKetBanResponse {
    maNguoiDung: number;
    ho: string;
    ten: string;
    bietDanh: string;
    anhDaiDien: string;
    email: string;
}

export interface FriendItem extends GoiYKetBanResponse {
    hoatDongLanCuoi: string;
    ngayKetBan: string;
}

export interface friendRequestItem {
    id: number;
    maNguoiGui: number;
    hoTenNguoiGui: string;
    anhDaiDienNguoiGui: string;
    maNguoiNhan: number;
    trangThai: string;
}

interface baseApiResponse<T> {
    code: number;
    message: string;
    data: T;
}

export interface ApiRecommendationsResponse extends baseApiResponse<{
    content: GoiYKetBanResponse[];
    hasNext: boolean;
    page: number;
    size: number;
}> {}

export interface ApiFriendRequestResponse extends baseApiResponse<friendRequestItem[]> {}

export interface ApiLoadListFriendsResponse extends baseApiResponse<FriendItem[]> {}

export interface ApiAcceptFriendRequestResponse extends baseApiResponse<{
    code: number;
    message: string;
}> {}