interface FetchOptions {
  method: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH';
  url: string;
  credentials?: 'include' | 'same-origin' | 'omit' ;
  body?: unknown;
}

/**
 * Once the credentials: "include" prop is set and working, all storage of the authentication tokens in session storage will end
 * Only the refreshToken will be stored in localStorage. 
 */

export const FAILED_AUTH_RETRY = 10;

export async function customFetch<T>({ method, url, body }: FetchOptions): Promise<T> {

  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
  };

  const config: RequestInit = {
    method,
    credentials: 'include',
    headers,
    body: body ? JSON.stringify(body) : undefined,
  };

  try {
    let response = await fetch(url, config);

    // 2. Handle the Reactive Refresh (If Token is Expired)
    if (response.status === 401) {
      const refreshSuccess = await handleTokenRefresh();
      
      if (refreshSuccess) {
        response = await fetch(url, config);
      } else {
        // Refresh token failed or expired
        //sessionStorage.clear();
        throw new Error('Session expired. Please log in again.');
      }
    } else if (response.status === 404) {
      window.location.href = "/not-found";
    }

    if (!response.ok) {
      throw new Error(`API Error: ${response.statusText}`);
    }

    return response.json() as Promise<T>;
  } catch (error) {
    if (error instanceof TypeError) {
      window.location.href = "/server-error";
    }
    throw error;
  }
}

export async function handleTokenRefresh(): Promise<boolean> {
  const refreshToken = localStorage.getItem('refreshToken');
  if (!refreshToken) return false;

  try {
    const res = await fetch(`${devurl}/api/users/refresh`, {
      method: 'POST',
      credentials: "include",
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken }),
    });

    if (res.ok) {
      const data = await res.json(); 
      localStorage.setItem('refreshToken', data.result.refreshToken);
      return true;
    }
  } catch (error) {
    console.error('Failed to refresh token', error);
  }
  return false;
}


export const produrl = "https://policysimapi.charlesokeke.com";
export const devurl = "http://localhost:8080";

export const devSocketUrl = "ws://localhost:8081";
export const prodSocketUrl = "wss://ws.charlesokeke.com";