import { defineStore } from "pinia";
import axiosInstance from "../helpers/apiHelper";
import type { PostItem, PostListResponse } from "../types/postType";

interface PostState {
  posts: PostItem[];
  page: number;
  size: number;
  hasNext: boolean;
  loading: boolean;
  error: string;
}

export const usePostStore = defineStore("post", {
  state: (): PostState => ({
    posts: [],
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
  },
});
