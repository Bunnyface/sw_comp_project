FROM python:latest
WORKDIR /app
RUN pip install pytest requests
COPY scala_integration_test.py /app
CMD ["pytest", "scala_integration_test.py"]