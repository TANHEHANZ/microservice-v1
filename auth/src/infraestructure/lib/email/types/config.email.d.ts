declare global {
  namespace ConfigEmail {
    interface ConfigEmail {
      host: string;
      port: number;
      secure: boolean;
      auth: {
        user: string;
        pass: string;
      };
    }
  }
}
