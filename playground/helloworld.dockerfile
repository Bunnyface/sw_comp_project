FROM ubuntu:latest

WORKDIR /usr/src/app

COPY . .

CMD["test.py"]

ENTRYPOINT ["python3"]
