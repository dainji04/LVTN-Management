import { defineStore } from "pinia";
import axiosInstance from "../helpers/apiHelper";
import type { GroupItem } from "../types/groupType";

interface GroupState {
  groups: GroupItem[];
  group: GroupItem | null;
  error: string;
}

export const useGroupStore = defineStore("group", {
  state: (): GroupState => ({
    groups: [],
    group: null,
    error: ""
  }),
  actions: {
    async loadListGroup(): Promise<void> {
      const response = await axiosInstance.get('/group/all');
      if (response.data.code === 200 || response.data.code === 201) {
        this.groups = response.data.data;
      } else {
        this.error = response.data.message;
      }
    },
    async getDetailGroup(groupId: number): Promise<void> {
      const response = await axiosInstance.get(`/group/${groupId}`);
      if (response.data.code === 200 || response.data.code === 201) {
        this.group = response.data.data;
      } else {
        this.error = response.data.message;
      }
    },
  },
});
