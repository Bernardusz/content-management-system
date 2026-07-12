import { fail, redirect, type PageServerAction } from '@analogjs/router/server/actions';
import { readFormData } from 'h3';

export async function action({ event }: PageServerAction) {
  const body = await readFormData(event);

  const username = body.get('username') as string | null;
  const password = body.get('password') as string | null;

  if (!username || !password) {
    return fail(422, { error: 'All fields are required', username, password });
  }

  const payload = JSON.stringify({ username, password });
  const response = await fetch('https://localhost:8443/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'content-length': Buffer.byteLength(payload).toString(),
    },
    body: payload,
    credentials: 'include',
  });

  if (response.ok) {
    const setCookieHeaders = response.headers.getSetCookie?.() ?? [];
    const setCookie = setCookieHeaders.length > 0
      ? setCookieHeaders
      : response.headers.get('set-cookie');

    if (setCookie && event.node.res) {
      event.node.res.setHeader('set-cookie', setCookie);
    }

    console.log("Successfully logged in");
    return {success: true};
  }

  return fail(422, { error: 'Form submission failed' });
}
