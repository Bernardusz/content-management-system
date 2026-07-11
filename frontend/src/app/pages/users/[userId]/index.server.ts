import { UserDetail } from "@/types/user";
import { PageServerLoad } from "@analogjs/router";
import { fail, PageServerAction, redirect } from "@analogjs/router/server/actions";
import { getHeader, readFormData } from "h3";

export async function load({ event }: PageServerLoad){
    const response = await event.fetch('https://localhost:8443/api/auth/me', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
        credentials: 'include',
    });
    
    const data: UserDetail = await response.json();
    
    return { data };
}

export async function action({ event }: PageServerAction){
    const body = await readFormData(event);

    const userId = body.get('userId') as string | null;
    const action = body.get('action') as string | null;
    const cookieHeader = getHeader(event, 'cookie');
    
    // Safety check fields
    if (!userId || !action){
        return fail(422, { error: 'All fields are required' });
    }

    try {
        if (action === 'information'){
            const email = body.get('email') as string | null;
            const username = body.get('username') as string | null;

            const payload = {
                email, username
            }

            console.log("Sending Info Update:", username, email);

            // CHANGE: Switch to standard fetch to handle empty/custom responses cleanly
            const response = await fetch(`https://localhost:8443/api/users/${userId}/information`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    ...(cookieHeader ? { 'Cookie': cookieHeader } : {}),
                },
                body: JSON.stringify(payload),
            });

            if (response.ok){
                const createdAt = body.get('createdAt');
                return { success: true, action: 'information', data: {
                    ...payload,
                    createdAt
                }};
            }
        }
        
        else if (action === 'password'){
            const password = body.get('password') as string | null;
            const passwordConfirm = body.get('password-confirm') as string | null;
            
            if (password !== passwordConfirm){
                return fail(422, { error: 'Passwords do not match' });
            }
            
            console.log("Sending Password Update:", password);

            // CHANGE: Switch to standard fetch here too
            const response = await fetch(`https://localhost:8443/api/users/${userId}/password`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    ...(cookieHeader ? { 'Cookie': cookieHeader } : {}),
                },
                body: JSON.stringify({ password }),
            });

            if (response.ok){
                return { success: true, action: 'password' };
            }
        }
    } catch (error: any) {
        console.error("Network Fetch Crash Details:", error.message || error);
        return fail(500, { error: 'Internal backend connection issue', details: error.message });
    }

    return fail(422, {
        error: 'Form submission failed - index.server.ts',
    });
}