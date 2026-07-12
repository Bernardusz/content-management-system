import { PageServerAction, redirect } from "@analogjs/router/server/actions";
import { getHeader, readFormData } from "h3";

export async function action ({ event }: PageServerAction) {

    const body = await readFormData(event);

    const title = body.get('title') as string | null;
    const content = body.get('content') as string | null;
    const contentId = body.get('contentId') as number | null;
    const userId = body.get('userId') as number | null;

    const cookieHeader = getHeader(event, 'cookie');

    try {
        const creationResponse = await fetch('https://localhost:8443/api/comments', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...(cookieHeader ? { 'Cookie': cookieHeader } : {}),
            },
            body: JSON.stringify({ title, content, userId, contentId }),
        });
    
        if (creationResponse.ok){
            const location = creationResponse.headers.get('location') as string;

            const response = await fetch(location, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    ...(cookieHeader ? { 'Cookie': cookieHeader } : {}),
                },
            });

            return {
                success: true,
                data: await response.json()
            }
        }

        else throw new Error();
    }
    catch (error) {
        return { success: false };
    }
}