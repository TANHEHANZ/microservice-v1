export const serviceConfig = {
  auth: {
    url: process.env.AUTH_SERVICE_URL || "http://localhost:3010",
    timeout: 3000,
  },
  approvent: {
    url: process.env.APPROVENT_SERVICE_URL || "http://localhost:3020",
    timeout: 3000,
  },
};
