from enum import Enum
from typing import Optional
from pydantic import BaseModel, Field


class ApplicationStatus(str, Enum):
    REGISTERED = "Зарегистрировано"
    ADMITTED_TO_EXAMS = "Допущен к испытаниям"
    ENROLLED = "Зачислен"
    REJECTED = "Отклонено"


class ApplicationBase(BaseModel):
    full_name: str = Field(...,
        min_length=5,
        max_length=150,
        description="ФИО абитуриента",
        example="Иванов Иван Иванович",
    )
    education_doc_number: str = Field(
        ...,
        min_length=4,
        max_length=50,
        description="Номер документа об образовании (аттестат/диплом)",
        example="АТ-1234567",
    )
    specialty_code: str = Field(
        ...,
        min_length=2,
        max_length=30,
        description="Код специальности согласно плану приема",
        example="6-05-0611-01",
    )
    total_score: Optional[int] = Field(
        default=0,
        ge=0,
        le=400,
        description="Сумма баллов по сертификатам ЦТ/ЦЭ и аттестату",
        example=315,
    )


class ApplicationCreate(ApplicationBase):
    pass


class ApplicationResponse(ApplicationBase):
    id: int = Field(
        ...,
        description="Уникальный регистрационный номер заявления",
        example=1,
    )
    status: ApplicationStatus = Field(
        default=ApplicationStatus.REGISTERED,
        description="Текущий статус заявления в приемной комиссии",
    )


class ErrorResponse(BaseModel):
    detail: str
