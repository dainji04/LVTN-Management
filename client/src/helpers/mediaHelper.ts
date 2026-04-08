const ABSOLUTE_URL_REGEX = /^(https?:)?\/\//i;

let apiBaseUrl = import.meta.env.VITE_API_URL || "http://localhost:8080/SocialMedia";
if (!apiBaseUrl.startsWith("http://") && !apiBaseUrl.startsWith("https://")) {
  apiBaseUrl = `http://${apiBaseUrl}`;
}

const normalizedApiBaseUrl = apiBaseUrl.replace(/\/+$/, "");

export const resolveMediaUrl = (path?: string | null): string => {
  if (!path) return "";
  if (ABSOLUTE_URL_REGEX.test(path)) return path;
  return `${normalizedApiBaseUrl}/${path.replace(/^\/+/, "")}`;
};
