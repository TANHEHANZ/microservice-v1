import { Router } from "express";
import passport from "passport";

const UserAuthRoutes = Router();

UserAuthRoutes.get("/google", (req, res, next) => {
  const origin =
    req.headers.referer || req.headers.origin || process.env.CLIENT_URL;
  console.log(origin);
  passport.authenticate("google", {
    scope: ["profile", "email"],
    session: false,
    state: JSON.stringify({ origin }),
  })(req, res, next);
});

UserAuthRoutes.get("/google/callback", (req, res, next) => {
  passport.authenticate("google", { session: false }, (err, user) => {
    if (err) {
      const state =
        typeof req.query.state === "string" ? req.query.state : "{}";
      const origin = JSON.parse(state).origin;
      return res.redirect(
        `${origin}/auth/error?message=${encodeURIComponent(err.message)}`
      );
    }

    if (!user) {
      const state =
        typeof req.query.state === "string" ? req.query.state : "{}";
      const origin = JSON.parse(state).origin;
      return res.redirect(`${origin}/auth/denied`);
    }

    if (user.error) {
      return res.redirect(
        `${user.origin}/auth/error?message=${encodeURIComponent(user.message)}`
      );
    }
    // deveriamos redireccionar de otra forma si todo esta bien
    // res.redirect(`${user.origin}auth/success?token=${user.token}`);ç
    console.log(user);
  })(req, res, next);
});

export default UserAuthRoutes;
