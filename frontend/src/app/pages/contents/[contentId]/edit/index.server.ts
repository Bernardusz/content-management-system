import { fail, redirect, type PageServerAction } from '@analogjs/router/server/actions';
import { getHeader, readFormData } from 'h3';
import type { ContentDetail, ContentUpdate } from '@/types/content.d';
import { PageServerLoad } from '@analogjs/router';

export const load = async ({
	params, // params/queryParams from the request
	req, // H3 Request
	res, // H3 Response handler
	fetch, // internal fetch for direct API calls,
	event, // full request event
}: PageServerLoad) => {
	if (!params || !params['contentId']) {
		return null;
	}
	const contentId = params['contentId'];

	const backendUrl = "https://localhost:8443/api/contents";
	try {
		const contentDetail = await fetch<ContentDetail>(`${backendUrl}/${contentId}`, {
			headers: {
				cookie: req.headers.cookie || "",
			},
		});

		return contentDetail;
	} catch (error) {
		console.error(
			"❌ Analog server loader failed to fetch user details:",
			error,
		);
		return null;
	}
};

export async function action({ event }: PageServerAction) {
	const body = await readFormData(event);
	const cookieHeader = getHeader(event, 'cookie');


	const title = body.get('title') as string | null;
	const description = body.get('description') as string | null;
	const content = body.get('content') as string | null;
	const isPrivate = body.get('isPrivate') as string | null;
	const contentId = body.get('contentId') as number | null;

	console.log({
		title,
		description,
		content,
		isPrivate,
		contentId
	});

	if (!title || !description || !content || !isPrivate || !contentId) {
		return fail(422, { error: 'All fields are required' });
	}

	const payload = JSON.stringify({ 
		title, 
		description, 
		content, 
		isPrivate: isPrivate === 'true'
	});

	
	const response = await fetch(`https://localhost:8443/api/contents/${contentId}`, {
		method: 'PUT',
		headers: {
			'Content-Type': 'application/json',
			...(cookieHeader ? { 'Cookie': cookieHeader } : {}),
		},
		body: payload,
		credentials: 'include',
	});

	if (response.ok) {
		return redirect(`/contents/${contentId}`);
	}

	return fail(422, { error: 'Content update failed' });
}
