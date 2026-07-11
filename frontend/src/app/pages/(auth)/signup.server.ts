import { fail, redirect, type PageServerAction } from '@analogjs/router/server/actions';
import { readFormData } from 'h3';

export async function action({ event }: PageServerAction) {
  const body = await readFormData(event);

  const email = body.get('email') as string | null;
  const username = body.get('username') as string | null;
  const password = body.get('password') as string | null;

  if (!email || !username || !password) {
    return fail(422, { error: 'All fields are required' });
  }

  const payload = JSON.stringify({ email, username, password });
  const response = await fetch('https://localhost:8443/api/auth/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'content-length': Buffer.byteLength(payload).toString(),
    },
    body: payload,
    credentials: 'include',
  });

  if (response.ok) {
    return redirect('/');
  }

  return fail(422, { error: 'Form submission failed' });
}
