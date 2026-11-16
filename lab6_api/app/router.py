from typing import List
from fastapi import APIRouter, HTTPException, status
from app.schemas import ApplicationCreate, ApplicationResponse, ErrorResponse
from app.storage import storage

router = APIRouter(
    prefix="/api/v1/applications",
    tags=["Заявления абитуриентов"],
)


@router.get(
    "/",
    response_model=List[ApplicationResponse],
    status_code=status.HTTP_200_OK,
    summary="Получение реестра заявлений абитуриентов",
)
def get_applications():
    return storage.get_all()


@router.get(
    "/{id}",
    response_model=ApplicationResponse,
    status_code=status.HTTP_200_OK,
    responses={
        400: {"model": ErrorResponse, "description": "Некорректный ID заявления"},
        404: {"model": ErrorResponse, "description": "Заявление абитуриента не найдено"},
    },
    summary="Получение детальной информации по заявлению",
)
def get_application(id: int):
    if id <= 0:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Регистрационный номер (ID) должен быть положительным числом",
        )
    application = storage.get_by_id(id)
    if not application:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Заявление с ID {id} не найдено в реестре",
        )
    return application


@router.post(
    "/",
    response_model=ApplicationResponse,
    status_code=status.HTTP_201_CREATED,
    responses={
        400: {
            "model": ErrorResponse,
            "description": "Ошибка валидации или дубликат документа об образовании",
        },
    },
    summary="Регистрация нового заявления абитуриента",
)
def create_application(app_data: ApplicationCreate):
    if storage.exists_by_doc_number(app_data.education_doc_number):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Заявление с таким номером документа об образовании уже зарегистрировано",
        )
    return storage.create(app_data)


@router.delete(
    "/{id}",
    status_code=status.HTTP_200_OK,
    responses={
        400: {"model": ErrorResponse, "description": "Некорректный ID заявления"},
        404: {"model": ErrorResponse, "description": "Заявление абитуриента не найдено"},
    },
    summary="Удаление (отзыв) заявления абитуриента",
)
def delete_application(id: int):
    if id <= 0:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Регистрационный номер (ID) должен быть положительным числом",
        )
    deleted = storage.delete(id)
    if not deleted:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Заявление с ID {id} не найдено",
        )
    return {"detail": f"Заявление с ID {id} успешно удалено из реестра"}
