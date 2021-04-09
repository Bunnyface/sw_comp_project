FROM python:latest
WORKDIR /app
RUN pip install pytest requests asyncpg asyncio
COPY scala_integration_test.py /app
CMD ["pytest", "scala_integration_test.py"]