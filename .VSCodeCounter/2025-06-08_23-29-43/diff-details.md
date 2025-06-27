# Diff Details

Date : 2025-06-08 23:29:43

Directory c:\\Users\\mi\\Desktop\\PROYECTOS\\REFACTOR\\auth

Total : 78 files,  459 codes, 82 comments, 68 blanks, all 609 lines

[Summary](results.md) / [Details](details.md) / [Diff Summary](diff.md) / Diff Details

## Files
| filename | language | code | comment | blank | total |
| :--- | :--- | ---: | ---: | ---: | ---: |
| [administration/package.json](/administration/package.json) | JSON | -65 | 0 | -1 | -66 |
| [administration/prisma/migrations/20250516165231_/migration.sql](/administration/prisma/migrations/20250516165231_/migration.sql) | MS SQL | -36 | -7 | -10 | -53 |
| [administration/prisma/migrations/20250516173551_/migration.sql](/administration/prisma/migrations/20250516173551_/migration.sql) | MS SQL | -4 | -10 | -4 | -18 |
| [administration/prisma/migrations/20250602175423_y/migration.sql](/administration/prisma/migrations/20250602175423_y/migration.sql) | MS SQL | -41 | -26 | -20 | -87 |
| [administration/prisma/schema.prisma](/administration/prisma/schema.prisma) | Prisma | -63 | 0 | -10 | -73 |
| [administration/prisma/seeder/index.seeder.ts](/administration/prisma/seeder/index.seeder.ts) | TypeScript | 0 | 0 | -1 | -1 |
| [administration/src/app.ts](/administration/src/app.ts) | TypeScript | -16 | 0 | -4 | -20 |
| [administration/src/infraestructure/CONSTANTS/http.ts](/administration/src/infraestructure/CONSTANTS/http.ts) | TypeScript | -20 | 0 | -3 | -23 |
| [administration/src/infraestructure/CONSTANTS/index.ts](/administration/src/infraestructure/CONSTANTS/index.ts) | TypeScript | -2 | 0 | -1 | -3 |
| [administration/src/infraestructure/CONSTANTS/status.ts](/administration/src/infraestructure/CONSTANTS/status.ts) | TypeScript | -13 | 0 | -1 | -14 |
| [administration/src/infraestructure/config/config.ts](/administration/src/infraestructure/config/config.ts) | TypeScript | -18 | 0 | -3 | -21 |
| [administration/src/infraestructure/config/prisma.client.ts](/administration/src/infraestructure/config/prisma.client.ts) | TypeScript | -12 | 0 | -4 | -16 |
| [administration/src/infraestructure/config/response.ts](/administration/src/infraestructure/config/response.ts) | TypeScript | -87 | 0 | -16 | -103 |
| [administration/src/infraestructure/global.d.ts](/administration/src/infraestructure/global.d.ts) | TypeScript | -9 | 0 | -2 | -11 |
| [administration/src/infraestructure/helpers/controllerHandler.ts](/administration/src/infraestructure/helpers/controllerHandler.ts) | TypeScript | -18 | 0 | -4 | -22 |
| [administration/src/infraestructure/validator/service/s_group.validator.ts](/administration/src/infraestructure/validator/service/s_group.validator.ts) | TypeScript | -8 | 0 | -2 | -10 |
| [administration/src/infraestructure/validator/service/service.model.ts](/administration/src/infraestructure/validator/service/service.model.ts) | TypeScript | -24 | 0 | -5 | -29 |
| [administration/src/modules/myServices/controllers/crud.controller.ts](/administration/src/modules/myServices/controllers/crud.controller.ts) | TypeScript | -28 | 0 | -3 | -31 |
| [administration/src/modules/myServices/controllers/group.controller.ts](/administration/src/modules/myServices/controllers/group.controller.ts) | TypeScript | -26 | -2 | -5 | -33 |
| [administration/src/modules/myServices/myservices.routes.ts](/administration/src/modules/myServices/myservices.routes.ts) | TypeScript | -12 | 0 | -4 | -16 |
| [administration/src/modules/myServices/services/createService.ts](/administration/src/modules/myServices/services/createService.ts) | TypeScript | -62 | 0 | -5 | -67 |
| [administration/src/modules/myServices/services/getService.service.ts](/administration/src/modules/myServices/services/getService.service.ts) | TypeScript | -26 | 0 | -3 | -29 |
| [administration/src/modules/myServices/services/group.service.ts](/administration/src/modules/myServices/services/group.service.ts) | TypeScript | -89 | 0 | -3 | -92 |
| [administration/src/modules/routes.ts](/administration/src/modules/routes.ts) | TypeScript | -5 | 0 | -3 | -8 |
| [administration/src/server.ts](/administration/src/server.ts) | TypeScript | -35 | 0 | -5 | -40 |
| [administration/tsconfig.json](/administration/tsconfig.json) | JSON with Comments | -33 | 0 | -1 | -34 |
| [auth/Dockerfile](/auth/Dockerfile) | Docker | 16 | 0 | 8 | 24 |
| [auth/jest.config.ts](/auth/jest.config.ts) | TypeScript | 11 | 0 | 1 | 12 |
| [auth/jsdoc.json](/auth/jsdoc.json) | JSON | 12 | 0 | 0 | 12 |
| [auth/package.json](/auth/package.json) | JSON | 59 | 0 | 3 | 62 |
| [auth/src/app.ts](/auth/src/app.ts) | TypeScript | 17 | 1 | 3 | 21 |
| [auth/src/infraestructure/CONSTANTS/http.ts](/auth/src/infraestructure/CONSTANTS/http.ts) | TypeScript | 20 | 0 | 3 | 23 |
| [auth/src/infraestructure/CONSTANTS/index.ts](/auth/src/infraestructure/CONSTANTS/index.ts) | TypeScript | 0 | 0 | 1 | 1 |
| [auth/src/infraestructure/CONSTANTS/status.ts](/auth/src/infraestructure/CONSTANTS/status.ts) | TypeScript | 13 | 0 | 1 | 14 |
| [auth/src/infraestructure/config/config.ts](/auth/src/infraestructure/config/config.ts) | TypeScript | 15 | 0 | 1 | 16 |
| [auth/src/infraestructure/config/response.ts](/auth/src/infraestructure/config/response.ts) | TypeScript | 87 | 0 | 16 | 103 |
| [auth/src/infraestructure/global.d.ts](/auth/src/infraestructure/global.d.ts) | TypeScript | 31 | 0 | 2 | 33 |
| [auth/src/infraestructure/helpers/bycript.ts](/auth/src/infraestructure/helpers/bycript.ts) | TypeScript | 11 | 0 | 3 | 14 |
| [auth/src/infraestructure/helpers/encript.ts](/auth/src/infraestructure/helpers/encript.ts) | TypeScript | 43 | 0 | 7 | 50 |
| [auth/src/infraestructure/helpers/getIp.ts](/auth/src/infraestructure/helpers/getIp.ts) | TypeScript | 3 | 0 | 2 | 5 |
| [auth/src/infraestructure/helpers/handler.error.ts](/auth/src/infraestructure/helpers/handler.error.ts) | TypeScript | 49 | 0 | 7 | 56 |
| [auth/src/infraestructure/helpers/jwt.decript.ts](/auth/src/infraestructure/helpers/jwt.decript.ts) | TypeScript | 40 | 0 | 7 | 47 |
| [auth/src/infraestructure/helpers/jwt.ts](/auth/src/infraestructure/helpers/jwt.ts) | TypeScript | 42 | 0 | 6 | 48 |
| [auth/src/infraestructure/helpers/permission-checker.ts](/auth/src/infraestructure/helpers/permission-checker.ts) | TypeScript | 24 | 0 | 3 | 27 |
| [auth/src/infraestructure/lib/email/modules/sendEmail.ts](/auth/src/infraestructure/lib/email/modules/sendEmail.ts) | TypeScript | 17 | 0 | 2 | 19 |
| [auth/src/infraestructure/lib/email/services/conection.email.ts](/auth/src/infraestructure/lib/email/services/conection.email.ts) | TypeScript | 45 | 0 | 6 | 51 |
| [auth/src/infraestructure/lib/email/services/config.service.ts](/auth/src/infraestructure/lib/email/services/config.service.ts) | TypeScript | 17 | 0 | 2 | 19 |
| [auth/src/infraestructure/lib/email/types/config.email.d.ts](/auth/src/infraestructure/lib/email/types/config.email.d.ts) | TypeScript | 13 | 0 | 1 | 14 |
| [auth/src/infraestructure/lib/index.ts](/auth/src/infraestructure/lib/index.ts) | TypeScript | 0 | 0 | 1 | 1 |
| [auth/src/infraestructure/lib/passport/formaterData.ts](/auth/src/infraestructure/lib/passport/formaterData.ts) | TypeScript | 36 | 0 | 4 | 40 |
| [auth/src/infraestructure/lib/passport/passport.config.ts](/auth/src/infraestructure/lib/passport/passport.config.ts) | TypeScript | 6 | 0 | 3 | 9 |
| [auth/src/infraestructure/lib/passport/strategies/google.strategy.ts](/auth/src/infraestructure/lib/passport/strategies/google.strategy.ts) | TypeScript | 18 | 0 | 3 | 21 |
| [auth/src/infraestructure/midlweware/auth.response.ts](/auth/src/infraestructure/midlweware/auth.response.ts) | TypeScript | 26 | 1 | 5 | 32 |
| [auth/src/infraestructure/midlweware/authentication.ts](/auth/src/infraestructure/midlweware/authentication.ts) | TypeScript | 38 | 0 | 3 | 41 |
| [auth/src/infraestructure/midlweware/check-permission.ts](/auth/src/infraestructure/midlweware/check-permission.ts) | TypeScript | 33 | 1 | 4 | 38 |
| [auth/src/infraestructure/midlweware/validated.ts](/auth/src/infraestructure/midlweware/validated.ts) | TypeScript | 25 | 0 | 2 | 27 |
| [auth/src/infraestructure/test/integration/user.test.ts](/auth/src/infraestructure/test/integration/user.test.ts) | TypeScript | 72 | 4 | 18 | 94 |
| [auth/src/infraestructure/test/setup.ts](/auth/src/infraestructure/test/setup.ts) | TypeScript | 6 | 1 | 3 | 10 |
| [auth/src/infraestructure/types/auth.error.type.ts](/auth/src/infraestructure/types/auth.error.type.ts) | TypeScript | 0 | 0 | 1 | 1 |
| [auth/src/infraestructure/types/auth/google.types.ts](/auth/src/infraestructure/types/auth/google.types.ts) | TypeScript | 9 | 0 | 3 | 12 |
| [auth/src/infraestructure/types/internal.types.ts](/auth/src/infraestructure/types/internal.types.ts) | TypeScript | 10 | 0 | 3 | 13 |
| [auth/src/infraestructure/types/jwt.ts](/auth/src/infraestructure/types/jwt.ts) | TypeScript | 14 | 0 | 2 | 16 |
| [auth/src/infraestructure/types/oauth.types.ts](/auth/src/infraestructure/types/oauth.types.ts) | TypeScript | 20 | 0 | 3 | 23 |
| [auth/src/infraestructure/types/openid.types.ts](/auth/src/infraestructure/types/openid.types.ts) | TypeScript | 18 | 0 | 2 | 20 |
| [auth/src/infraestructure/types/responseS.type.ts](/auth/src/infraestructure/types/responseS.type.ts) | TypeScript | 6 | 0 | 1 | 7 |
| [auth/src/modules/authentication/authentication.routes.ts](/auth/src/modules/authentication/authentication.routes.ts) | TypeScript | 20 | 8 | 5 | 33 |
| [auth/src/modules/authentication/google/controllers/handleGoogleAuthCallback.ts](/auth/src/modules/authentication/google/controllers/handleGoogleAuthCallback.ts) | TypeScript | 32 | 0 | 3 | 35 |
| [auth/src/modules/authentication/google/services/validatedAcount.service.ts](/auth/src/modules/authentication/google/services/validatedAcount.service.ts) | TypeScript | 12 | 16 | 2 | 30 |
| [auth/src/modules/client/client.routes.ts](/auth/src/modules/client/client.routes.ts) | TypeScript | 4 | 3 | 4 | 11 |
| [auth/src/modules/client/controller/JWT.client.ts](/auth/src/modules/client/controller/JWT.client.ts) | TypeScript | 0 | 65 | 4 | 69 |
| [auth/src/modules/client/controller/verificated.ts](/auth/src/modules/client/controller/verificated.ts) | TypeScript | 0 | 25 | 5 | 30 |
| [auth/src/modules/routes.ts](/auth/src/modules/routes.ts) | TypeScript | 5 | 0 | 3 | 8 |
| [auth/src/modules/user/controller/logout.ts](/auth/src/modules/user/controller/logout.ts) | TypeScript | 2 | 0 | 2 | 4 |
| [auth/src/modules/user/controller/refresh.ts](/auth/src/modules/user/controller/refresh.ts) | TypeScript | 0 | 0 | 1 | 1 |
| [auth/src/modules/user/user.routes.ts](/auth/src/modules/user/user.routes.ts) | TypeScript | 38 | 2 | 7 | 47 |
| [auth/src/public/templates/emails/verification.html](/auth/src/public/templates/emails/verification.html) | HTML | 102 | 0 | 3 | 105 |
| [auth/src/server.ts](/auth/src/server.ts) | TypeScript | 39 | 0 | 5 | 44 |
| [auth/tsconfig.json](/auth/tsconfig.json) | JSON with Comments | 35 | 0 | 1 | 36 |

[Summary](results.md) / [Details](details.md) / [Diff Summary](diff.md) / Diff Details