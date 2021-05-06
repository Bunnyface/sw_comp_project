FROM python:3

WORKDIR /usr/src/app

COPY hello_world/ .

CMD ["test.py"]

ENTRYPOINT ["python3"]
