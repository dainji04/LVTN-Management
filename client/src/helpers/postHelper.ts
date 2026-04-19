import callApi from "./mixinApi";

const addCommentHelper = async (content: string, postId: number, userId: number) => {
    if (content.trim() !== '') {
        const result = await callApi(`/binh-luan`, 'POST', {
            maBaiDang: postId,
            noiDung: content,
            maNguoiDung: userId,
        });
        return result;
    }
};

interface ToggleLikePostResponse {
    message: string | null;
    data: boolean | null;
}

const toggleLikePostHelper = async (postId: number, userId: number, objectType: 'BaiViet', reactionType: boolean): Promise<ToggleLikePostResponse> => {
    const result = await callApi(`/luot-thich/toggle`, 'POST', {
        maNguoiDung: userId,
        maDoiTuong: postId,
        loaiDoiTuong: objectType,
        camXuc: reactionType,
    });
    if (result.data && result.message) {
        return result;
    }
    return {
        message: null,
        data: null,
    };
};

export { addCommentHelper, toggleLikePostHelper };