/* Global API helper for all pages (no build step). */
(function () {
	const STORAGE_KEYS = {
		baseUrl: "admin_social_api_base_url",
		userToken: "admin_social_user_token",
		adminToken: "admin_social_admin_token",
	};

	function getBaseUrl() {
		return (
			localStorage.getItem(STORAGE_KEYS.baseUrl) ||
			"https://api-social.dainji.id.vn"
		);
	}

	function setBaseUrl(value) {
		if (!value) {
			return;
		}
		localStorage.setItem(STORAGE_KEYS.baseUrl, value.trim());
	}

	function getToken(role) {
		const key = role === "admin" ? STORAGE_KEYS.adminToken : STORAGE_KEYS.userToken;
		return localStorage.getItem(key) || "";
	}

	function setToken(role, value) {
		const key = role === "admin" ? STORAGE_KEYS.adminToken : STORAGE_KEYS.userToken;
		if (!value) {
			localStorage.removeItem(key);
			return;
		}
		const trimmed = value.trim();
		const normalized = trimmed.startsWith("Bearer ") ? trimmed.slice(7) : trimmed;
		localStorage.setItem(key, normalized);
	}

	function buildUrl(path, query) {
		const base = getBaseUrl().replace(/\/$/, "");
		const url = new URL(base + path);
		if (query && typeof query === "object") {
			Object.keys(query).forEach((key) => {
				if (query[key] !== undefined && query[key] !== null && query[key] !== "") {
					url.searchParams.set(key, query[key]);
				}
			});
		}
		return url.toString();
	}

	async function request(path, options) {
		const opts = options || {};
		const tokenRole = opts.tokenRole || "user";
		const headers = Object.assign(
			{
				"Content-Type": "application/json",
			},
			opts.headers || {}
		);

		const token = getToken(tokenRole);
		if (token) {
			headers.Authorization = "Bearer " + token;
		}

		const response = await fetch(buildUrl(path, opts.query), {
			method: opts.method || "GET",
			headers,
			body: opts.body ? JSON.stringify(opts.body) : undefined,
		});

		const contentType = response.headers.get("content-type") || "";
		const isJson = contentType.includes("application/json");
		const payload = isJson ? await response.json() : await response.text();

		if (!response.ok) {
			const error = new Error("Request failed");
			error.status = response.status;
			error.payload = payload;
			throw error;
		}

		return payload;
	}

	function prettyJson(value) {
		return JSON.stringify(value, null, 2);
	}

	window.Api = {
		getBaseUrl,
		setBaseUrl,
		getToken,
		setToken,
		request,
		prettyJson,
	};
})();
