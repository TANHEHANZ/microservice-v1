FROM node:22-alpine AS builder

WORKDIR /code
COPY package*.json ./
RUN npm install

COPY . .
RUN npm install typescript -g
RUN tsc

FROM node:22-alpine AS prod
WORKDIR /code

COPY package*.json ./
RUN npm install --only=development

COPY --from=builder /code/dist ./dist

ENV TZ=America/La_Paz
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

USER node

CMD ["node", "dist/app.js"]