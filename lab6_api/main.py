from fastapi import FastAPI
from app.router import router as applications_router

app = FastAPI(
    title="Admissions Commission API",
    description="Микросервис приемной комиссии (Предметная область: Прием абитуриентов в УО)",
    version="1.0.0",
    docs_url="/docs",
)

app.include_router(applications_router)
