FROM nginx:1.27-alpine

WORKDIR /usr/share/nginx/html

COPY . /usr/share/nginx/html/

RUN chown -R nginx:nginx /usr/share/nginx/html && \
    apk add --no-cache curl

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
