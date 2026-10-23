interface FetchOptions {
  method: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH';
  url: string;
  body?: unknown;
}

export async function customFetch<T>({ method, url, body }: FetchOptions): Promise<T> {
  const accessToken = sessionStorage.getItem('authToken');

  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
  };

  if (accessToken) {
    headers['Authorization'] = `Bearer ${accessToken}`;
  }

  const config: RequestInit = {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  };

  let response = await fetch(url, config);

  // 2. Handle the Reactive Refresh (If Token is Expired)
  if (response.status === 401) {
    const refreshSuccess = await handleTokenRefresh();
    
    if (refreshSuccess) {
      // Get the fresh token that was just saved to storage
      const newAccessToken = sessionStorage.getItem('authToken');
      if (newAccessToken) {
        (config.headers as Record<string, string>)['Authorization'] = `Bearer ${newAccessToken}`;
      }
      // Retry the exact original request
      response = await fetch(url, config);
    } else {
      // Refresh token failed or expired
      //sessionStorage.clear();
      //window.location.href = '/login';
      throw new Error('Session expired. Please log in again.');
    }
  }

  if (!response.ok) {
    throw new Error(`API Error: ${response.statusText}`);
  }

  return response.json() as Promise<T>;
}

async function handleTokenRefresh(): Promise<boolean> {
  const refreshToken = sessionStorage.getItem('refreshToken');
  if (!refreshToken) return false;

  try {
    const res = await fetch('http://localhost:8080/api/users/refresh', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken }),
    });

    if (res.ok) {
      const data = await res.json(); 
      sessionStorage.setItem('authToken', data.result.authToken);
      sessionStorage.setItem('refreshToken', data.result.refreshToken);
      return true;
    }
  } catch (error) {
    console.error('Failed to refresh token', error);
  }
  return false;
}
