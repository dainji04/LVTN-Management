import { defineStore } from "pinia";
import axiosInstance from "../helpers/apiHelper";
import type { CreatePostRequest, EditPostRequest, PostItem, PostListResponse } from "../types/postType";

interface PostState {
  posts: PostItem[];
  postsByUserId: PostItem[];
  page: number;
  size: number;
  hasNext: boolean;
  loading: boolean;
  error: string;
}

export const usePostStore = defineStore("post", {
  state: (): PostState => ({
    posts: [],
    postsByUserId: [],
    page: 1,
    size: 10,
    hasNext: true,
    loading: false,
    error: "",
  }),
  actions: {
    async fetchPosts(page = 1): Promise<void> {
      if (this.loading) return;

      this.loading = true;
      this.error = "";

      try {
        const response = await axiosInstance.get<PostListResponse>("/bai-viet", {
          params: {
            page,
            size: this.size,
          },
        });

        const result = response.data;
        if (result.code !== 200 && result.code !== 0) {
          this.error = result.message || "Khong the tai bai viet";
          return;
        }

        const content = result.data?.content || [];
        if (page === 1) {
          this.posts = content;
        } else {
          this.posts = [...this.posts, ...content];
        }

        this.page = result.data?.page ?? page;
        this.hasNext = !!result.data?.hasNext;
      } catch (error: any) {
        this.error = error?.response?.data?.message || "Khong the tai bai viet";
      } finally {
        this.loading = false;
      }
    },

    async fetchNextPage(): Promise<void> {
      if (!this.hasNext || this.loading) return;
      await this.fetchPosts(this.page + 1);
    },

    async fetchFirstPage(): Promise<void> {
      this.page = 1;
      this.hasNext = true;
      await this.fetchPosts(1);
    },

    // ======CRUD====

    async createPost(post: CreatePostRequest): Promise<boolean> {
      try {
        const response = await axiosInstance.post('/bai-viet', post);
        if (response.data.code !== 200) {
          throw new Error(response.data.message);
        }
        return true;
      } catch (error) {
        console.error(error);
        return false;
      }
    },

    async deletePost(postId: number): Promise<boolean> {
      try {
        const response = await axiosInstance.delete(`/bai-viet/${postId}`);
        if (response.data.code !== 200) {
          throw new Error(response.data.message);
        }
        return true;
      } catch (error) {
        console.error(error);
        return false;
      }
    },

    async editPost(post: EditPostRequest): Promise<boolean> {
      try {
        const response = await axiosInstance.put(`/bai-viet/${post.maBaiViet}`, post);
        if (response.data.code !== 200) {
          throw new Error(response.data.message);
        }
        return true;
      } catch (error) {
        console.error(error);
        return false;
      }
    },

    updatePostContent(maBaiViet: number, noiDung: string): void {
      const idx = this.posts.findIndex((p) => p.maBaiViet === maBaiViet);
      console.log(noiDung,idx);
      // if (idx !== -1) {
      //   this.posts[idx] = {
      //     ...this.posts[idx],
      //     noiDung,
      //     daSua: true,
      //   };
      // }
    },

    async fetchPostsByUserId(userId: number, page = 1): Promise<void> {
      if (this.loading) return;

      this.loading = true;
      this.error = "";

      try {
        const response = await axiosInstance.get<PostListResponse>(`/bai-viet/user/${userId}`, {
          params: {
            page,
            size: this.size,
          },
        });

        const result = response.data;
        if (result.code !== 200 && result.code !== 0) {
          this.error = result.message || "Khong the tai bai viet";
          return;
        }

        const content = result.data?.content || [];
        if (page === 1) {
          this.postsByUserId = content;
        } else {
          this.postsByUserId = [...this.postsByUserId, ...content];
        }

        this.page = result.data?.page ?? page;
        this.hasNext = !!result.data?.hasNext;
      } catch (error: any) {
        this.error = error?.response?.data?.message || "Khong the tai bai viet";
      } finally {
        this.loading = false;
      }
    },

    async fetchNextPageByUserId(userId: number): Promise<void> {
      if (!this.hasNext || this.loading) return;
      await this.fetchPostsByUserId(userId, this.page + 1);
    },

    async fetchFirstPageByUserId(userId: number): Promise<void> {
      this.hasNext = true;
      await this.fetchPostsByUserId(userId, 0);
    },
  },
});
