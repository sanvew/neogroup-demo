FROM ubuntu:latest
LABEL authors="sanvew"

ENTRYPOINT ["top", "-b"]