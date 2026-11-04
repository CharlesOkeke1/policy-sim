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

  let response = await fetch(url, config);

  // 2. Handle the Reactive Refresh (If Token is Expired)
  if (response.status === 401) {
    const refreshSuccess = await handleTokenRefresh();
    
    if (refreshSuccess) {
      response = await fetch(url, config);
    } else {
      // Refresh token failed or expired
      //sessionStorage.clear();
      window.location.href = '/login';
      throw new Error('Session expired. Please log in again.');
    }
  }

  if (!response.ok) {
    throw new Error(`API Error: ${response.statusText}`);
  }

  return response.json() as Promise<T>;
}

export async function handleTokenRefresh(): Promise<boolean> {
  const refreshToken = localStorage.getItem('refreshToken');
  if (!refreshToken) return false;

  try {
    const res = await fetch('http://localhost:8080/api/users/refresh', {
      method: 'POST',
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
