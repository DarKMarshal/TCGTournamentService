/**
 * Wrapper around fetch that automatically attaches the JWT Bearer token
 * from localStorage to the Authorization header.
 *
 * Use this for any API call that requires authentication.
 * For requests with a JSON body, the Content-Type header is set automatically
 * unless the body is FormData (in which case the browser sets multipart headers).
 */
export function authFetch(url: string, options: RequestInit = {}): Promise<Response> {
  const token = localStorage.getItem("token");
  const headers = new Headers(options.headers);

  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  // Auto-set Content-Type for non-FormData bodies
  if (options.body && !(options.body instanceof FormData) && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  return fetch(url, {
    ...options,
    headers,
  });
}
