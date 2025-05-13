import { Request, Response } from "express";

export const handleGoogleAuthCallback = async (
  err: any,
  user: any,
  req: Request,
  res: Response
): Promise<void> => {
  const state =
    typeof req.query.state === "string" ? JSON.parse(req.query.state) : {};
  const origin =
    state.origin || process.env.CLIENT_URL || "http://localhost:3000";

  if (err || !user || user.error) {
    return res.redirect(
      `${origin}?status=error&message=${encodeURIComponent(
        err?.message || user?.message || "Authentication failed"
      )}`
    );
  }

  return res.redirect(
    `${origin}?status=success#access_token=sajidhaljsdnbliufabcfsjdsabonasdasduhfisuadfguadiali&token_type=bearer`
  );
};
