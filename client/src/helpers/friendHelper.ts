import type { ApiAcceptFriendRequestResponse, ApiFriendRequestResponse, ApiLoadListFriendsResponse, ApiRecommendationsResponse, FriendItem, friendRequestItem, GoiYKetBanResponse } from "../types/friendType";
import axiosInstance from "./apiHelper";

const friendHelper = {
    async getRecommendations(page: number = 0, size: number = 4): Promise<GoiYKetBanResponse[]> {
        const response = await axiosInstance.get<ApiRecommendationsResponse>('/ban-be/goi-y-ket-ban', {
            params: { page, size },
        });
        return response.data.data.content;
    },

    async loadListFriends(currentUserId: number, page: number = 0, size: number = 10): Promise<FriendItem[]> {
        const response = await axiosInstance.get<ApiLoadListFriendsResponse>(`/ban-be/${currentUserId}`, {
            params: { page, size },
        });
        return response.data.data;
    },

    async getFriendRequest(currentUserId: number): Promise<friendRequestItem[]> {
        const response = await axiosInstance.get<ApiFriendRequestResponse>(`/loi-moi-ket-ban/${currentUserId}`, {
        });
        return response.data.data;
    },

    async searchFriends(currentUserId: number, query: string): Promise<FriendItem[]> {
        const response = await axiosInstance.get<ApiLoadListFriendsResponse>(`/ban-be/search`, {
            params: { userId: currentUserId, query },
        });
        return response.data.data;
    },

    async acceptFriendRequest(senderUserId: number, receiverUserId: number, loiMoiId: number): Promise<ApiAcceptFriendRequestResponse> {

        await axiosInstance.patch(`/loi-moi-ket-ban/${loiMoiId}/trang-thai?trangThai=CHAP_NHAN`);
        
        const response = await axiosInstance.post<ApiAcceptFriendRequestResponse>(`/ban-be/add?id1=${receiverUserId}&id2=${senderUserId}&loiMoiId=${loiMoiId}`);
        return response.data;
    }
};

export default friendHelper;