FROM --platform=linux/x86_64 nginx:alpine

COPY composeApp/build/dist/wasmJs/productionExecutable /usr/share/nginx/html
#COPY composeApp/build/dist/js/productionExecutable /usr/share/nginx/html

EXPOSE 80/tcp

CMD ["nginx", "-g", "daemon off;"]