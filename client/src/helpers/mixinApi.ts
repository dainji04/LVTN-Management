import axiosInstance from "./apiHelper";

export interface ApiResponse {
  status: boolean;
  message: string | null;
  data: any | null;
}

export default async function callApi(endpoint: string, method: string, data: any): Promise<ApiResponse> {
  try {
    const response = await axiosInstance.request({
      url: endpoint,
      method: method,
      data: data,
    });
    
    console.log(response);
    return {
      status: true,
      message: response.data.message,
      data: response.data.data,
    };
  } catch (error: any) {
    console.error(error);
    return {
      status: false,
      data: null,
      message: error.message,
    };
  }
}