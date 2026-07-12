import { fail, redirect, type PageServerAction } from '@analogjs/router/server/actions';
import { getHeader, readFormData } from 'h3';

export async function action({ event }: PageServerAction) {
  const body = await readFormData(event);
  const cookieHeader = getHeader(event, 'cookie');

  const title = body.get('title') as string | null;
  const description = body.get('description') as string | null;
  const content = body.get('content') as string | null;
  const isPrivate = body.get('isPrivate') as string | null;
  const userId = body.get('userId') as string | null;

  if (!title || !description || !content || !isPrivate || !userId) {
    return fail(422, { error: 'All fields are required' });
  }

  const payload = JSON.stringify({ 
    title, 
    description, 
    content, 
    isPrivate: isPrivate === 'true',
    userId
  });

  console.log(payload);

  
  const response = await fetch('https://localhost:8443/api/contents', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...(cookieHeader ? { 'Cookie': cookieHeader } : {}),
    },
    body: payload,
    credentials: 'include',
  });

  if (response.ok) {
    return redirect('/');
  }

  return fail(422, { 
    error: 'Content creation failed',
    response: response.status,
    errorMessage: response.text()
  });
}
