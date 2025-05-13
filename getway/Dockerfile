FROM node:22-alpine AS builder

WORKDIR /code
COPY package*.json ./
RUN npm install

COPY . .
COPY .env.dev .env
RUN npm run build

FROM node:22-alpine AS prod
WORKDIR /code

COPY package*.json ./
RUN npm install --only=development

COPY --from=builder /code/dist ./dist
COPY .env.dev .env

RUN mkdir -p /code/logs && \
    chown -R node:node /code/logs

ENV TZ=America/La_Paz
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

USER node

CMD ["npm", "start"]